package org.inuua.snmp.types;

import java.math.BigInteger;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericNumber;

public final class SnmpCounter32 implements SnmpVariable<BigInteger> {

    public static SnmpCounter32 newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpCounter32(tlv);
    }

    public static SnmpCounter32 newFromNumber(Number value) {
        return new SnmpCounter32(new BigInteger("" + value));
    }
    private SnmpGenericNumber gn;

    private SnmpCounter32(BigInteger bigInteger) {
        this.gn = SnmpGenericNumber.newOfTypeAndValue(this.getSnmpType(), bigInteger);
    }

    private SnmpCounter32(EncodedSnmpVariable tlv) {
        this.gn = SnmpGenericNumber.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gn.encode();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.COUNTER_32;
    }

    @Override
    public String getTypeNameAsString() {
        return this.gn.getTypeNameAsString();
    }

    @Override
    public BigInteger getValue() {
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
