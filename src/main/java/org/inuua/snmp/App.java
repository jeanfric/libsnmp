package org.inuua.snmp;

import org.inuua.snmp.types.SnmpObjectIdentifier;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

public final class App {

    private App() {
        // private ctor;
    }

    public static void main(String[] args) throws SocketException, UnknownHostException, IOException, InterruptedException {

        SnmpConnection c = BlockingSnmpConnection.newConnection(SnmpVersion.V1, "public", InetAddress.getByName("localhost"));
        try {
            c.registerIncomingVariableBindingsHandler(new IncomingVariableBindingsHandler() {

                @Override
                public void handleIncomingVariableBindings(Map<SnmpObjectIdentifier, SnmpVariable<?>> map) {
                    System.out.println(map);
                }
            });

            c.retrieveAllObjectsStartingFrom("0");
        } finally {
            c.close();
        }
    }
}
