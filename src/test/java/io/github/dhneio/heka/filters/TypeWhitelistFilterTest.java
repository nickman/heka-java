package io.github.dhneio.heka.filters;

import io.github.dhneio.heka.Message;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TypeWhitelistFilterTest {
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
    public void testEmptyWhitelist() {
        TypeWhitelistFilter filter = new TypeWhitelistFilter();

        assertFalse(filter.filter(nullMsg));
        assertFalse(filter.filter(fooMsg));
        assertFalse(filter.filter(barMsg));
        assertFalse(filter.filter(bazMsg));
    }

    @Test
    public void testWhitelist() {
        TypeWhitelistFilter filter = new TypeWhitelistFilter();
        filter.add("foo");
        filter.add("bar");

        assertFalse(filter.filter(nullMsg));
        assertTrue(filter.filter(fooMsg));
        assertTrue(filter.filter(barMsg));
        assertFalse(filter.filter(bazMsg));
    }

    @Test
    public void testWhitelistWithNull() {
        TypeWhitelistFilter filter = new TypeWhitelistFilter();
        filter.add(null);

        assertTrue(filter.filter(nullMsg));
        assertFalse(filter.filter(fooMsg));
        assertFalse(filter.filter(barMsg));
        assertFalse(filter.filter(bazMsg));
    }
}
