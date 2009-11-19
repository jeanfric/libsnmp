package org.inuua.snmp;

import java.util.List;
import java.util.Map;

import org.inuua.snmp.types.SnmpObjectIdentifier;

public interface SnmpPdu extends SnmpVariable<List<SnmpVariable<?>>> {

    Integer getRequestId();

    Map<SnmpObjectIdentifier, SnmpVariable<?>> getVariableBindings();
}
