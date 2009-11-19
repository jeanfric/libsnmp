package org.inuua.snmp;

import java.io.IOException;
import org.inuua.snmp.types.SnmpNull;
import org.inuua.snmp.types.SnmpGetRequest;
import org.inuua.snmp.types.SnmpObjectIdentifier;
import java.util.HashMap;
import java.util.Map;
import org.inuua.snmp.types.SnmpMessage;
import org.inuua.util.ListHelpers;
import org.junit.Test;
import static org.junit.Assert.*;

public final class SnmpMessageTest {

    @Test
    public void testEncoding() throws IOException {
        Map<SnmpObjectIdentifier, SnmpVariable<?>> m = new HashMap<SnmpObjectIdentifier, SnmpVariable<?>>();
        m.put(SnmpObjectIdentifier.newFromString("1.3.6.1.2.1.1.9.1.4.2"), SnmpNull.newNull());
        SnmpGetRequest getReq = SnmpGetRequest.newFromDefinition(1511152378, ErrorStatus.NO_ERROR, 0, m);
        byte[] result = SnmpMessage.newFromDefinition(SnmpVersion.V1, "public", getReq).encode().toByteArray();

        // Taken from a wireshark dump
        byte[] expResult = ListHelpers.byteArray(
                0x30, 0x2b, 0x02, 0x01, 0x00, 0x04, 0x06, 0x70,
                0x75, 0x62, 0x6c, 0x69, 0x63, 0xa0, 0x1e, 0x02,
                0x04, 0x5a, 0x12, 0x5a, 0xfa, 0x02, 0x01, 0x00,
                0x02, 0x01, 0x00, 0x30, 0x10, 0x30, 0x0e, 0x06,
                0x0a, 0x2b, 0x06, 0x01, 0x02, 0x01, 0x01, 0x09,
                0x01, 0x04, 0x02, 0x05, 0x00);

        assertArrayEquals(expResult, result);
    }

    @Test
    public void testDecoding() throws IOException {

        Map<SnmpObjectIdentifier, SnmpVariable<?>> m = new HashMap<SnmpObjectIdentifier, SnmpVariable<?>>();
        m.put(SnmpObjectIdentifier.newFromString("1.3.6.1.2.1.1.9.1.4.2"), SnmpNull.newNull());
        SnmpGetRequest getReq = SnmpGetRequest.newFromDefinition(1511152378, ErrorStatus.NO_ERROR, 0, m);
        SnmpMessage encodedMsg = SnmpMessage.newFromDefinition(SnmpVersion.V1, "public", getReq);
        byte[] result = encodedMsg.encode().toByteArray();

        // Taken from a wireshark dump
        byte[] expResult = ListHelpers.byteArray(
                0x30, 0x2b, 0x02, 0x01, 0x00, 0x04, 0x06, 0x70,
                0x75, 0x62, 0x6c, 0x69, 0x63, 0xa0, 0x1e, 0x02,
                0x04, 0x5a, 0x12, 0x5a, 0xfa, 0x02, 0x01, 0x00,
                0x02, 0x01, 0x00, 0x30, 0x10, 0x30, 0x0e, 0x06,
                0x0a, 0x2b, 0x06, 0x01, 0x02, 0x01, 0x01, 0x09,
                0x01, 0x04, 0x02, 0x05, 0x00);

        assertArrayEquals(expResult, result);

        SnmpMessage decodedMsg = SnmpMessage.newFromEncodedSnmpVariable(EncodedSnmpVariable.newFromByteArray(expResult));

        assertEquals(decodedMsg.getCommunity(), encodedMsg.getCommunity());
        assertArrayEquals(expResult, decodedMsg.encode().toByteArray());
    }
}
