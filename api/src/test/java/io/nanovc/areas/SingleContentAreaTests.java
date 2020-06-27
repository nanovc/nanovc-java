package io.nanovc.areas;

import io.nanovc.content.StringContent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This tests {@link SingleContentArea}'s.
 */
public class SingleContentAreaTests
{
    /**
     * Tests that we can create the {@link SingleContentArea}.
     */
    @Test
    public void testCreation()
    {
        new SingleContentArea<StringContent>();
    }

    /**
     * Tests the API for the {@link SingleContentArea}.
     */
    @Test
    public void testAPI()
    {
        // Create the area under test:
        SingleContentArea<StringContent> area = new SingleContentArea<>();

        // Make sure the area is empty to begin with:
        assertFalse(area.hasAnyContent());

        // Put some content in the area:
        area.putContent("/Hello", new StringContent("World"));

        // Make sure the area has content:
        assertTrue(area.hasAnyContent());
        assertEquals("/Hello : 'World'", area.asListString());

        // Put some content at another path in the area:
        area.putContent("/World", new StringContent("Hello"));

        // Make sure the area has content:
        assertTrue(area.hasAnyContent());
        assertEquals("/World : 'Hello'", area.asListString());
    }
}
