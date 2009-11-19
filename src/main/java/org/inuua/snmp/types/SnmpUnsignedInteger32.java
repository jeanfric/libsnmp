package org.inuua.snmp.types;

import java.math.BigInteger;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericNumber;

public final class SnmpUnsignedInteger32 implements SnmpVariable<BigInteger> {

    public static SnmpUnsignedInteger32 newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpUnsignedInteger32(tlv);
    }

    public static SnmpUnsignedInteger32 newFromNumber(Number value) {
        return new SnmpUnsignedInteger32(new BigInteger("" + value));
    }
    private SnmpGenericNumber gn;

    private SnmpUnsignedInteger32(BigInteger bigInteger) {
        this.gn = SnmpGenericNumber.newOfTypeAndValue(this.getSnmpType(), bigInteger);
    }

    private SnmpUnsignedInteger32(EncodedSnmpVariable tlv) {
        this.gn = SnmpGenericNumber.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gn.encode();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.UNSIGNED_INTEGER_32;
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
