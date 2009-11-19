package org.inuua.snmp;

import java.io.IOException;
import org.inuua.snmp.types.SnmpInteger;
import org.inuua.util.ListHelpers;
import org.junit.Test;
import static org.junit.Assert.*;

public final class SnmpIntegerTest {

    @Test
    public void testEncoding() throws IOException {
        byte[] result = SnmpInteger.newFromNumber(1).encode().toByteArray();
        byte[] expResult = ListHelpers.byteArray(
                0x02, 0x01, 0x01);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testEncodingLargeNumber() throws IOException {
        byte[] result = SnmpInteger.newFromNumber(4242).encode().toByteArray();
        byte[] expResult = ListHelpers.byteArray(
                0x02, 0x02, 0x10, 0x92);
        assertArrayEquals(expResult, result);
    }
}
