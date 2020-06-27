package io.nanovc.comparisons;

import io.nanovc.ComparisonAPI;
import io.nanovc.areas.StringHashMapArea;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link HashMapComparisonHandler}.
 */
public class HashMapComparisonHandlerTests
{
    /**
     * Tests that we can create the {@link HashMapComparisonHandler}.
     */
    @Test
    public void testCreation()
    {
        HashMapComparisonHandler handler;
        handler = new HashMapComparisonHandler();
        handler = new HashMapComparisonHandler(new HashMapComparisonEngine());
    }

    /**
     * Tests the comparison between two empty areas.
     */
    @Test
    public void comparisonBetweenEmptyAreas()
    {
        // Create the engine under test:
        HashMapComparisonHandler handler = new HashMapComparisonHandler();

        // Create the first area to compare:
        StringHashMapArea firstArea = new StringHashMapArea();

        // Create the second area to compare:
        StringHashMapArea secondArea = new StringHashMapArea();

        // Get the comparisons between the two areas:
        ComparisonAPI comparison = handler.compare(firstArea, secondArea);

        // Make sure we got a comparison structure:
        assertNotNull(comparison, "We were expecting a comparison to be computed that was empty. Instead, we got null.");

        // Make sure there are no comparisons:
        assertFalse(comparison.hasComparisons(), "We weren't expecting there to be any comparisons.");
        assertEquals("", comparison.asListString());
    }
}
