package io.github.dhneio.heka;

import static org.junit.Assert.*;

import io.github.dhneio.heka.Message;
import org.junit.Test;

import java.util.UUID;

public class MessageTest {
    @Test
    public void testEquality() {
        UUID uuid = UUID.randomUUID();
        long timestamp = 294309852;

        Message msg1 = new Message.Builder()
                .uuid(uuid)
                .timestamp(timestamp)
                .hostname("localhost")
                .payload("payload")
                .field("a", 1)
                .build();

        Message msg2 = new Message.Builder()
                .uuid(uuid)
                .timestamp(timestamp)
                .hostname("localhost")
                .payload("payload")
                .field("a", 1)
                .build();

        assertEquals(msg1, msg1);
        assertEquals(msg1, msg2);
        assertNotEquals(msg1, null);

        assertEquals(msg1.hashCode(), msg2.hashCode());

        // change payload
        Message msg3 = new Message.Builder()
                .uuid(uuid)
                .timestamp(timestamp)
                .hostname("localhost")
                .payload("different_payload")
                .field("a", 1)
                .build();

        assertNotEquals(msg1, msg3);
        assertNotEquals(msg1.hashCode(), msg3.hashCode());

        // change field
        Message msg4 = new Message.Builder()
                .uuid(uuid)
                .timestamp(timestamp)
                .hostname("localhost")
                .payload("payload")
                .field("b", 6)
                .build();

        assertNotEquals(msg1, msg4);
        assertNotEquals(msg1.hashCode(), msg4.hashCode());
    }
}
