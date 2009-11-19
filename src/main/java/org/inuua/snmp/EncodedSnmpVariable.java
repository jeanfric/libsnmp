package org.inuua.snmp;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import org.inuua.snmp.types.SnmpBitString;
import org.inuua.snmp.types.SnmpCounter32;
import org.inuua.snmp.types.SnmpGauge32;
import org.inuua.snmp.types.SnmpGetNextRequest;
import org.inuua.snmp.types.SnmpGetRequest;
import org.inuua.snmp.types.SnmpInteger;
import org.inuua.snmp.types.SnmpIpAddress;
import org.inuua.snmp.types.SnmpNsapAddress;
import org.inuua.snmp.types.SnmpNull;
import org.inuua.snmp.types.SnmpObjectIdentifier;
import org.inuua.snmp.types.SnmpOctetString;
import org.inuua.snmp.types.SnmpOpaque;
import org.inuua.snmp.types.SnmpResponse;
import org.inuua.snmp.types.SnmpSequence;
import org.inuua.snmp.types.SnmpSetRequest;
import org.inuua.snmp.types.SnmpTimeTicks;
import org.inuua.snmp.types.SnmpUnsignedInteger32;

public final class EncodedSnmpVariable {

    private static byte[] getLengthEncoding(Integer length) throws IOException {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        try {

            if (length < 128) {
                /* Single byte representation if < 128 */
                outBytes.write((byte) ((int) length));

            } else {
                /* Multi byte representation if >= 128 */
                byte requiredBytes = 0;
                int temp = length;
                while (temp > 0) {
                    requiredBytes++;
                    temp = (int) Math.floor(temp / 256.0);
                }

                byte num = requiredBytes;

                num += 128;
                outBytes.write(num);

                byte[] len = new byte[requiredBytes];
                for (int i = requiredBytes - 1; i >= 0; --i) {
                    len[i] = (byte) (length % 256);
                    length = (int) Math.floor(length / 256.0);
                }
                outBytes.write(len, 0, requiredBytes);
            }

            return outBytes.toByteArray();
        } finally {
            outBytes.close();
        }
    }

    public static EncodedSnmpVariable newFromByteArray(byte[] barray) {
        return newFromByteArrayRange(barray, 0, barray.length);
    }

    public static EncodedSnmpVariable newFromByteArrayRange(byte[] barray, int fromIndex, int toIndex) {
        // TODO: check we don't go outside the specified range
        String errorMsg = "Bad SNMP encoded value: the length specifier goes outside the buffer.";
        int currentPos = fromIndex;
        SnmpType tRequestType = SnmpType.valueOf(barray[currentPos++]);

        int length;

        int unsignedValue = barray[currentPos];
        if (unsignedValue < 0) {
            unsignedValue += 256;
        }

        if ((unsignedValue / 128) < 1) {
            // single byte length; extract value
            length = unsignedValue;
        } else {
            // multiple byte length; first byte's value (minus first bit) is #
            // of length bytes
            int numBytes = (unsignedValue % 128);

            length = 0;

            for (int i = 0; i < numBytes; i++) {
                currentPos++;
                if (currentPos > toIndex) {
                    throw new Error(errorMsg);
                }
                if (currentPos > toIndex) {
                    throw new Error(errorMsg);
                }
                unsignedValue = barray[currentPos];
                if (unsignedValue < 0) {
                    unsignedValue += 256;
                }
                length = length * 256 + unsignedValue;
            }
        }
        currentPos++;
        if (currentPos > toIndex) {
            throw new Error(errorMsg);
        }

        return new EncodedSnmpVariable(tRequestType, barray, currentPos, currentPos + length);
    }

    public static EncodedSnmpVariable newFromTypeAndValue(SnmpType type, byte[] value) {
        return new EncodedSnmpVariable(type, value);
    }
    private final SnmpType type;
    private final byte[] value;

    private EncodedSnmpVariable(SnmpType type, byte[] data) {
        this.type = type;
        this.value = data.clone();
    }

    private EncodedSnmpVariable(SnmpType type, byte[] backingArr, int fromIndex, int toIndex) {
        this.type = type;
        int len = toIndex - fromIndex;
        this.value = new byte[len];
        System.arraycopy(backingArr, fromIndex, this.value, 0, this.value.length);
    }

    public SnmpVariable<?> asSnmpVariable() throws IOException {

        switch (this.getSnmpType()) {
            case INTEGER:
                return SnmpInteger.newFromEncodedSnmpVariable(this);
            case BIT_STRING:
                return SnmpBitString.newFromEncodedSnmpVariable(this);
            case OCTET_STRING:
                return SnmpOctetString.newFromEncodedSnmpVariable(this);
            case NULL:
                return SnmpNull.newFromEncodedSnmpVariable(this);
            case OBJECT_IDENTIFIER:
                return SnmpObjectIdentifier.newFromEncodedSnmpVariable(this);
            case SEQUENCE:
                return SnmpSequence.newFromEncodedSnmpVariable(this);
            case IP_ADDRESS:
                return SnmpIpAddress.newFromEncodedSnmpVariable(this);
            case COUNTER_32:
                return SnmpCounter32.newFromEncodedSnmpVariable(this);
            case GAUGE_32:
                return SnmpGauge32.newFromEncodedSnmpVariable(this);
            case TIMETICKS:
                return SnmpTimeTicks.newFromEncodedSnmpVariable(this);
            case OPAQUE:
                return SnmpOpaque.newFromEncodedSnmpVariable(this);
            case NSAP_ADDRESS:
                return SnmpNsapAddress.newFromEncodedSnmpVariable(this);
            case COUNTER_64:
                return SnmpNsapAddress.newFromEncodedSnmpVariable(this);
            case UNSIGNED_INTEGER_32:
                return SnmpUnsignedInteger32.newFromEncodedSnmpVariable(this);
            case GET_REQUEST:
                return SnmpGetRequest.newFromEncodedSnmpVariable(this);
            case RESPONSE:
                return SnmpResponse.newFromEncodedSnmpVariable(this);
            case GET_NEXT_REQUEST:
                return SnmpGetNextRequest.newFromEncodedSnmpVariable(this);
            case SET_REQUEST:
                return SnmpSetRequest.newFromEncodedSnmpVariable(this);
        }

        throw new Error(String.format("Unknown SNMP type (tag 0x%x)", this.getSnmpType().tag()));
    }

    public SnmpType getSnmpType() {
        return this.type;
    }

    public byte[] getValueAsByteArray() {
        return this.value.clone();
    }

    public int size() throws IOException {
        return this.value.length + getLengthEncoding(this.value.length).length + 1;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        try {
            outBytes.write(this.type.tag());
            byte[] len = getLengthEncoding(this.value.length);
            outBytes.write(len, 0, len.length);
            outBytes.write(this.value, 0, this.value.length);

            return outBytes.toByteArray();
        } finally {
            outBytes.close();
        }
    }
}
