package org.inuua.snmp.types;

import java.util.ArrayList;
import java.util.List;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericByteString;
import org.inuua.util.ListHelpers;

public final class SnmpOctetString implements SnmpVariable<List<Byte>> {

    private static final int MAX_SHOWABLE_HEX_BYTES = -1; // 16

    public static SnmpOctetString newFromByteList(List<Byte> byteList) {
        return new SnmpOctetString(ListHelpers.toPrimitiveByteArray(byteList));
    }

    public static SnmpOctetString newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpOctetString(tlv);
    }

    public static SnmpOctetString newFromString(String string) {
        return new SnmpOctetString(string.getBytes());
    }
    private final SnmpGenericByteString gb;

    private SnmpOctetString(byte[] byteList) {
        this.gb = SnmpGenericByteString.newOfTypeAndValue(this.getSnmpType(), byteList);
    }

    private SnmpOctetString(EncodedSnmpVariable tlv) {
        this.gb = SnmpGenericByteString.newOfTypeAndValue(this.getSnmpType(), tlv.getValueAsByteArray());
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gb.encode();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.OCTET_STRING;
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
        String ret = new String(ListHelpers.toPrimitiveByteArray(this.gb.getValue()));
        if (this.gb.getValue().size() <= MAX_SHOWABLE_HEX_BYTES) {
            // Each hex is 2 letters, with one ":" for each, and with the "|"
            // This is a rough initial capacity estimation.
            StringBuilder b = new StringBuilder(ret.length() + (MAX_SHOWABLE_HEX_BYTES * 3) + 4);
            b.append(":");
            for (Byte by : this.gb.getValue()) {
                b.append(String.format("%x:", by));
            }
            return ret + "|" + b.toString();
        }

        return ret;
    }

    @Override
    public String toString() {
        return this.getValueAsString();
    }
}
