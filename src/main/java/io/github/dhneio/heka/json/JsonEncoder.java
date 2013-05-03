package io.github.dhneio.heka.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dhneio.heka.Encoder;
import io.github.dhneio.heka.client.Protobuf;
import org.jboss.netty.channel.ChannelHandler;

import io.github.dhneio.heka.Message;

@ChannelHandler.Sharable
public class JsonEncoder extends Encoder {
    // ObjectMapper is fully thread-safe
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Module());

    public JsonEncoder() {
    }

    @Override
    public Protobuf.Header.MessageEncoding getMessageEncoding() {
        return Protobuf.Header.MessageEncoding.JSON;
    }

    @Override
    public byte[] encodeMessage(Message message) {
        try {
            return mapper.writeValueAsBytes(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
