package io.github.dhneio.heka.filters;

import io.github.dhneio.heka.MessageFilter;
import io.github.dhneio.heka.Message;

/**
 * Filters out messages that are above a certain severity.
 */
public class MaxSeverityFilter implements MessageFilter {
    private int maxSeverity;

    public MaxSeverityFilter(int maxSeverity) {
        this.maxSeverity = maxSeverity;
    }

    public int getMaxSeverity() {
        return maxSeverity;
    }

    public void setMaxSeverity(int maxSeverity) {
        this.maxSeverity = maxSeverity;
    }

    @Override
    public boolean filter(Message message) {
        Integer severity = message.getSeverity();
        return severity == null || severity <= maxSeverity;
    }
}
