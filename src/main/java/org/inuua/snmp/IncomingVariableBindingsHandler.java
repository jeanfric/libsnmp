package org.inuua.snmp;

import java.util.Map;

import org.inuua.snmp.types.SnmpObjectIdentifier;

public interface IncomingVariableBindingsHandler {

    void handleIncomingVariableBindings(Map<SnmpObjectIdentifier, SnmpVariable<?>> map);
}
