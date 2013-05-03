package io.github.dhneio.heka.transports;

import io.github.dhneio.heka.Encoder;
import io.github.dhneio.heka.Message;
import io.github.dhneio.heka.ProtobufEncoder;
import io.github.dhneio.heka.Transport;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class UdpTransport implements Transport {
    private final ChannelGroup channels = new DefaultChannelGroup();
    private volatile ConnectionlessBootstrap bootstrap;

    protected final Encoder encoder;

    public UdpTransport() {
        this(new ProtobufEncoder());
    }

    public UdpTransport(Encoder encoder) {
        this.encoder = encoder;
    }

    public Encoder getEncoder() {
        return encoder;
    }

    public synchronized void setup() {
        if (bootstrap != null) {
            throw new IllegalStateException("setup() called twice");
        }

        final ChannelFactory channelFactory = new NioDatagramChannelFactory(Executors.newCachedThreadPool());

        bootstrap = new ConnectionlessBootstrap(channelFactory);

        bootstrap.setPipelineFactory(
                new ChannelPipelineFactory() {
                    @Override
                    public ChannelPipeline getPipeline() throws Exception {
                        final ChannelPipeline p = Channels.pipeline();

                        p.addLast("message-encoder", encoder);

                        return p;
                    }
                }
        );
    }

    public void connect(String host, int port) throws IOException {
        connect(new InetSocketAddress(host, port));
    }

    public void connect(InetSocketAddress address) throws IOException {
        ChannelFuture result = bootstrap.connect(address).awaitUninterruptibly();

        if (!result.isSuccess()) {
            throw new IOException("Connection failed", result.getCause());
        }

        Channel channel = result.getChannel();
        channel.setReadable(false);
        channels.add(channel);
    }

    public void disconnect(String host, int port) {
        disconnect(new InetSocketAddress(host, port));
    }

    public void disconnect(InetSocketAddress address) {
        for (Channel ch : channels) {
            if (ch.getRemoteAddress().equals(address)) {
                ch.close();
            }
        }
    }

    @Override
    public synchronized void close() {
        try {
            channels.close().awaitUninterruptibly();
            bootstrap.releaseExternalResources();
        } finally {
            bootstrap = null;
        }
    }

    public boolean isConnected() {
        for (Channel ch : channels) {
            if (ch.isConnected()) {
                return true;
            }
        }

        return false;
    }

    public boolean isConnected(String host, int port) {
        return isConnected(new InetSocketAddress(host, port));
    }

    public boolean isConnected(InetSocketAddress address) {
        for (Channel ch : channels) {
            if (ch.getRemoteAddress().equals(address) && ch.isConnected()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sends a Heka message to all connections asynchronously.
     */
    @Override
    public void sendMessage(Message message) {
        channels.write(message);
    }
}
