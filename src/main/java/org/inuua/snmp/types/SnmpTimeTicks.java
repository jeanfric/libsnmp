package org.inuua.snmp.types;

import java.math.BigInteger;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.snmp.types.helpers.SnmpGenericNumber;

public final class SnmpTimeTicks implements SnmpVariable<BigInteger> {

    public static SnmpTimeTicks newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpTimeTicks(tlv);
    }

    public static SnmpTimeTicks newFromNumber(Number value) {
        return new SnmpTimeTicks(new BigInteger("" + value));
    }
    private SnmpGenericNumber gn;

    private SnmpTimeTicks(BigInteger bigInteger) {
        this.gn = SnmpGenericNumber.newOfTypeAndValue(this.getSnmpType(), bigInteger);
    }

    private SnmpTimeTicks(EncodedSnmpVariable tlv) {
        this.gn = SnmpGenericNumber.newFromEncodedSnmpVariable(this.getSnmpType(), tlv);
    }

    @Override
    public EncodedSnmpVariable encode() {
        return this.gn.encode();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.TIMETICKS;
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
        // We have hundreths of a second as a value.
        // let's make sense of it
        double secs = (this.gn.getValue().intValue()) / 100;

        int days = (int) Math.floor(secs / (60 * 60 * 24));
        int hours = (int) Math.floor((secs - (days * 60 * 60 * 24)) / (60 * 60));
        int minutes = (int) Math.floor((secs - (days * 60 * 60 * 24) - (hours * 60 * 60)) / (60));
        int seconds = (int) Math.floor(secs - (days * 60 * 60 * 24) - (hours * 60 * 60) - (minutes * 60));

        int hundreths = (int) (secs - (double) seconds);

        return String.format("%2dd %02d:%02d:%02d.%d", days, hours, minutes, seconds, hundreths);
    }

    @Override
    public String toString() {
        return this.getValueAsString();
    }
}
