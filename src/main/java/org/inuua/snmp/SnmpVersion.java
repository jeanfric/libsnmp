package org.inuua.snmp;

public enum SnmpVersion {

    V1(0),
    V2C(1),
    V3(2);
    private final int version;

    private SnmpVersion(int version) {
        this.version = version;
    }

    public int tag() {
        return this.version;
    }
}
