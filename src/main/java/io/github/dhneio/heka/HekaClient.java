package io.github.dhneio.heka;

import io.github.dhneio.heka.transports.TcpTransport;
import io.github.dhneio.heka.transports.UdpTransport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HekaClient {
    private volatile MessageDefaultsProvider defaults;
    private final Transport transport;
    private final CopyOnWriteArrayList<MessageFilter> filters;

    /**
     * Constructs a HekaClient pointing to a single Heka daemon over TCP,
     * using the default (protocol buffer) message encoder.
     */
    public static HekaClient tcp(String host, int port) throws IOException {
        return tcp(host, port, new ProtobufEncoder());
    }

    /**
     * Constructs a HekaClient pointing to a single Heka daemon over TCP,
     * using the supplied message encoder.
     */
    public static HekaClient tcp(String host, int port, Encoder encoder)
            throws IOException {
        TcpTransport transport = new TcpTransport(encoder);
        transport.setup();
        transport.connect(host, port);

        return new HekaClient(transport, new MessageDefaults());
    }

    /**
     * Constructs a HekaClient pointing to a single Heka daemon over UDP,
     * using the default (protocol buffer) message encoder.
     */
    public static HekaClient udp(String host, int port) throws IOException {
        return udp(host, port, new ProtobufEncoder());
    }

    /**
     * Constructs a HekaClient pointing to a single Heka daemon over UDP,
     * using the supplied message encoder.
     */
    public static HekaClient udp(String host, int port, Encoder encoder)
            throws IOException {
        UdpTransport transport = new UdpTransport(encoder);
        transport.setup();
        transport.connect(host, port);
        return new HekaClient(transport, new MessageDefaults());
    }

    public HekaClient(Transport transport, MessageDefaultsProvider defaults) {
        this.transport = transport;

        this.defaults = defaults;
        this.filters = new CopyOnWriteArrayList<MessageFilter>();
    }

    public MessageDefaultsProvider getDefaults() {
        return defaults;
    }

    public void setDefaults(MessageDefaultsProvider defaults) {
        this.defaults = defaults;
    }

    /**
     * Appends the supplied message filter to the end of the filter chain.
     */
    public void addFilter(MessageFilter filter) {
        filters.addIfAbsent(filter);
    }

    /**
     * Removes the supplied filter.
     */
    public void removeFilter(MessageFilter filter) {
        filters.remove(filter);
    }

    /**
     * Removes all message filters.
     */
    public void clearFilters() {
        filters.clear();
    }

    public List<MessageFilter> getFilters() {
        return new ArrayList<MessageFilter>(filters);
    }

    public Message.Builder message() {
        return new Message.Builder().defaults(defaults);
    }

    public Message.Builder message(String type) {
        return new Message.Builder().defaults(defaults).type(type);
    }

    public Message.Builder counter(String name) {
        return new Message.Builder()
                .defaults(defaults)
                .type("counter")
                .payload("1")
                .field("name", name)
                .field("rate", 1.0);
    }

    /**
     * Sends a message, if it passes any filters which have been added to this client.
     */
    public void send(Message message) {
        for (MessageFilter filter : filters) {
            if (!filter.filter(message)) {
                return;
            }
        }
        transport.sendMessage(message);
    }

    /**
     * Closes the client and underlying transport.
     */
    public void close() {
        transport.close();
    }
}
