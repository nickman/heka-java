package io.github.dhneio.heka.filters;

import io.github.dhneio.heka.Message;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TypeBlacklistFilterTest {
    public Message nullMsg, fooMsg, barMsg, bazMsg;

    @Before
    public void setupMessages() {
        nullMsg = mock(Message.class);
        when(nullMsg.getType()).thenReturn(null);

        fooMsg = mock(Message.class);
        when(fooMsg.getType()).thenReturn("foo");

        barMsg = mock(Message.class);
        when(barMsg.getType()).thenReturn("bar");

        bazMsg = mock(Message.class);
        when(bazMsg.getType()).thenReturn("baz");
    }

    @Test
    public void testEmptyBlacklist() {
        TypeBlacklistFilter filter = new TypeBlacklistFilter();

        assertTrue(filter.filter(nullMsg));
        assertTrue(filter.filter(fooMsg));
        assertTrue(filter.filter(barMsg));
        assertTrue(filter.filter(bazMsg));
    }

    @Test
    public void testBlacklist() {
        TypeBlacklistFilter filter = new TypeBlacklistFilter();
        filter.add("foo");
        filter.add("bar");

        assertTrue(filter.filter(nullMsg));
        assertFalse(filter.filter(fooMsg));
        assertFalse(filter.filter(barMsg));
        assertTrue(filter.filter(bazMsg));
    }

    @Test
    public void testBlacklistWithNull() {
        TypeBlacklistFilter filter = new TypeBlacklistFilter();
        filter.add(null);

        assertFalse(filter.filter(nullMsg));
        assertTrue(filter.filter(fooMsg));
        assertTrue(filter.filter(barMsg));
        assertTrue(filter.filter(bazMsg));
    }
}
