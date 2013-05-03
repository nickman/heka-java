package io.github.dhneio.heka.transports;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;

class TcpHandler extends SimpleChannelHandler {
    protected final ChannelGroup channelGroup;

    public TcpHandler(final ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = e.getChannel();
        channel.setReadable(false);
        channelGroup.add(channel);
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channelGroup.remove(e.getChannel());
        super.channelClosed(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getChannel().close();
    }
}
