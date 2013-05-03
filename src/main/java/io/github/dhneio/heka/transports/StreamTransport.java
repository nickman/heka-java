package io.github.dhneio.heka.transports;

import io.github.dhneio.heka.Encoder;
import io.github.dhneio.heka.Message;
import io.github.dhneio.heka.Transport;

import java.io.IOException;
import java.io.OutputStream;

public class StreamTransport implements Transport {
    private final OutputStream stream;
    private final Encoder encoder;

    public StreamTransport(OutputStream stream, Encoder encoder) {
        this.stream = stream;
        this.encoder = encoder;
    }

    @Override
    public void setup() {
    }

    @Override
    public synchronized void close() {
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing stream", e);
        }
    }

    @Override
    public synchronized void sendMessage(Message message) {
        byte[] bytes = encoder.encodeMessage(message);
        try {
            stream.write(bytes);
            stream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error writing message to stream", e);
        }
    }
}
