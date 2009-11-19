package org.inuua.snmp;

import java.io.IOException;

public interface SnmpVariable<T> {

    EncodedSnmpVariable encode() throws IOException;

    SnmpType getSnmpType();

    String getTypeNameAsString();

    T getValue();

    String getValueAsString();
}
