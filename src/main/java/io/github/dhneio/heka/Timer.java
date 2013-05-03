package io.github.dhneio.heka;

/**
 * A stop watch that sends a timer message to the supplied HekaClient when
 * stop() is called.
 *
 * Note: this class is not thread-safe.
 */
public class Timer {
    private long started;
    private boolean isRunning;

    private final HekaClient client;
    private final String name;

    public Timer(HekaClient client, String name) {
        this.name = name;
        this.client = client;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void start() {
        if (isRunning) {
            throw new IllegalStateException("The Timer is already running.");
        }
        isRunning = true;

        started = System.currentTimeMillis();
    }

    public long stop() {
        long elapsed = System.currentTimeMillis() - started;

        if (!isRunning) {
            throw new IllegalStateException("The Timer isn't running.");
        }
        isRunning = false;

        client.send(client.message("timer")
                .payload(Long.toString(elapsed))
                .field("name", name)
                .build());

        return elapsed;
    }
}
