package org.inuua.snmp.types;

import java.io.IOException;
import java.util.List;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericSequence;

public final class SnmpSequence implements SnmpVariable<List<SnmpVariable<?>>> {

    public static SnmpSequence newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) throws IOException {
        return new SnmpSequence(tlv);
    }

    public static SnmpSequence newFromList(List<SnmpVariable<?>> sequence) {
        return new SnmpSequence(sequence);
    }
    private SnmpGenericSequence gn;

    private SnmpSequence(EncodedSnmpVariable tlv) throws IOException {
        this.gn = SnmpGenericSequence.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    private SnmpSequence(List<SnmpVariable<?>> list) {
        this.gn = SnmpGenericSequence.newOfTypeAndValue(this.getSnmpType(), list);
    }

    @Override
    public EncodedSnmpVariable encode() throws IOException {
        return this.gn.encode();
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

    @Override
    public String toString() {
        return this.getValueAsString();
    }
}
