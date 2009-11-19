package org.inuua.snmp.types;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpPdu;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericSequence;

public final class SnmpInformRequest implements SnmpPdu {

    public static SnmpInformRequest newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) throws IOException {
        return new SnmpInformRequest(tlv);
    }

    public static SnmpInformRequest newFromList(List<SnmpVariable<?>> sequence) {
        return new SnmpInformRequest(sequence);
    }
    private SnmpGenericSequence gn;

    private SnmpInformRequest(EncodedSnmpVariable tlv) throws IOException {
        this.gn = SnmpGenericSequence.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    private SnmpInformRequest(List<SnmpVariable<?>> list) {
        this.gn = SnmpGenericSequence.newOfTypeAndValue(this.getSnmpType(), list);
    }

    @Override
    public EncodedSnmpVariable encode() throws IOException {
        return this.gn.encode();
    }

    @Override
    public Integer getRequestId() {
        return ((BigInteger) this.gn.getVariableOfExpectedTypeAt(SnmpType.INTEGER, 0).getValue()).intValue();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.INFORM_REQUEST;
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
