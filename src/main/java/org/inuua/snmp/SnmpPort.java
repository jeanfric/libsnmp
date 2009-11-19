package org.inuua.snmp;

public enum SnmpPort {

    STANDARD_PORT(161), STANDARD_TRAP_PORT(162);
    private final int portNumber;

    private SnmpPort(int portNumber) {
        this.portNumber = portNumber;
    }

    public int portNumber() {
        return this.portNumber;
    }
}
