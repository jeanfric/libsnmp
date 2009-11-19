package org.inuua.snmp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.inuua.snmp.types.SnmpGetNextRequest;
import org.inuua.snmp.types.SnmpGetRequest;
import org.inuua.snmp.types.SnmpMessage;
import org.inuua.snmp.types.SnmpNull;
import org.inuua.snmp.types.SnmpObjectIdentifier;
import org.inuua.snmp.types.SnmpResponse;

public final class NonBlockingSnmpConnection implements SnmpConnection {

    private final Set<IncomingSnmpMessageHandler> subscribers = new HashSet<IncomingSnmpMessageHandler>();
    private final Set<IncomingVariableBindingsHandler> mibSubscribers = new HashSet<IncomingVariableBindingsHandler>();
    private final Set<IOExceptionHandler> exceptionSubscribers = new HashSet<IOExceptionHandler>();
    private final InetAddress hostAddress;
    private final Integer hostPort;
    private final SnmpVersion version;
    private final String community;
    private Integer requestId = 0;

    private static class NonBlockingMessageSender implements Runnable {

        private final Set<IncomingSnmpMessageHandler> subscribers;
        private final Set<IncomingVariableBindingsHandler> mibSubscribers;
        private final Set<IOExceptionHandler> exceptionSubscribers;
        private static final Integer PACKET_BUFFER_SIZE = 4096;
        private final DatagramSocket socket;
        private final SnmpMessage message;
        private final InetAddress hostAddress;
        private final Integer hostPort;

        public NonBlockingMessageSender(InetAddress hostAddress, Integer hostPort, SnmpMessage msg,
                Set<IncomingSnmpMessageHandler> msgHandlers, Set<IncomingVariableBindingsHandler> bindingHandlers,
                Set<IOExceptionHandler> exceptionHandlers) throws SocketException {
            this.message = msg;
            this.exceptionSubscribers = exceptionHandlers;
            this.mibSubscribers = bindingHandlers;
            this.subscribers = msgHandlers;
            this.hostAddress = hostAddress;
            this.hostPort = hostPort;
            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(10000); // 10 secs
        }

        public void close() {
            if (socket != null) {
                this.socket.close();
            }
        }

        @Override
        public void run() {
            try {
                SnmpMessage reply = this.sendAndReceive(this.message);
                for (IncomingSnmpMessageHandler sub : this.subscribers) {
                    sub.handleIncomingSnmpMessage(reply);
                }
                for (IncomingVariableBindingsHandler han : this.mibSubscribers) {
                    han.handleIncomingVariableBindings(reply.getPdu().getVariableBindings());
                }
            } catch (IOException ex) {
                for (IOExceptionHandler h : this.exceptionSubscribers) {
                    h.handleIOException(ex);
                }
            }
        }

        private SnmpMessage sendAndReceive(SnmpMessage message) throws IOException {
            Integer currReqId = message.getPdu().getRequestId();
            byte[] outB = message.encode().toByteArray();
            DatagramPacket out = new DatagramPacket(outB, outB.length, this.hostAddress, this.hostPort);
            DatagramPacket in = new DatagramPacket(new byte[PACKET_BUFFER_SIZE], PACKET_BUFFER_SIZE);
            this.socket.send(out);
            this.socket.receive(in);
            byte[] inB = in.getData();

            SnmpMessage reply = SnmpMessage.newFromEncodedSnmpVariable(EncodedSnmpVariable.newFromByteArray(inB));
            if ((int) currReqId != (int) reply.getPdu().getRequestId()) {
                throw new Error(String.format(
                        "We did not receive an SNMP message with the right request ID.  We sent %d, and received %d",
                        currReqId, reply.getPdu().getRequestId()));
            }
            return reply;
        }
    }

    private static class NonBlockingTreeWalker implements Runnable {

