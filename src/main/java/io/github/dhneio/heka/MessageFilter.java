package io.github.dhneio.heka;

public interface MessageFilter {
    /**
     * Returns true if the message should be sent, false otherwise.
     * Implementations must be reentrant.
     */
    public boolean filter(Message message);
}
