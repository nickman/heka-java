package io.github.dhneio.heka.filters;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import io.github.dhneio.heka.Message;
import org.junit.Before;
import org.junit.Test;

public class MaxSeverityFilterTest {
    MaxSeverityFilter filter0, filter1, filter2, filter3, filter4, filter5, filter6, filter7;

    @Before
    public void setupFilters() {
        filter0 = new MaxSeverityFilter(0);
        filter1 = new MaxSeverityFilter(1);
        filter2 = new MaxSeverityFilter(2);
        filter3 = new MaxSeverityFilter(3);
        filter4 = new MaxSeverityFilter(4);
        filter5 = new MaxSeverityFilter(5);
        filter6 = new MaxSeverityFilter(6);
        filter7 = new MaxSeverityFilter(7);
    }

    @Test
    public void testPassesNullSeverity() {
        // Is this actually desired behavior?
        Message msgMock = mock(Message.class);
        when(msgMock.getSeverity()).thenReturn(null);

        assertTrue(filter0.filter(msgMock));
        assertTrue(filter1.filter(msgMock));
        assertTrue(filter2.filter(msgMock));
        assertTrue(filter3.filter(msgMock));
        assertTrue(filter4.filter(msgMock));
        assertTrue(filter5.filter(msgMock));
        assertTrue(filter6.filter(msgMock));
        assertTrue(filter7.filter(msgMock));
    }

    @Test
    public void testFilter4() {
        Message msgMock = mock(Message.class);
        when(msgMock.getSeverity()).thenReturn(new Integer(4));

        assertFalse(filter0.filter(msgMock));
        assertFalse(filter1.filter(msgMock));
        assertFalse(filter2.filter(msgMock));
        assertFalse(filter3.filter(msgMock));
        assertTrue(filter4.filter(msgMock));
        assertTrue(filter5.filter(msgMock));
        assertTrue(filter6.filter(msgMock));
        assertTrue(filter7.filter(msgMock));
    }

    @Test
    public void testModification() {
        Message msgMock = mock(Message.class);
        when(msgMock.getSeverity()).thenReturn(new Integer(4));

        assertEquals(4, filter4.getMaxSeverity());
        filter4.setMaxSeverity(3);
        assertEquals(3, filter4.getMaxSeverity());
        assertFalse(filter4.filter(msgMock));
    }
}
