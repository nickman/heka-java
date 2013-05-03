package io.github.dhneio.heka;

public interface Transport {
    void setup();
    void close();
    void sendMessage(Message message);
}
