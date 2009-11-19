package org.inuua.snmp;

public enum SnmpSequenceLength {

    ONE_ELEMENT(1),
    TWO_ELEMENTS(2),
    THREE_ELEMENTS(3),
    FOUR_ELEMENTS(4),
    INFINITE_NUMBER_OF_ELEMENTS(999); // Really?  hummmmmm
    private final int length;

    private SnmpSequenceLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }
}
