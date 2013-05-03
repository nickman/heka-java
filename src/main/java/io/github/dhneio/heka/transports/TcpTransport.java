package io.github.dhneio.heka.transports;

import io.github.dhneio.heka.Encoder;
import io.github.dhneio.heka.Message;
import io.github.dhneio.heka.ProtobufEncoder;
import io.github.dhneio.heka.Transport;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class TcpTransport implements Transport {
    private final ChannelGroup channels = new DefaultChannelGroup();
    private ClientBootstrap bootstrap;

    protected final Encoder encoder;

    public TcpTransport() {
        this(new ProtobufEncoder());
    }

    public TcpTransport(Encoder encoder) {
        this.encoder = encoder;
    }

    public Encoder getEncoder() {
        return encoder;
    }

    /**
     * Returns true if this transport is connected to at least one TCP server.
     */
    public boolean isConnected() {
        if (bootstrap == null) {
            return false;
        }

        for (Channel ch : channels) {
            if (ch.isConnected()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if this transport is connected to the supplied host and port.
     */
    public boolean isConnected(String host, int port) {
        return isConnected(new InetSocketAddress(host, port));
    }

    /**
     * Returns true if this transport is connected to the supplied address.
     */
    public boolean isConnected(InetSocketAddress address) {
        for (Channel ch : channels) {
            if (ch.getRemoteAddress().equals(address) && ch.isConnected()) {
                return true;
            }
        }

        return false;
    }

    public synchronized void setup() {
        if (bootstrap != null) {
            throw new IllegalStateException("setup() called twice");
        }

        final ChannelFactory channelFactory = new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()
        );

        bootstrap = new ClientBootstrap(channelFactory);

        bootstrap.setPipelineFactory(
                new ChannelPipelineFactory() {
                    @Override
                    public ChannelPipeline getPipeline() throws Exception {
                        final ChannelPipeline p = Channels.pipeline();

                        p.addLast("message-encoder", encoder);
                        p.addLast("tcp-handler", new TcpHandler(channels));

                        return p;
                    }
                }
        );

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
    }

    /**
     * Connects to the supplied host and port. This is a synchronous call
     * that will raise an IOException if the connection fails.
     */
    public void connect(String host, int port) throws IOException {
        connect(new InetSocketAddress(host, port));
    }

    /***
     * Connects to the supplied address. This is a synchronous call
     * that will raise an IOException if the connection fails.
     */
    public void connect(InetSocketAddress address) throws IOException {
        ChannelFuture result = bootstrap.connect(address).awaitUninterruptibly();

        if (!result.isSuccess()) {
            throw new IOException("Connection failed", result.getCause());
        }
    }

    /**
     * Disconnects from the supplied host and port.
     */
    public void disconnect(String host, int port) {
        disconnect(new InetSocketAddress(host, port));
    }

    /**
     * Disconnects from the supplied address.
     */
    public void disconnect(InetSocketAddress address) {
        for (Channel ch : channels) {
            if (ch.getRemoteAddress().equals(address)) {
                ch.close();
            }
        }
    }

    /**
     * Closes all connections and releases associated system resources.
     * This is a synchronous call.
     */
    @Override
    public synchronized void close() {
        try {
            channels.close().awaitUninterruptibly();
            bootstrap.releaseExternalResources();
        } finally {
            bootstrap = null;
        }
    }

    /**
     * Sends a Heka message to all connections asynchronously.
     */
    @Override
    public void sendMessage(Message message) {
        channels.write(message);
    }
}
