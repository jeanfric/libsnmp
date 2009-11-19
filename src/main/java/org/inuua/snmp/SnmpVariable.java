package org.inuua.snmp;

public interface SnmpVariable<T> {

    EncodedSnmpVariable encode();

    SnmpType getSnmpType();

    String getTypeNameAsString();

    T getValue();

    String getValueAsString();
}
