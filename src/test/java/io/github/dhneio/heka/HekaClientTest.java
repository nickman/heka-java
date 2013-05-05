package io.github.dhneio.heka;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HekaClientTest {
    @Test
    public void testCloseClosesTransport() {
        Transport mockTransport = mock(Transport.class);

        HekaClient hc = new HekaClient(mockTransport, null);
        hc.close();

        verify(mockTransport).close();
    }

    @Test
    public void testEventsHaveDefaultsSet() {
        Transport transport = mock(Transport.class);

        MessageDefaultsProvider defaults = mock(MessageDefaultsProvider.class);
        UUID uuid = UUID.randomUUID();
        long timestamp = 390;
        when(defaults.getUuid()).thenReturn(uuid);
        when(defaults.getTimestamp()).thenReturn(timestamp);

        HekaClient hc = new HekaClient(transport, defaults);
        assertEquals(defaults, hc.getDefaults());

        Message msg = hc.message().build();

        assertEquals(uuid, msg.getUuid());
        assertEquals(timestamp, msg.getTimestamp());

        msg = hc.message("a_type").build();
        assertEquals("a_type", msg.getType());

        hc.setDefaults(null);
        assertNull(hc.getDefaults());
    }

    @Test
    public void testCounterConstruction() {
        Transport transport = mock(Transport.class);

        HekaClient hc = new HekaClient(transport, new MessageDefaults());
        Message msg = hc.counter("ctr1").build();
        assertEquals("counter", msg.getType());
        assertEquals("1", msg.getPayload());

        msg = hc.counter("ctr1").incr(42).build();
        assertEquals("42", msg.getPayload());
    }

    @Test
    public void testFilterChain() {
        Message msg1 = mock(Message.class);
        Message msg2 = mock(Message.class);
        Message msg3 = mock(Message.class);

        MessageFilter failsMsg1 = mock(MessageFilter.class);
        when(failsMsg1.filter(msg1)).thenReturn(false);
        when(failsMsg1.filter(msg2)).thenReturn(true);
        when(failsMsg1.filter(msg3)).thenReturn(true);

        MessageFilter failsMsg2 = mock(MessageFilter.class);
        when(failsMsg2.filter(msg1)).thenReturn(true);
        when(failsMsg2.filter(msg2)).thenReturn(false);

        when(failsMsg2.filter(msg3)).thenReturn(true);
        Transport transport = mock(Transport.class);

        HekaClient hc = new HekaClient(transport, null);
        hc.addFilter(failsMsg1);
        hc.addFilter(failsMsg2);

        List<MessageFilter> filterChain = hc.getFilters();
        assertEquals(2, filterChain.size());
        assertEquals(failsMsg1, filterChain.get(0));
        assertEquals(failsMsg2, filterChain.get(1));

        hc.send(msg1);
        hc.send(msg2);
        hc.send(msg3);
        hc.send(msg1);
        hc.send(msg2);
        hc.send(msg3);

        verify(transport, never()).sendMessage(msg1);
        verify(transport, never()).sendMessage(msg2);
        verify(transport, times(2)).sendMessage(msg3);

        // verify short circuiting
        verify(failsMsg2, never()).filter(msg1);

        List<MessageFilter> filterList = new ArrayList<MessageFilter>();
        filterList.add(failsMsg1);
        filterList.add(failsMsg2);
        assertEquals(filterList, hc.getFilters());

        hc.removeFilter(failsMsg1);
        filterList.remove(failsMsg1);
        assertEquals(filterList, hc.getFilters());

        hc.clearFilters();
        assertTrue(hc.getFilters().isEmpty());
    }
}
