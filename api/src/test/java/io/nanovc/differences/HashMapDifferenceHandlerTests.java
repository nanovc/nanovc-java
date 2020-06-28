package io.nanovc.differences;

import io.nanovc.DifferenceAPI;
import io.nanovc.areas.StringHashMapArea;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link HashMapDifferenceHandler}.
 */
public class HashMapDifferenceHandlerTests
{
    /**
     * Tests that we can create the {@link HashMapDifferenceHandler}.
     */
    @Test
    public void testCreation()
    {
        HashMapDifferenceHandler handler;
        handler = new HashMapDifferenceHandler();
        handler = new HashMapDifferenceHandler(new HashMapDifferenceEngine());
    }

    /**
     * Tests the difference between two empty areas.
     */
    @Test
    public void differenceBetweenEmptyAreas()
    {
        // Create the engine under test:
        HashMapDifferenceHandler handler = new HashMapDifferenceHandler();

        // Create the first area to compare:
        StringHashMapArea firstArea = new StringHashMapArea();

        // Create the second area to compare:
        StringHashMapArea secondArea = new StringHashMapArea();

        // Get the differences between the two areas:
        DifferenceAPI difference = handler.computeDifference(firstArea, secondArea);

        // Make sure we got a difference structure:
        assertNotNull(difference, "We were expecting a difference to be computed that was empty. Instead, we got null.");

        // Make sure there are no differences:
        assertFalse(difference.hasDifferences(), "We weren't expecting there to be any differences.");
        assertEquals("", difference.asListString());
    }
}
