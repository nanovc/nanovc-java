package io.nanovc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NanoVersionControlTests
{
    @Test
    public void testCreation()
    {
        NanoVersionControl nanoVersionControl = new NanoVersionControl();
    }

    @Test
    public void testURL()
    {
        assertEquals("http://nanovc.io", NanoVersionControl.URL);
    }
}
