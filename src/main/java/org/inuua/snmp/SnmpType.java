package org.inuua.snmp;

public enum SnmpType {

    INTEGER((byte) 0x02),
    BIT_STRING((byte) 0x03),
    OCTET_STRING((byte) 0x04),
    NULL((byte) 0x05),
    OBJECT_IDENTIFIER((byte) 0x06),
    SEQUENCE((byte) 0x30, SnmpSequenceLength.INFINITE_NUMBER_OF_ELEMENTS),
    IP_ADDRESS((byte) 0x40),
    COUNTER_32((byte) 0x41),
    GAUGE_32((byte) 0x42),
    TIMETICKS((byte) 0x43),
    OPAQUE((byte) 0x44),
    NSAP_ADDRESS((byte) 0x45),
    COUNTER_64((byte) 0x46),
    UNSIGNED_INTEGER_32((byte) 0x47),
    GET_REQUEST((byte) 0xA0, SnmpSequenceLength.FOUR_ELEMENTS),
    GET_NEXT_REQUEST((byte) 0xA1, SnmpSequenceLength.FOUR_ELEMENTS),
    RESPONSE((byte) 0xA2, SnmpSequenceLength.FOUR_ELEMENTS),
    SET_REQUEST((byte) 0xA3, SnmpSequenceLength.FOUR_ELEMENTS),
    GET_BULK_REQUEST((byte) 0xA5, SnmpSequenceLength.FOUR_ELEMENTS),
    INFORM_REQUEST((byte) 0xA6, SnmpSequenceLength.FOUR_ELEMENTS),
    TRAP_V2((byte) 0xA7, SnmpSequenceLength.FOUR_ELEMENTS),
    TRAP_V1((byte) 0xA4),
    COMMUNICATION((byte) 0xA2),
    AUTHORIZED_MESSAGE((byte) 0xA1),
    ENCRYPTED_MESSAGE((byte) 0xA1),
    ENCRYPTED_DATA((byte) 0xA1);

    public static SnmpType valueOf(byte tag) {
        for (SnmpType rt : SnmpType.values()) {
            if (tag == rt.tag()) {
                return rt;
            }
        }
        throw new Error(String.format("Unknown type: 0x%x", tag));
    }
    private final byte tag;
    private final SnmpSequenceLength numberOfExpectedSubSnmpVariables;

    private SnmpType(byte tag) {
        this.tag = tag;
        this.numberOfExpectedSubSnmpVariables = SnmpSequenceLength.INFINITE_NUMBER_OF_ELEMENTS;
    }

    private SnmpType(byte tag, SnmpSequenceLength numberOfExpectedSubSnmpVariables) {
        this.tag = tag;
        this.numberOfExpectedSubSnmpVariables = numberOfExpectedSubSnmpVariables;
    }

    public SnmpSequenceLength getExpectedSequenceLength() {
        return this.numberOfExpectedSubSnmpVariables;
    }

    public byte tag() {
        return this.tag;
    }
}
