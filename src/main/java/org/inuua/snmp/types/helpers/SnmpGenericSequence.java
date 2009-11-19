package org.inuua.snmp.types.helpers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpSequenceLength;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.SnmpObjectIdentifier;
import org.inuua.snmp.types.SnmpSequence;

public final class SnmpGenericSequence {

    public static SnmpGenericSequence newFromEncodedSnmpVariable(SnmpType type, EncodedSnmpVariable tlv) {
        return new SnmpGenericSequence(type, tlv);
    }

    public static SnmpGenericSequence newOfTypeAndValue(SnmpType type, List<SnmpVariable<?>> value) {
        return new SnmpGenericSequence(type, value);

    }
    private final List<SnmpVariable<?>> value;
    private final SnmpType snmpType;

    private SnmpGenericSequence(SnmpType type, EncodedSnmpVariable tlv) {
        if (tlv.getSnmpType() != type) {
            throw new Error("The type specified in the encoded variable is not the expected type");
        }

        this.snmpType = type;
        List<SnmpVariable<?>> lll = new ArrayList<SnmpVariable<?>>();

        int position = 0;
        byte[] valueArray = tlv.getValueAsByteArray();
        while (position < valueArray.length) {
            EncodedSnmpVariable t = EncodedSnmpVariable.newFromByteArrayRange(valueArray, position, valueArray.length);
            lll.add(t.asSnmpVariable());
            position += t.size();
        }

        if (type.getExpectedSequenceLength() != SnmpSequenceLength.INFINITE_NUMBER_OF_ELEMENTS) {
            if (lll.size() != type.getExpectedSequenceLength().getLength()) {
                throw new Error("Type " + type + ": did not get the expected of sub snmp variable, " + type.getExpectedSequenceLength().getLength() + " , but " + lll.size());
            }

        }
        this.value = lll;
    }

    private SnmpGenericSequence(SnmpType type, List<SnmpVariable<?>> value) {
        this.snmpType = type;
        this.value = new ArrayList<SnmpVariable<?>>(value);
    }

    public Map<SnmpObjectIdentifier, SnmpVariable<?>> asVariableBindingsMap() {

        Map<SnmpObjectIdentifier, SnmpVariable<?>> m = new HashMap<SnmpObjectIdentifier, SnmpVariable<?>>();
        List<SnmpVariable<?>> entry = new ArrayList<SnmpVariable<?>>();

        SnmpGenericSequence topLevelSeq = SnmpGenericSequence.newOfTypeAndValue(SnmpType.SEQUENCE, this.value);

        for (int i = 0; i < topLevelSeq.getValue().size(); i++) {
            SnmpSequence entrySeq = (SnmpSequence) topLevelSeq.getVariableOfExpectedTypeAt(SnmpType.SEQUENCE, i);
            SnmpGenericSequence gEntrySeq = SnmpGenericSequence.newOfTypeAndValue(SnmpType.SEQUENCE, entrySeq.getValue());
            m.put(((SnmpObjectIdentifier) gEntrySeq.getVariableOfExpectedTypeAt(SnmpType.OBJECT_IDENTIFIER, 0)),
                    ((SnmpVariable<?>) gEntrySeq.getVariableAt(1)));

            entry.clear();
        }
        return m;
    }

    public EncodedSnmpVariable encode() {
        int num = this.value.size();
        // Roughly estimate the number of byte slots required as the number
        // of values in this sequence, times 16 bytes
        ByteArrayOutputStream buf = new ByteArrayOutputStream(num * 16);
        for (int i = 0; i < num; i++) {
            byte[] b = this.value.get(i).encode().toByteArray();
            buf.write(b, 0, b.length);
        }

        return EncodedSnmpVariable.newFromTypeAndValue(this.snmpType, buf.toByteArray());
    }

    public String getTypeNameAsString() {
        return this.snmpType.toString();
    }

    public List<SnmpVariable<?>> getValue() {
        return Collections.unmodifiableList(this.value);
    }

    public String getValueAsString() {
        return this.value.toString();
    }

    public SnmpVariable<?> getVariableAt(int position) {
        return this.value.get(position);
    }

    public SnmpVariable<?> getVariableOfAnyExpectedTypeAt(SnmpType[] types, int position) {
        SnmpVariable<?> v = this.value.get(position);
        for (SnmpType t : types) {
            if (v.getSnmpType() == t) {
                return v;
            }
        }
        throw new Error("Expected types " + types + " but read a " + v.getSnmpType());
    }

    public SnmpVariable<?> getVariableOfExpectedTypeAt(SnmpType type, int position) {
        SnmpVariable<?> v = this.value.get(position);

        if (v.getSnmpType() != type) {
            throw new Error("Expected type " + type + " but read a " + v.getSnmpType());
        }

        return v;
    }
}
