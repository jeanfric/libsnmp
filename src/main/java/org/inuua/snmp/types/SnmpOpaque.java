package org.inuua.snmp.types;

import java.util.List;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericByteString;
import org.inuua.util.ListHelpers;

public final class SnmpOpaque implements SnmpVariable<List<Byte>> {

    public static SnmpOpaque newFromByteList(List<Byte> byteList) {
        return new SnmpOpaque(ListHelpers.toPrimitiveByteArray(byteList));
    }

    public static SnmpOpaque newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpOpaque(tlv);
    }
    private final SnmpGenericByteString gb;

    private SnmpOpaque(byte[] byteList) {
        this.gb = SnmpGenericByteString.newOfTypeAndValue(this.getSnmpType(), byteList);
    }

    private SnmpOpaque(EncodedSnmpVariable tlv) {
        this.gb = SnmpGenericByteString.newOfTypeAndValue(this.getSnmpType(), tlv.getValueAsByteArray());
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gb.encode();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.OPAQUE;
    }

    @Override
    public String getTypeNameAsString() {
        return this.gb.getTypeNameAsString();
    }

    @Override
    public List<Byte> getValue() {
        return this.gb.getValue();
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
