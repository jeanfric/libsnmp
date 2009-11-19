package org.inuua.snmp;

import org.inuua.snmp.types.SnmpMessage;

public interface IncomingSnmpMessageHandler {

    void handleIncomingSnmpMessage(SnmpMessage msg);
}
