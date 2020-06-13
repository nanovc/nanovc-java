package io.nanovc.comparisons;

import io.nanovc.ComparisonState;
import io.nanovc.NanoVersionControlTestsBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link HashMapComparison}'s.
 */
public class HashMapComparisonTests extends NanoVersionControlTestsBase
{
    /**
     * Tests that we can create the {@link HashMapComparison}.
     */
    @Test
    public void testCreation()
    {
        HashMapComparison comparison;
        comparison = new HashMapComparison();
    }

    /**
     * Tests the API for the {@link HashMapComparison}.
     */
    @Test
    public void testAPI()
    {
        // Create the comparison:
        HashMapComparison comparison = new HashMapComparison();

        // Make sure the comparison is empty to begin with:
        assertFalse(comparison.hasComparisons());
        assertEquals(0, comparison.size());

        // Put some comparisons:
        comparison.putComparison("/", ComparisonState.CHANGED);
        assertTrue(comparison.hasComparisons());
        assertEquals(1, comparison.size());

        comparison.putComparison("/Unchanged", ComparisonState.UNCHANGED);
        comparison.putComparison("/Changed", ComparisonState.CHANGED);
        comparison.putComparison("/Added", ComparisonState.ADDED);
        comparison.putComparison("/Deleted", ComparisonState.DELETED);
        assertEquals(5, comparison.size());

        // Make sure that the structure looks as expected:
        String expected = "/ : Changed\n" +
                          "/Added : Added\n" +
                          "/Changed : Changed\n" +
                          "/Deleted : Deleted\n" +
                          "/Unchanged : Unchanged";
        assertEquals(expected, comparison.asListString());
    }
}
