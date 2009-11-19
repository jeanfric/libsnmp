package org.inuua.snmp;

public enum ErrorStatus {

    NO_ERROR((byte) 0),
    TOO_BIG((byte) 1),
    NO_SUCH_NAME((byte) 2),
    BAD_VALUE((byte) 3),
    READ_ONLY((byte) 4),
    GENERAL_ERROR((byte) 5),
    NO_ACCESS((byte) 6),
    WRONG_TYPE((byte) 7),
    WRONG_LENGTH((byte) 8),
    WRONG_ENCODING((byte) 9),
    WRONG_VALUE((byte) 10),
    NO_CREATION((byte) 11),
    INCONSISTENT_VALUE((byte) 12),
    RESOURCE_UNAVAILABLE((byte) 13),
    COMMIT_FAILED((byte) 14),
    UNDO_FAILED((byte) 15),
    AUTHORIZATION_ERROR((byte) 16),
    NOT_WRITABLE((byte) 17),
    INCONSISTENT_NAME((byte) 18);

    public static ErrorStatus valueOf(Integer errorStatus) {
        for (ErrorStatus e : ErrorStatus.values()) {
            Integer i = Integer.valueOf(errorStatus);
            if (e.tag() == i) {
                return e;
            }
        }
        throw new Error("Could not find the error status specified by this integer, " + errorStatus);
    }
    private byte tag;

    private ErrorStatus(byte tag) {
        this.tag = tag;
    }

    public int tag() {
        return this.tag;
    }
}
