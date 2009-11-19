package org.inuua.snmp.types;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpPdu;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericSequence;

public final class SnmpTrap implements SnmpPdu {

    public static SnmpTrap newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpTrap(tlv);
    }

    public static SnmpTrap newFromList(List<SnmpVariable<?>> sequence) {
        return new SnmpTrap(sequence);
    }
    private SnmpGenericSequence gn;

    private SnmpTrap(EncodedSnmpVariable tlv) {
        this.gn = SnmpGenericSequence.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    private SnmpTrap(List<SnmpVariable<?>> bigInteger) {
        this.gn = SnmpGenericSequence.newOfTypeAndValue(this.getSnmpType(), bigInteger);
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gn.encode();
    }

    @Override
    public Integer getRequestId() {
        return ((BigInteger) this.gn.getVariableOfExpectedTypeAt(SnmpType.INTEGER, 0).getValue()).intValue();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.TRAP_V2;
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
