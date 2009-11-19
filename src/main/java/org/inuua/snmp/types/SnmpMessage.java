package org.inuua.snmp.types;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpPdu;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.SnmpVersion;
import org.inuua.snmp.types.helpers.SnmpGenericSequence;
import org.inuua.util.ListHelpers;

public final class SnmpMessage implements SnmpVariable<List<SnmpVariable<?>>> {

    public static SnmpMessage newFromDefinition(SnmpVersion version, String community, SnmpPdu pdu) {
        SnmpInteger ver = SnmpInteger.newFromNumber(version.tag());
        SnmpOctetString com = SnmpOctetString.newFromString(community);

        List<SnmpVariable<?>> l = new ArrayList<SnmpVariable<?>>();
        l.add(ver);
        l.add(com);
        l.add(pdu);

        return SnmpMessage.newFromList(l);
    }

    public static SnmpMessage newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpMessage(tlv);
    }

    public static SnmpMessage newFromList(List<SnmpVariable<?>> sequence) {
        return new SnmpMessage(sequence);
    }
    private SnmpGenericSequence gn;

    private SnmpMessage(EncodedSnmpVariable tlv) {
        this.gn = SnmpGenericSequence.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    private SnmpMessage(List<SnmpVariable<?>> list) {
        this.gn = SnmpGenericSequence.newOfTypeAndValue(this.getSnmpType(), list);
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gn.encode();
    }

    public String getCommunity() {
        @SuppressWarnings("unchecked")
        List<Byte> lb = (List<Byte>) this.gn.getVariableOfExpectedTypeAt(SnmpType.OCTET_STRING, 1).getValue();
        return new String(ListHelpers.toPrimitiveByteArray(lb));
    }

    public SnmpPdu getPdu() {
        return (SnmpPdu) this.gn.getVariableOfAnyExpectedTypeAt(new SnmpType[]{SnmpType.GET_REQUEST,
                    SnmpType.GET_NEXT_REQUEST, SnmpType.GET_BULK_REQUEST, SnmpType.INFORM_REQUEST, SnmpType.RESPONSE,
                    SnmpType.TRAP_V2,}, 2);
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.SEQUENCE;
    }

    @Override
    public String getTypeNameAsString() {
        return this.gn.getTypeNameAsString();
    }

    @Override
    public List<SnmpVariable<?>> getValue() {
        return this.gn.getValue();
    }

    @Override
    public String getValueAsString() {
        return this.gn.getValueAsString();
    }

    public Integer getVersion() {
        return ((BigInteger) this.gn.getVariableOfExpectedTypeAt(SnmpType.INTEGER, 0).getValue()).intValue();
    }

    @Override
    public String toString() {
        return this.getValueAsString();
    }
}
