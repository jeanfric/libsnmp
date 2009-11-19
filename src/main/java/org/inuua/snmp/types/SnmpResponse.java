package org.inuua.snmp.types;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.ErrorStatus;
import org.inuua.snmp.SnmpPdu;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericSequence;

public final class SnmpResponse implements SnmpPdu {

    public static SnmpResponse newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpResponse(tlv);
    }

    public static SnmpResponse newFromList(List<SnmpVariable<?>> sequence) {
        return new SnmpResponse(sequence);
    }
    private SnmpGenericSequence gn;

    private SnmpResponse(EncodedSnmpVariable tlv) {
        this.gn = SnmpGenericSequence.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    private SnmpResponse(List<SnmpVariable<?>> list) {
        this.gn = SnmpGenericSequence.newOfTypeAndValue(this.getSnmpType(), list);
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gn.encode();
    }

    public Integer getErrorIndex() {
        return ((BigInteger) this.gn.getVariableOfExpectedTypeAt(SnmpType.INTEGER, 1).getValue()).intValue();
    }

    public ErrorStatus getErrorStatusId() {
        return ErrorStatus.valueOf(((BigInteger) this.gn.getVariableOfExpectedTypeAt(SnmpType.INTEGER, 2).getValue()).intValue());
    }

    @Override
    public Integer getRequestId() {
        return ((BigInteger) this.gn.getVariableOfExpectedTypeAt(SnmpType.INTEGER, 0).getValue()).intValue();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.RESPONSE;
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

    @Override
    public Map<SnmpObjectIdentifier, SnmpVariable<?>> getVariableBindings() {
        @SuppressWarnings("unchecked")
        List<SnmpVariable<?>> lv = (List<SnmpVariable<?>>) this.gn.getVariableOfExpectedTypeAt(SnmpType.SEQUENCE, 3).getValue();
        return SnmpGenericSequence.newOfTypeAndValue(SnmpType.SEQUENCE, lv).asVariableBindingsMap();

    }

    @Override
    public String toString() {
        return this.getValueAsString();
    }
}
