package io.nanovc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Tests for {@link DifferenceState}'s.
 */
public class DifferenceStateTests extends NanoVersionControlTestsBase
{
    /**
     * Makes sure that we have the expected {@link DifferenceState}'s.
     */
    @Test
    public void testDifferenceStates()
    {
        assertArrayEquals(
            new DifferenceState[] {
                DifferenceState.CHANGED,
                DifferenceState.ADDED,
                DifferenceState.DELETED,
            },
            DifferenceState.values()
        );
    }
}
