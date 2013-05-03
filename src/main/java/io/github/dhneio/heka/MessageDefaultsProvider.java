package io.github.dhneio.heka;

import java.util.UUID;

/**
 * Provides default values for use at {@link Message} construction time.
 */
public interface MessageDefaultsProvider {
    public UUID getUuid();
    public Long getTimestamp();
    public String getType();
    public String getLogger();
    public Integer getSeverity();
    public String getPayload();
    public String getEnvVersion();
    public Integer getPid();
    public String getHostname();
}
