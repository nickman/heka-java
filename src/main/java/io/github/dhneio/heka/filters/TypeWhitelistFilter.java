package io.github.dhneio.heka.filters;

import io.github.dhneio.heka.MessageFilter;
import io.github.dhneio.heka.Message;

import java.util.HashSet;
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

    public void addType(String type) {
        whitelist.add(type);
    }

    public void removeType(String type) {
        whitelist.remove(type);
    }

    public Set<String> getTypes() {
        return new HashSet<String>(whitelist);
    }

    @Override
    public boolean filter(Message message) {
        return whitelist.contains(message.getType());
    }
}
