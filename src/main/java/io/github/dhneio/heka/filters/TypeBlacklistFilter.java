package io.github.dhneio.heka.filters;

import io.github.dhneio.heka.MessageFilter;
import io.github.dhneio.heka.Message;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Filters out a message if its type is in a blacklist.
 */
public class TypeBlacklistFilter implements MessageFilter {
    private final CopyOnWriteArraySet<String> blacklist;

    public TypeBlacklistFilter() {
        blacklist = new CopyOnWriteArraySet<String>();
    }

    public void addType(String type) {
        blacklist.add(type);
    }

    public void removeType(String type) {
        blacklist.remove(type);
    }

    public Set<String> getTypes() {
        return new HashSet<String>(blacklist);
    }

    @Override
    public boolean filter(Message message) {
        return !blacklist.contains(message.getType());
    }
}
