package org.inuua.snmp;

import java.io.IOException;
import org.inuua.snmp.types.SnmpSequence;
import org.inuua.snmp.types.SnmpInteger;
import java.util.ArrayList;
import java.util.List;
import org.inuua.util.ListHelpers;
import org.junit.Test;
import static org.junit.Assert.*;

public final class SnmpSequenceTest {

    @Test
    public void testEncodingOfOneInteger() throws IOException {
        SnmpInteger i = SnmpInteger.newFromNumber(4242);
        List<SnmpVariable<?>> l = new ArrayList<SnmpVariable<?>>();
        l.add(i);
        byte[] result = SnmpSequence.newFromList(l).encode().toByteArray();
        byte[] expResult = ListHelpers.byteArray(
                0x30, 0x04, 0x02, 0x02, 0x10, 0x92);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testEncodingOfTwoIntegers() throws IOException {
        SnmpInteger i = SnmpInteger.newFromNumber(4242);
        List<SnmpVariable<?>> l = new ArrayList<SnmpVariable<?>>();
        l.add(i);
        l.add(i);
        byte[] result = SnmpSequence.newFromList(l).encode().toByteArray();
        byte[] expResult = ListHelpers.byteArray(
                0x30, 0x08, 0x02, 0x02, 0x10, 0x92, 0x02, 0x02, 0x10, 0x92);
        assertArrayEquals(expResult, result);
    }
}
