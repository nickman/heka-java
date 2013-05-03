package io.github.dhneio.heka;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import io.github.dhneio.heka.Message;
import io.github.dhneio.heka.MessageDefaultsProvider;
import org.junit.Test;

import java.util.UUID;

public class MessageBuilderTest {
    @Test(expected=RuntimeException.class)
    public void testEmptyBuildFails() {
        // UUID and timestamp are required
        Message.Builder builder = new Message.Builder();
        builder.build();
    }

    @Test
    public void testBuild() {
        UUID uuid = UUID.randomUUID();

        Message msg = new Message.Builder()
                .uuid(uuid)
                .timestamp(42L)
                .build();

        assertEquals(uuid, msg.getUuid());
        assertEquals(42L, msg.getTimestamp());
        assertNull(msg.getPayload());
        assertNull(msg.getPid());
        assertNull(msg.getType());
        assertNull(msg.getSeverity());
        assertNull(msg.getLogger());
    }

    @Test
    public void testBuildWithDefaults() {
        MessageDefaultsProvider mdpMock = mock(MessageDefaultsProvider.class);

        UUID uuid = UUID.randomUUID();
        long timestamp = 42L;
        String hostname = "localhost";
        int pid = 7;
        String envVersion = "0.8";

        when(mdpMock.getUuid()).thenReturn(uuid);
        when(mdpMock.getTimestamp()).thenReturn(timestamp);
        when(mdpMock.getHostname()).thenReturn(hostname);
        when(mdpMock.getPid()).thenReturn(pid);
        when(mdpMock.getEnvVersion()).thenReturn(envVersion);
        when(mdpMock.getSeverity()).thenReturn(null);

        Message msg = new Message.Builder()
                .defaults(mdpMock)
                .hostname("remotehost")
                .pid(94)
                .type("msg_type")
                .build();

        assertEquals(uuid, msg.getUuid());
        assertEquals(timestamp, (long)msg.getTimestamp());
        assertEquals(envVersion, msg.getEnvVersion());
        assertEquals("remotehost", msg.getHostname());
        assertEquals(94, (int)msg.getPid());
        assertEquals("msg_type", msg.getType());
        assertNull(msg.getPayload());
        assertNull(msg.getLogger());
        assertNull(msg.getSeverity());
    }
}
