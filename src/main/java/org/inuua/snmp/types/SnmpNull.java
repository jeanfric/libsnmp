package org.inuua.snmp.types;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;

public final class SnmpNull implements SnmpVariable<Object> {

    public static SnmpNull newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpNull(tlv);
    }

    public static SnmpNull newNull() {
        return new SnmpNull();
    }

    private SnmpNull() {
    }

    private SnmpNull(EncodedSnmpVariable tlv) {
    }

    @Override
    public EncodedSnmpVariable encode() {
        byte[] b = new byte[0];
        return EncodedSnmpVariable.newFromTypeAndValue(SnmpType.NULL, b);
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.NULL;
    }

    @Override
    public String getTypeNameAsString() {
        return SnmpType.NULL.toString();
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String getValueAsString() {
        return "null";
    }

    @Override
    public String toString() {
        return this.getValueAsString();
    }
}
