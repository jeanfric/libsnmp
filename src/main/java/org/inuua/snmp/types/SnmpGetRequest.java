package org.inuua.snmp.types;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.ErrorStatus;
import org.inuua.snmp.SnmpPdu;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericSequence;

public final class SnmpGetRequest implements SnmpPdu {

    public static SnmpGetRequest newFromDefinition(Integer requestId, ErrorStatus errorStatus, Integer errorIndex,
            Map<SnmpObjectIdentifier, SnmpVariable<?>> varBindings) {
        SnmpInteger reqId = SnmpInteger.newFromNumber(requestId);
        SnmpInteger errStatus = SnmpInteger.newFromNumber(errorStatus.tag());
        SnmpInteger errIdx = SnmpInteger.newFromNumber(errorIndex);

        List<SnmpVariable<?>> s = new ArrayList<SnmpVariable<?>>();
        s.add(reqId);
        s.add(errStatus);
        s.add(errIdx);

        List<SnmpVariable<?>> vbs = new ArrayList<SnmpVariable<?>>();
        List<SnmpVariable<?>> vb;

        for (Entry<SnmpObjectIdentifier, SnmpVariable<?>> e : varBindings.entrySet()) {
            vb = new ArrayList<SnmpVariable<?>>();
            vb.add(e.getKey());
            vb.add(e.getValue());
            vbs.add(SnmpSequence.newFromList(vb));
        }

        s.add(SnmpSequence.newFromList(vbs));
        return SnmpGetRequest.newFromList(s);
    }

    public static SnmpGetRequest newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) throws IOException {
        return new SnmpGetRequest(tlv);
    }

    public static SnmpGetRequest newFromList(List<SnmpVariable<?>> sequence) {
        return new SnmpGetRequest(sequence);
    }
    private SnmpGenericSequence gn;

    private SnmpGetRequest(EncodedSnmpVariable tlv) throws IOException {
        this.gn = SnmpGenericSequence.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    private SnmpGetRequest(List<SnmpVariable<?>> list) {
        this.gn = SnmpGenericSequence.newOfTypeAndValue(this.getSnmpType(), list);
    }

    @Override
    public EncodedSnmpVariable encode() throws IOException {
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
        return SnmpType.GET_REQUEST;
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
