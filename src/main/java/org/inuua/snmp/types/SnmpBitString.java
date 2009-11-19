package org.inuua.snmp.types;

import java.util.ArrayList;
import java.util.List;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericByteString;
import org.inuua.util.ListHelpers;

public final class SnmpBitString implements SnmpVariable<List<Byte>> {

    public static SnmpBitString newFromByteList(List<Byte> byteList) {
        return new SnmpBitString(ListHelpers.toPrimitiveByteArray(byteList));
    }

    public static SnmpBitString newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpBitString(tlv);
    }
    private final SnmpGenericByteString gb;

    private SnmpBitString(byte[] byteList) {
        this.gb = SnmpGenericByteString.newOfTypeAndValue(this.getSnmpType(), byteList);
    }

    private SnmpBitString(EncodedSnmpVariable tlv) {
        this.gb = SnmpGenericByteString.newOfTypeAndValue(this.getSnmpType(), tlv.getValueAsByteArray());
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gb.encode();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.BIT_STRING;
    }

    @Override
    public String getTypeNameAsString() {
        return this.gb.getTypeNameAsString();
    }

    @Override
    public List<Byte> getValue() {
        return new ArrayList<Byte>(this.gb.getValue());
    }

    @Override
    public String getValueAsString() {
        return this.gb.getValueAsString();
    }

    @Override
    public String toString() {
        return this.getValueAsString();
    }
}
