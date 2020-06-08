package io.nanovc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Tests for {@link ComparisonState}'s.
 */
public class ComparisonStateTests extends NanoVersionControlTestsBase
{
    /**
     * Makes sure that we have the expected {@link ComparisonState}'s.
     */
    @Test
    public void testComparisonStates()
    {
        assertArrayEquals(
            new ComparisonState[] {
                ComparisonState.UNCHANGED,
                ComparisonState.CHANGED,
                ComparisonState.ADDED,
                ComparisonState.DELETED,
            },
            ComparisonState.values()
        );
    }

}
