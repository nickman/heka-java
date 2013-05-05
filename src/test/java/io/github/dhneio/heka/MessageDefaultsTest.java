package io.github.dhneio.heka;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class MessageDefaultsTest {
    public MessageDefaults defaults;

    @Before
    public void setupDefaults() {
        defaults = new MessageDefaults();
    }

    @Test
    public void testEmptyDefaults() {
        assertNotNull(defaults.getUuid());
        assertNotNull(defaults.getTimestamp());
        assertNotNull(defaults.getHostname());
        assertNotNull(defaults.getPid());
        assertNull(defaults.getLogger());
        assertNull(defaults.getSeverity());
        assertNull(defaults.getPayload());
        assertNull(defaults.getEnvVersion());
    }

    @Test
    public void testUniqueUuids() {
        UUID uuid1 = defaults.getUuid();
        UUID uuid2 = defaults.getUuid();
        UUID uuid3 = defaults.getUuid();

        assertNotEquals(uuid1, uuid2);
        assertNotEquals(uuid2, uuid3);
        assertNotEquals(uuid3, uuid1);
    }

    @Test
    public void testSetDefaults() {
        defaults.setSeverity(7);
        defaults.setPayload("payload");
        defaults.setHostname("new_hostname");

        assertEquals(7, (int)defaults.getSeverity());
        assertEquals("payload", defaults.getPayload());
        assertEquals("new_hostname", defaults.getHostname());
    }
}
