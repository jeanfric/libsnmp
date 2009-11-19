package org.inuua.snmp.types;

import java.util.List;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericByteString;
import org.inuua.util.ListHelpers;

public final class SnmpNsapAddress implements SnmpVariable<List<Byte>> {

    public static SnmpNsapAddress newFromByteList(List<Byte> byteList) {
        return new SnmpNsapAddress(ListHelpers.toPrimitiveByteArray(byteList));
    }

    public static SnmpNsapAddress newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpNsapAddress(tlv);
    }
    private final SnmpGenericByteString gb;

    private SnmpNsapAddress(byte[] byteList) {
        this.gb = SnmpGenericByteString.newOfTypeAndValue(this.getSnmpType(), byteList);
    }

    private SnmpNsapAddress(EncodedSnmpVariable tlv) {
        this.gb = SnmpGenericByteString.newOfTypeAndValue(this.getSnmpType(), tlv.getValueAsByteArray());
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gb.encode();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.NSAP_ADDRESS;
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
