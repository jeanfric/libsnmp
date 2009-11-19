package org.inuua.snmp.types.helpers;

import java.util.List;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.util.ListHelpers;

public final class SnmpGenericByteString {

    public static SnmpGenericByteString newFromEncodedSnmpVariable(SnmpType t, EncodedSnmpVariable tlv) {
        if (tlv.getSnmpType() != t) {
            throw new Error("The type specified in the encoded variable is not the expected type");
        }
        return new SnmpGenericByteString(t, tlv);
    }

    public static SnmpGenericByteString newOfTypeAndValue(SnmpType type, byte[] value) {
        return new SnmpGenericByteString(type, value);
    }
    private final byte[] value;
    private final SnmpType snmpType;

    private SnmpGenericByteString(SnmpType type, byte[] value) {
        this.snmpType = type;
        this.value = value.clone();
    }

    private SnmpGenericByteString(SnmpType type, EncodedSnmpVariable tlv) {
        this.snmpType = type;
        byte[] b = tlv.getValueAsByteArray();
        this.value = b.clone();
    }

    public EncodedSnmpVariable encode() {
        return EncodedSnmpVariable.newFromTypeAndValue(this.snmpType, this.value);
    }

    public String getTypeNameAsString() {
        return this.snmpType.toString();
    }

    public List<Byte> getValue() {
        return ListHelpers.toListOfBytes(this.value);
    }

    public String getValueAsString() {
        return this.value.toString();
    }
}
