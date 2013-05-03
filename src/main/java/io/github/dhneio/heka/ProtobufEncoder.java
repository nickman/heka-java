package io.github.dhneio.heka;

import org.jboss.netty.channel.ChannelHandler;
import io.github.dhneio.heka.client.Protobuf.Header;

@ChannelHandler.Sharable
public class ProtobufEncoder extends Encoder {
    public ProtobufEncoder() {
    }

    @Override
    public Header.MessageEncoding getMessageEncoding() {
        return Header.MessageEncoding.PROTOCOL_BUFFER;
    }

    @Override
    public byte[] encodeMessage(Message message) {
        return message.toByteArray();
    }
}
