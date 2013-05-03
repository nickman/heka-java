package io.github.dhneio.heka;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * A basic {@link MessageDefaultsProvider} implementation.
 * <p>
 * <ul>
 *     <li><code>getUuid()</code> returns a random UUID</li>
 *     <li><code>getTimestamp()</code> returns the current Unix time in nanoseconds, with
 *         millisecond resolution.</li>
 *     <li>attempts to determine the correct hostname and PID at construction,
 *         which can be replaced with <code>setHostname()</code> and <code>setPid()</code></li>]
 *     <li>other defaults are unset after construction</li>
 * </ul>
 */
public class MessageDefaults implements MessageDefaultsProvider {
    private String type;
    private String logger;
    private Integer severity;
    private String payload;
    private Integer pid;
    private String hostname;
    private String envVersion;

    public MessageDefaults() {
        initHostname();
        initPid();
    }

    private void initHostname() {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = null;
        }
    }

    private void initPid() {
        // This is a hack
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        this.pid = Integer.parseInt(pid);
    }

    /**
     * Returns a random (type 4) UUID.
     */
    public UUID getUuid() {
        return UUID.randomUUID();
    }

    /**
     * Returns the current Unix time in nanoseconds.
     */
    public Long getTimestamp() {
        // nsec
        return System.currentTimeMillis() * 1000000;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getEnvVersion() {
        return envVersion;
    }

    public void setEnvVersion(String envVersion) {
        this.envVersion = envVersion;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
