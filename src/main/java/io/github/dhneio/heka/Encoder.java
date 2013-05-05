package io.github.dhneio.heka;

import com.google.protobuf.ByteString;
import io.github.dhneio.heka.client.Protobuf;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public abstract class Encoder extends SimpleChannelHandler {
    protected static final int RECORD_SEPARATOR = 0x1e;
    protected static final int UNIT_SEPARATOR = 0x1f;

    private volatile HmacConfiguration hmacConfiguration;

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws IOException {
        Message message = (Message)e.getMessage();
        ChannelBuffer cb = encodeToChannelBuffer(message);
        Channels.write(ctx, e.getFuture(), cb);
    }

    public void encode(Message message, OutputStream stream) throws IOException {
        byte[] messageBytes = encodeMessage(message);
        byte[] headerBytes = encodeHeader(messageBytes);

        stream.write(RECORD_SEPARATOR);
        stream.write(headerBytes.length);
        stream.write(headerBytes);
        stream.write(UNIT_SEPARATOR);
        stream.write(messageBytes);
    }

    public ChannelBuffer encodeToChannelBuffer(Message message) throws IOException {
        byte[] messageBytes = encodeMessage(message);
        byte[] headerBytes = encodeHeader(messageBytes);

        ChannelBuffer cb = ChannelBuffers.buffer(2 + headerBytes.length + 1 + messageBytes.length);
        ChannelBufferOutputStream cbos = new ChannelBufferOutputStream(cb);
        cbos.writeByte(RECORD_SEPARATOR);
        cbos.writeByte(headerBytes.length);
        cbos.write(headerBytes);
        cbos.writeByte(UNIT_SEPARATOR);
        cbos.write(messageBytes);

        return cb;
    }

    private byte[] encodeHeader(byte[] messageBytes) {
        Protobuf.Header.Builder headerBuilder = Protobuf.Header.newBuilder()
                .setMessageLength(messageBytes.length)
                .setMessageEncoding(getMessageEncoding());

        HmacConfiguration hConf = getHmacConfiguration();
        if (hConf != null) {
            signMessage(messageBytes, headerBuilder, hConf);
        }

        return headerBuilder.build().toByteArray();
    }

    private void signMessage(byte[] messageBytes,
                             Protobuf.Header.Builder headerBuilder,
                             HmacConfiguration config)
    {
        String algorithm;
        Protobuf.Header.HmacHashFunction hmacHashFunction;
        switch(config.hashFunction) {
            case MD5:
                algorithm = "HmacMD5";
                hmacHashFunction = Protobuf.Header.HmacHashFunction.MD5;
                break;
            case SHA1:
                algorithm = "HmacSHA1";
                hmacHashFunction = Protobuf.Header.HmacHashFunction.SHA1;
                break;
            default: throw new IllegalStateException("Unsupported hash function");
        }

        headerBuilder.setHmacSigner(config.signer);
        headerBuilder.setHmacKeyVersion(config.keyVersion);
        headerBuilder.setHmacHashFunction(hmacHashFunction);

        SecretKeySpec key = new SecretKeySpec(config.key.getBytes(), algorithm);

        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            headerBuilder.setHmac(ByteString.copyFrom(mac.doFinal(messageBytes)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unsupported hash function", e);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("Invalid HMAC key", e);
        }
    }

    public HmacConfiguration getHmacConfiguration() {
        return hmacConfiguration;
    }

    public void setHmacConfiguration(HmacConfiguration hmacConfiguration) {
        this.hmacConfiguration = hmacConfiguration;
    }

    public abstract Protobuf.Header.MessageEncoding getMessageEncoding();
    public abstract byte[] encodeMessage(Message message);
}
