package io.github.dhneio.heka.filters;

import io.github.dhneio.heka.MessageFilter;
import io.github.dhneio.heka.Message;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Only passes a message if its type is in a whitelist.
 */
public class TypeWhitelistFilter implements MessageFilter {
    private final Set<String> whitelist;

    public TypeWhitelistFilter() {
        whitelist = new CopyOnWriteArraySet<String>();
    }

    public void add(String type) {
        whitelist.add(type);
    }

    public void remove(String type) {
        whitelist.remove(type);
    }

    @Override
    public boolean filter(Message message) {
        return whitelist.contains(message.getType());
    }
}