        private final Set<IncomingSnmpMessageHandler> subscribers;
        private final Set<IncomingVariableBindingsHandler> mibSubscribers;
        private final Set<IOExceptionHandler> exceptionSubscribers;
        private final SnmpVersion version;
        private final String community;
        private final InetAddress hostAddress;
        private final Integer hostPort;
        private final String firstMib;
        private static final Integer PACKET_BUFFER_SIZE = 4096;
        private final DatagramSocket socket = new DatagramSocket();
        private Integer requestId = 0;

        public NonBlockingTreeWalker(SnmpVersion version, String community, InetAddress hostAddress, Integer hostPort,
                String firstMib, Set<IncomingSnmpMessageHandler> msgHandlers,
                Set<IncomingVariableBindingsHandler> bindingHandlers, Set<IOExceptionHandler> exceptionHandlers)
                throws SocketException {
            this.version = version;
            this.community = community;
            this.firstMib = firstMib;
            this.hostAddress = hostAddress;
            this.hostPort = hostPort;
            this.exceptionSubscribers = exceptionHandlers;
            this.mibSubscribers = bindingHandlers;
            this.subscribers = msgHandlers;
        }

        private Integer getNewRequestId() {
            this.requestId++;
            return this.requestId;
        }

        public void close() {
            if (this.socket != null) {
                this.socket.close();
            }
        }

        @Override
        public void run() {
            SnmpMessage reply = null;
            String currentMib = this.firstMib;
            do {
                try {
                    Map<SnmpObjectIdentifier, SnmpVariable<?>> m = new HashMap<SnmpObjectIdentifier, SnmpVariable<?>>();
                    m.put(SnmpObjectIdentifier.newFromString(currentMib), SnmpNull.newNull());
                    SnmpGetNextRequest getReq = SnmpGetNextRequest.newFromDefinition(this.getNewRequestId(),
                            ErrorStatus.NO_ERROR, 0, m);
                    SnmpMessage msg = SnmpMessage.newFromDefinition(this.version, this.community, getReq);
                    reply = this.sendAndReceive(msg);
                    if (!(reply.getPdu() instanceof SnmpResponse)) {
                        throw new Error("Unexpected reply:" + reply.toString());
                    }
                    SnmpResponse resp = (SnmpResponse) reply.getPdu();
                    if (resp.getErrorStatusId() != ErrorStatus.NO_ERROR) {
                        break;
                    }
                    SnmpObjectIdentifier obj = null;
                    for (Entry<SnmpObjectIdentifier, SnmpVariable<?>> e : resp.getVariableBindings().entrySet()) {
                        if (e.getKey() instanceof SnmpObjectIdentifier) {
                            obj = e.getKey();
                        } else {
                            throw new Error("Unexpected first element of the first variable binding: " + e.getKey());
                        }
                        break;
                    }
                    if (obj.getDottedRepresentation().equals(currentMib)) {
                        break;
                    }
                    for (IncomingSnmpMessageHandler sub : this.subscribers) {
                        sub.handleIncomingSnmpMessage(reply);
                    }
                    for (IncomingVariableBindingsHandler han : this.mibSubscribers) {
                        han.handleIncomingVariableBindings(reply.getPdu().getVariableBindings());
                    }
                    currentMib = obj.getDottedRepresentation();
                } catch (IOException ex) {
                    for (IOExceptionHandler h : this.exceptionSubscribers) {
                        h.handleIOException(ex);
                    }
                }
            } while (true);
        }

        private SnmpMessage sendAndReceive(SnmpMessage message) throws IOException {
            Integer currReqId = message.getPdu().getRequestId();
            byte[] outB = message.encode().toByteArray();
            DatagramPacket out = new DatagramPacket(outB, outB.length, this.hostAddress, this.hostPort);
            DatagramPacket in = new DatagramPacket(new byte[PACKET_BUFFER_SIZE], PACKET_BUFFER_SIZE);
            this.socket.send(out);
            this.socket.receive(in);
            byte[] inB = in.getData();

            SnmpMessage reply = SnmpMessage.newFromEncodedSnmpVariable(EncodedSnmpVariable.newFromByteArray(inB));
            if ((int) currReqId != (int) reply.getPdu().getRequestId()) {
                throw new Error(String.format(
                        "We did not receive an SNMP message with the right request ID.  We sent %d, and received %d",
                        currReqId, reply.getPdu().getRequestId()));
            }
            return reply;
        }
    }

