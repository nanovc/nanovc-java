package io.nanovc.areas;

import io.nanovc.NanoVersionControlTestsBase;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the {@link ByteArrayHashMapArea}.
 */
public class ByteArrayHashMapAreaTests extends NanoVersionControlTestsBase
{
    /**
     * Tests that we can create the {@link ByteArrayHashMapArea}.
     */
    @Test
    public void testCreation()
    {
        ByteArrayHashMapArea area;
        area = new ByteArrayHashMapArea();
    }

    /**
     * Tests the API for the {@link ByteArrayHashMapArea}.
     */
    @Test
    public void testAPI()
    {
        // Create the area:
        ByteArrayHashMapArea area = new ByteArrayHashMapArea();

        // Make sure the area is empty to begin with:
        assertFalse(area.hasAnyContent());
        assertEquals(0, area.size());

        // Put some content:
        area.putBytes("/",  bytes("Hello World"));
        assertTrue(area.hasAnyContent());
        assertEquals(1, area.size());
        assertTrue(area.hasContent("/"));
        assertFalse(area.hasContent(""));
        assertFalse(area.hasContent("/BAD"));

        // Make sure that the structure looks as expected:
        String expected = "/ : byte[11] ➡ 'Hello World'";
        assertEquals(expected, area.asListString());
    }
}
