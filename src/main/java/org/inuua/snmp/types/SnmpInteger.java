package org.inuua.snmp.types;

import java.math.BigInteger;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericNumber;

public final class SnmpInteger implements SnmpVariable<BigInteger> {

    public static SnmpInteger newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpInteger(tlv);
    }

    public static SnmpInteger newFromNumber(Number value) {
        return new SnmpInteger(new BigInteger("" + value));
    }
    private SnmpGenericNumber gn;

    private SnmpInteger(BigInteger bigInteger) {
        this.gn = SnmpGenericNumber.newOfTypeAndValue(this.getSnmpType(), bigInteger);
    }

    private SnmpInteger(EncodedSnmpVariable tlv) {
        this.gn = SnmpGenericNumber.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gn.encode();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.INTEGER;
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
