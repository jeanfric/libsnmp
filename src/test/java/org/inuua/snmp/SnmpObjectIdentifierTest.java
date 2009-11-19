package org.inuua.snmp;

import org.inuua.snmp.types.SnmpObjectIdentifier;
import java.util.List;
import org.inuua.util.ListHelpers;
import org.junit.Test;
import static org.junit.Assert.*;

public final class SnmpObjectIdentifierTest {

    @Test
    public void testNewFromString() {
        byte[] result = SnmpObjectIdentifier.newFromString("12.12.22.234").encode().toByteArray();
        byte[] expResult = ListHelpers.byteArray(
                0x06, 0x04, 0xec, 0x16, 0x81, 0x6a);
        assertArrayEquals(expResult, result);
    }
}
