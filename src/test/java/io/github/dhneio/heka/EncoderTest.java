package io.github.dhneio.heka;

import static org.junit.Assert.*;

import com.google.protobuf.InvalidProtocolBufferException;
import io.github.dhneio.heka.client.Protobuf;
import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

public class EncoderTest {
    private Message message;

    @Before
    public void setup() {
        message = new Message.Builder()
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
    }

    private Protobuf.Header extractHeader(byte[] bytes) throws InvalidProtocolBufferException {
        int headerLength = bytes[1];
        byte[] headerBytes = Arrays.copyOfRange(bytes, 2, 2 + headerLength);
        return Protobuf.Header.parseFrom(headerBytes);
    }

    @Test
    public void testSignHmacMD5() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String key = "hmac_key";
        HmacConfiguration conf = new HmacConfiguration("signer", 1, HmacConfiguration.HashFunction.MD5, key);

        Encoder encoder = new ProtobufEncoder();
        encoder.setHmacConfiguration(conf);
        assertEquals(conf, encoder.getHmacConfiguration());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encoder.encode(message, baos);

        Protobuf.Header header = extractHeader(baos.toByteArray());

        byte[] msgBytes = encoder.encodeMessage(message);

        assertEquals(conf.signer, header.getHmacSigner());
        assertEquals(conf.keyVersion, header.getHmacKeyVersion());
        assertEquals(Protobuf.Header.HmacHashFunction.MD5, header.getHmacHashFunction());

        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(new SecretKeySpec(key.getBytes(), "HmacMD5"));
        assertArrayEquals(header.getHmac().toByteArray(), mac.doFinal(msgBytes));
    }

    @Test
    public void testSignHmacSHA1() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String key = "hmac_key";
        HmacConfiguration conf = new HmacConfiguration("signer", 1, HmacConfiguration.HashFunction.SHA1, key);

        Encoder encoder = new ProtobufEncoder();
        encoder.setHmacConfiguration(conf);
        assertEquals(conf, encoder.getHmacConfiguration());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encoder.encode(message, baos);

        Protobuf.Header header = extractHeader(baos.toByteArray());

        byte[] msgBytes = encoder.encodeMessage(message);

        assertEquals(conf.signer, header.getHmacSigner());
        assertEquals(conf.keyVersion, header.getHmacKeyVersion());
        assertEquals(Protobuf.Header.HmacHashFunction.SHA1, header.getHmacHashFunction());

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
        assertArrayEquals(header.getHmac().toByteArray(), mac.doFinal(msgBytes));
    }

    @Test
    public void testEncodeHeader() throws IOException {
        Encoder encoder = new ProtobufEncoder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encoder.encode(message, baos);

        Protobuf.Header header = extractHeader(baos.toByteArray());

        assertEquals(Protobuf.Header.MessageEncoding.PROTOCOL_BUFFER, header.getMessageEncoding());
        assertEquals(encoder.encodeMessage(message).length, header.getMessageLength());
        assertFalse(header.hasHmac());
        assertFalse(header.hasHmacHashFunction());
        assertFalse(header.hasHmacKeyVersion());
        assertFalse(header.hasHmacSigner());
    }

    @Test
    public void testEncode() throws IOException {
        Encoder encoder = new ProtobufEncoder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encoder.encode(message, baos);

        byte[] bytes = baos.toByteArray();
        assertEquals(Encoder.RECORD_SEPARATOR, bytes[0]);

        byte headerLength = bytes[1];
        byte[] headerBytes = Arrays.copyOfRange(bytes, 2, 2 + headerLength);

        Protobuf.Header header = Protobuf.Header.parseFrom(headerBytes);
        int msgLength = header.getMessageLength();

        assertEquals(Encoder.UNIT_SEPARATOR, bytes[2 + headerLength]);
        byte[] msgBytes = Arrays.copyOfRange(bytes, 3 + headerLength, bytes.length);
        Message decodedMsg = new Message(Protobuf.Message.parseFrom(msgBytes));
        assertEquals(message, decodedMsg);

        ChannelBuffer cb = encoder.encodeToChannelBuffer(message);
        assertArrayEquals(bytes, cb.array());
    }
}
