package org.inuua.snmp.types.helpers;

import java.math.BigInteger;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;

public final class SnmpGenericNumber {

    public static SnmpGenericNumber newFromEncodedSnmpVariable(SnmpType t, EncodedSnmpVariable tlv) {
        if (tlv.getSnmpType() != t) {
            throw new Error("The type specified in the encoded variable is not the expected type");
        }
        return new SnmpGenericNumber(tlv);
    }

    public static SnmpGenericNumber newOfTypeAndValue(SnmpType type, BigInteger value) {
        return new SnmpGenericNumber(type, value);
    }
    private final BigInteger value;
    private final SnmpType snmpType;

    protected SnmpGenericNumber(EncodedSnmpVariable tlv) {
        this.value = new BigInteger(tlv.getValueAsByteArray());
        this.snmpType = tlv.getSnmpType();
    }

    protected SnmpGenericNumber(SnmpType type, BigInteger value) {
        this.snmpType = type;
        this.value = value;
    }

    public EncodedSnmpVariable encode() {
        return EncodedSnmpVariable.newFromTypeAndValue(this.snmpType, this.value.toByteArray());
    }

    public String getTypeNameAsString() {
        return this.snmpType.toString();
    }

    public BigInteger getValue() {
        return this.value;
    }

    public String getValueAsString() {
        return this.value.toString();
    }
}
