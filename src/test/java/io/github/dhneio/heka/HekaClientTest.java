package io.github.dhneio.heka;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

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
        Message msg = hc.message().build();

        assertEquals(uuid, msg.getUuid());
        assertEquals(timestamp, msg.getTimestamp());
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

        hc.clearFilters();
        filterChain = hc.getFilters();
        assertTrue(filterChain.isEmpty());
    }
}
