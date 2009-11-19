package org.inuua.snmp;

import org.inuua.snmp.types.SnmpNull;
import org.inuua.util.ListHelpers;
import org.junit.Test;
import static org.junit.Assert.*;

public final class SnmpNullTest {

    @Test
    public void testEncoding() {
        byte[] result = SnmpNull.newNull().encode().toByteArray();
        byte[] expResult = ListHelpers.byteArray(
                0x05, 0x00);
        assertArrayEquals(expResult, result);
    }
}
