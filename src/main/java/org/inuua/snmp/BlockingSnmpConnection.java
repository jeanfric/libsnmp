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

public final class BlockingSnmpConnection implements SnmpConnection {

    private static final Integer PACKET_BUFFER_SIZE = 4096;
    private Integer requestId = 0;
    private final InetAddress hostAddress;
    private final Integer hostPort;
    private final SnmpVersion version;
    private final String community;
    private final DatagramSocket socket;
    private final Set<IncomingSnmpMessageHandler> subscribers = new HashSet<IncomingSnmpMessageHandler>();
    private final Set<IncomingVariableBindingsHandler> mibSubscribers = new HashSet<IncomingVariableBindingsHandler>();
    private final Set<IOExceptionHandler> exceptionSubscribers = new HashSet<IOExceptionHandler>();

    public static BlockingSnmpConnection newConnection(SnmpVersion version, String community, InetAddress hostAddress)
            throws SocketException {
        return new BlockingSnmpConnection(version, community, hostAddress);
    }

    public static BlockingSnmpConnection newConnection(SnmpVersion version, String community, InetAddress hostAddress,
            Integer hostPort) throws SocketException {
        return new BlockingSnmpConnection(version, community, hostAddress, hostPort);
    }

    private BlockingSnmpConnection(SnmpVersion version, String community, InetAddress hostAddress)
            throws SocketException {
        this.version = version;
        this.community = community;
        this.hostAddress = hostAddress;
        this.hostPort = SnmpPort.STANDARD_PORT.portNumber();
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(10000); // 10 seconds
    }

    @Override
    public void close() {
        this.socket.close();
    }

    private BlockingSnmpConnection(SnmpVersion version, String community, InetAddress hostAddress, Integer hostPort)
            throws SocketException {
        this.version = version;
        this.community = community;
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(10000); // 10 seconds
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
    public void retrieveAllObjectsStartingFrom(String firstMib) {
        SnmpMessage reply = null;
        String currentMib = firstMib;
        do {
            try {
                Map<SnmpObjectIdentifier, SnmpVariable<?>> m = new HashMap<SnmpObjectIdentifier, SnmpVariable<?>>();
                m.put(SnmpObjectIdentifier.newFromString(currentMib), SnmpNull.newNull());
                SnmpGetNextRequest getReq = SnmpGetNextRequest.newFromDefinition(this.getNewRequestId(),
                        ErrorStatus.NO_ERROR, 0, m);
                SnmpMessage msg = SnmpMessage.newFromDefinition(this.version, this.community, getReq);
                reply = this.sendAndReceiveBlocking(msg);
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

    @Override
    public void retrieveOneObject(String mib) {
        try {
            Map<SnmpObjectIdentifier, SnmpVariable<?>> m = new HashMap<SnmpObjectIdentifier, SnmpVariable<?>>();
            m.put(SnmpObjectIdentifier.newFromString(mib), SnmpNull.newNull());
            SnmpGetRequest getReq = SnmpGetRequest.newFromDefinition(this.getNewRequestId(), ErrorStatus.NO_ERROR, 0, m);
            SnmpMessage msg = SnmpMessage.newFromDefinition(this.version, this.community, getReq);
            SnmpMessage reply = this.sendAndReceiveBlocking(msg);
            for (IncomingSnmpMessageHandler sub : this.subscribers) {
                sub.handleIncomingSnmpMessage(reply);
            }
            if (reply.getPdu() instanceof SnmpResponse) {
                for (IncomingVariableBindingsHandler han : this.mibSubscribers) {
                    han.handleIncomingVariableBindings(reply.getPdu().getVariableBindings());
                }
            } else {
                throw new Error("Unexpected reply:" + reply.toString());
            }
        } catch (IOException ex) {
            for (IOExceptionHandler h : this.exceptionSubscribers) {
                h.handleIOException(ex);
            }
        }
    }

    private SnmpMessage sendAndReceiveBlocking(SnmpMessage message) throws IOException {
        Integer currReqId = message.getPdu().getRequestId();
        byte[] outB = message.encode().toByteArray();
        DatagramPacket out = new DatagramPacket(outB, outB.length, this.hostAddress, this.hostPort);
        DatagramPacket in = new DatagramPacket(new byte[PACKET_BUFFER_SIZE], PACKET_BUFFER_SIZE);
        this.socket.send(out);
        this.socket.receive(in);
        byte[] inB = in.getData();

        SnmpMessage reply = SnmpMessage.newFromEncodedSnmpVariable(EncodedSnmpVariable.newFromByteArray(inB));
        // SnmpMessage reply =
        // SnmpMessage.newFromEncodedSnmpVariable(EncodedSnmpVariable.newFromListOfBytes(ListHelpers.toListOfBytes(inB)));
        if ((int) currReqId != (int) reply.getPdu().getRequestId()) {
            throw new Error(String.format(
                    "We did not receive an SNMP message with the right request ID.  We sent %d, and received %d",
                    currReqId, reply.getPdu().getRequestId()));
        }
        return reply;
    }

    @Override
    public void sendSnmpMessage(SnmpMessage message) {
        try {
            SnmpMessage m = this.sendAndReceiveBlocking(message);
            for (IncomingSnmpMessageHandler sub : this.subscribers) {
                sub.handleIncomingSnmpMessage(m);
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
