package org.inuua.snmp.types;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.inuua.snmp.EncodedSnmpVariable;
import org.inuua.snmp.SnmpType;
import org.inuua.snmp.SnmpVariable;
import org.inuua.util.ListHelpers;

public final class SnmpObjectIdentifier implements SnmpVariable<List<Long>> {

    public static SnmpObjectIdentifier newFromEncodedSnmpVariable(EncodedSnmpVariable tlv) {
        return new SnmpObjectIdentifier(tlv);
    }

    public static SnmpObjectIdentifier newFromString(String identifier) {
        return new SnmpObjectIdentifier(identifier);
    }

    private static long[] stringToLongArray(String identifier) {
        // TODO: accept humanized strings and convert them to dotted notation...
        // see the Humanizer class to do a reverse lookup.
        // Generate a list by tokenizing the string
        List<Long> tmpValue = new ArrayList<Long>(identifier.length());
        String[] toks = identifier.split("\\.");
        for (int i = 0; i < toks.length; i++) {
            tmpValue.add(Long.parseLong(toks[i]));
        }
        return ListHelpers.toPrimitiveLongArray(tmpValue);
    }
    private final long[] value;

    private SnmpObjectIdentifier(EncodedSnmpVariable tlv) {

        // note: masks must be ints; byte internal representation issue(?)
        int bitTest = 0x80; // test for leading 1
        int highBitMask = 0x7F; // mask out high bit for value
        long[] digits;
        byte[] enc = tlv.getValueAsByteArray();

        // first, compute number of "digits";
        // will just be number of bytes with leading 0's
        int numInts = 0;
        for (int i = 0; i < enc.length; i++) {
            if ((enc[i] & bitTest) == 0) // high-order bit not set; count
            {
                numInts++;
            }
        }

        if (numInts > 0) {
            // create new int array to hold digits; since first value is 40*x +
            // y,
            // need one extra entry in array to hold this.
            digits = new long[numInts + 1];

            int currentByte = -1; // will be incremented to 0

            long tvalue = 0;

            // read in values 'til get leading 0 in byte
            do {
                currentByte++;
                tvalue = tvalue * 128 + (enc[currentByte] & highBitMask);
            } while ((enc[currentByte] & bitTest) > 0); // implies high bit set!

            // now handle 40a + b
            digits[0] = (long) Math.floor(tvalue / 40.0);
            digits[1] = tvalue % 40;

            // now read in rest!
            for (int i = 2; i < numInts + 1; i++) {
                // read in values 'til get leading 0 in byte
                tvalue = 0;
                do {
                    currentByte++;
                    tvalue = tvalue * 128 + (enc[currentByte] & highBitMask);
                } while ((enc[currentByte] & bitTest) > 0);

                digits[i] = tvalue;
            }

        } else {
            // no digits; create empty digit array
            digits = new long[0];
        }

        this.value = digits;
    }

    private SnmpObjectIdentifier(String identifier) {
        this.value = stringToLongArray(identifier);
    }

    @Override
    public EncodedSnmpVariable encode() {
        return EncodedSnmpVariable.newFromTypeAndValue(this.getSnmpType(), this.encodeArray());
    }

    private byte[] encodeArray() {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        // encode first two identifier digits as one byte, using the 40*x + y
        // rule;
        // of course, if only one element, just use 40*x; if none, do nothing
        if (this.value.length >= 2) {
            outBytes.write((byte) (40 * this.value[0] + this.value[1]));
        } else if (this.value.length == 1) {
            outBytes.write((byte) (40 * this.value[0]));
        }
        for (int i = 2; i < this.value.length; ++i) {
            byte[] nextBytes = this.encodeValue(this.value[i]);
            outBytes.write(nextBytes, 0, nextBytes.length);
        }

        return outBytes.toByteArray();
    }

    private byte[] encodeValue(long v) {
        // see how many bytes are needed: each value uses just
        // 7 bits of each byte, with high-order bit functioning as
        // a continuation marker
        int numBytes = 0;
        long temp = v;

        do {
            numBytes++;
            temp = (long) Math.floor(temp / 128.0);
        } while (temp > 0);

        byte[] enc = new byte[numBytes];
        // encode lowest-order byte, without setting high bit
        enc[numBytes - 1] = (byte) (v % 128);
        v = (long) Math.floor(v / 128.0);

        // .encode other bytes with high bit set
        for (int i = numBytes - 2; i >= 0; --i) {
            enc[i] = (byte) ((v % 128) + 128);
            v = (long) Math.floor(v / 128.0);
        }

        return enc;
    }

    public String getDottedRepresentation() {
        // Roughly estimate to be 4 bytes for each value representation
        StringBuilder b = new StringBuilder(this.value.length * 4);
        for (int i = 0; i < this.value.length; i++) {
            if (i != 0) {
                b.append(".");
            }
            b.append(this.value[i]);
        }
        return b.toString();
    }

    @Override
    public SnmpType getSnmpType() {
        return SnmpType.OBJECT_IDENTIFIER;
    }

    @Override
    public String getTypeNameAsString() {
        return this.getSnmpType().toString();
    }

    @Override
    public List<Long> getValue() {
        return ListHelpers.toListOfLongs(this.value);
    }

    @Override
    public String getValueAsString() {
        return this.getDottedRepresentation();
    }

    @Override
    public String toString() {
        return this.getValueAsString();
    }
}
