package org.inuua.snmp.types;

import java.math.BigInteger;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericNumber;

public final class SnmpGauge32 implements SnmpVariable<BigInteger> {

    public static SnmpGauge32 newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpGauge32(tlv);
    }

    public static SnmpGauge32 newFromNumber(Number value) {
        return new SnmpGauge32(new BigInteger("" + value));
    }
    private SnmpGenericNumber gn;

    private SnmpGauge32(BigInteger bigInteger) {
        this.gn = SnmpGenericNumber.newOfTypeAndValue(this.getSnmpType(), bigInteger);
    }

    private SnmpGauge32(EncodedSnmpVariable tlv) {
        this.gn = SnmpGenericNumber.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gn.encode();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.GAUGE_32;
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
