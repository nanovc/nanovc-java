package io.nanovc.differences;

import io.nanovc.DifferenceState;
import io.nanovc.NanoVersionControlTestsBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for a {@link HashMapDifference}.
 */
public class HashMapDifferenceTests extends NanoVersionControlTestsBase
{
    /**
     * Tests that we can create the {@link HashMapDifference}.
     */
    @Test
    public void testCreation()
    {
        HashMapDifference difference;
        difference = new HashMapDifference();
    }

    /**
     * Tests the API for the {@link HashMapDifference}.
     */
    @Test
    public void testAPI()
    {
        // Create the difference:
        HashMapDifference difference = new HashMapDifference();

        // Make sure the difference is empty to begin with:
        assertFalse(difference.hasDifferences());
        assertEquals(0, difference.size());

        // Put some differences:
        difference.putDifference("/", DifferenceState.CHANGED);
        assertTrue(difference.hasDifferences());
        assertEquals(1, difference.size());

        difference.putDifference("/Changed", DifferenceState.CHANGED);
        difference.putDifference("/Added", DifferenceState.ADDED);
        difference.putDifference("/Deleted", DifferenceState.DELETED);
        assertEquals(4, difference.size());

        // Make sure that the structure looks as expected:
        String expected = "/ : Changed\n" +
                          "/Added : Added\n" +
                          "/Changed : Changed\n" +
                          "/Deleted : Deleted";
        assertEquals(expected, difference.asListString());
    }
}