    @Override
    public void close() {
    }

    public static NonBlockingSnmpConnection newConnection(SnmpVersion version, String community, InetAddress hostAddress)
            throws SocketException {
        return new NonBlockingSnmpConnection(version, community, hostAddress);
    }

    public static NonBlockingSnmpConnection newConnection(SnmpVersion version, String community,
            InetAddress hostAddress, Integer hostPort) throws SocketException {
        return new NonBlockingSnmpConnection(version, community, hostAddress, hostPort);
    }

    private NonBlockingSnmpConnection(SnmpVersion version, String community, InetAddress hostAddress)
            throws SocketException {
        this.version = version;
        this.community = community;
        this.hostAddress = hostAddress;
        this.hostPort = SnmpPort.STANDARD_PORT.portNumber();
    }

    private NonBlockingSnmpConnection(SnmpVersion version, String community, InetAddress hostAddress, Integer hostPort)
            throws SocketException {
        this.version = version;
        this.community = community;
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
    }

    private Integer getNewRequestId() {
        this.requestId++;
        return this.requestId;
    }

    @Override
    public void registerIncomingSnmpMessageHandler(IncomingSnmpMessageHandler ep) {
        this.subscribers.add(ep);
    }

    @Override
    public void registerIncomingVariableBindingsHandler(IncomingVariableBindingsHandler mibMapHandler) {
        this.mibSubscribers.add(mibMapHandler);
    }

    @Override
    public void registerIOExceptionHandler(IOExceptionHandler exceptionHandler) {
        this.exceptionSubscribers.add(exceptionHandler);
    }

    @Override
    public void retrieveAllObjectsStartingFrom(String objectIdentifier) {
        try {
            NonBlockingTreeWalker tw = new NonBlockingTreeWalker(this.version, this.community, this.hostAddress, this.hostPort,
                    objectIdentifier, this.subscribers, this.mibSubscribers, this.exceptionSubscribers);
            try {
                new Thread().start();
            } finally {
                tw.close();
            }
        } catch (IOException ex) {
            for (IOExceptionHandler h : this.exceptionSubscribers) {
                h.handleIOException(ex);
            }
        }
    }

    @Override
    public void retrieveOneObject(String objectIdentifier) {
        Map<SnmpObjectIdentifier, SnmpVariable<?>> m = new HashMap<SnmpObjectIdentifier, SnmpVariable<?>>();
        m.put(SnmpObjectIdentifier.newFromString(objectIdentifier), SnmpNull.newNull());
        SnmpGetRequest getReq = SnmpGetRequest.newFromDefinition(this.getNewRequestId(), ErrorStatus.NO_ERROR, 0, m);
        SnmpMessage msg = SnmpMessage.newFromDefinition(this.version, this.community, getReq);

        this.sendSnmpMessage(msg);
    }

    @Override
    public void sendSnmpMessage(SnmpMessage msg) {
        try {
            NonBlockingMessageSender ms = new NonBlockingMessageSender(this.hostAddress, this.hostPort, msg, this.subscribers,
                    this.mibSubscribers, this.exceptionSubscribers);
            try {
                new Thread(ms).start();
            } finally {
                ms.close();
            }
        } catch (IOException ex) {
            for (IOExceptionHandler h : this.exceptionSubscribers) {
                h.handleIOException(ex);
            }
        }
    }

    @Override
    public void unRegisterIncomingSnmpMessageHandler(IncomingSnmpMessageHandler ep) {
        this.subscribers.remove(ep);
    }

    @Override
    public void unRegisterIncomingVariableBindingsHandler(IncomingVariableBindingsHandler mibMapHandler) {
        this.mibSubscribers.remove(mibMapHandler);
    }

    @Override
    public void unRegisterIOExceptionHandler(IOExceptionHandler exceptionHandler) {
        this.exceptionSubscribers.remove(exceptionHandler);
    }
}
