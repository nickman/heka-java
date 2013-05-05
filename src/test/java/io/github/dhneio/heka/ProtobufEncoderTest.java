package io.github.dhneio.heka;

import static org.junit.Assert.*;

import com.google.protobuf.InvalidProtocolBufferException;
import io.github.dhneio.heka.client.Protobuf;
import org.junit.Test;

import java.util.UUID;

public class ProtobufEncoderTest {
    @Test
    public void testProtobufEncode() throws InvalidProtocolBufferException {
        Message msg = new Message.Builder()
                .uuid(UUID.randomUUID())
                .timestamp(new Long(3820482))
                .payload("Test message")
                .pid(780)
                .hostname("localhost")
                .field("foo", "bar")
                .field("baz", 39)
                .field("pi", 3.14)
                .field("true", false)
                .build();

        ProtobufEncoder encoder = new ProtobufEncoder();
        byte[] msgBytes = encoder.encodeMessage(msg);

        Message msg2 = new Message(Protobuf.Message.parseFrom(msgBytes));
        assertEquals(msg, msg2);
    }

    @Test
    public void testMessageEncoding() {
        Encoder encoder = new ProtobufEncoder();
        assertEquals(Protobuf.Header.MessageEncoding.PROTOCOL_BUFFER, encoder.getMessageEncoding());
    }
}
