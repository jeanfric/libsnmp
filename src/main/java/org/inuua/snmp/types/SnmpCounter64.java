package org.inuua.snmp.types;

import java.math.BigInteger;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericNumber;

public final class SnmpCounter64 implements SnmpVariable<BigInteger> {

    public static SnmpCounter64 newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpCounter64(tlv);
    }

    public static SnmpCounter64 newFromNumber(Number value) {
        return new SnmpCounter64(new BigInteger("" + value));
    }
    private SnmpGenericNumber gn;

    private SnmpCounter64(BigInteger bigInteger) {
        this.gn = SnmpGenericNumber.newOfTypeAndValue(this.getSnmpType(), bigInteger);
    }

    private SnmpCounter64(EncodedSnmpVariable tlv) {
        this.gn = SnmpGenericNumber.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gn.encode();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.COUNTER_64;
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
