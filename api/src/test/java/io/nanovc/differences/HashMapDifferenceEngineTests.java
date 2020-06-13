package io.nanovc.differences;

import io.nanovc.Difference;
import io.nanovc.DifferenceState;
import io.nanovc.areas.StringArea;
import io.nanovc.areas.StringHashMapArea;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link HashMapDifferenceEngine}.
 */
public class HashMapDifferenceEngineTests
{
    /**
     * Tests that we can create the {@link HashMapDifferenceEngine}.
     */
    @Test
    public void testCreation()
    {
        HashMapDifferenceEngine engine;
        engine = new HashMapDifferenceEngine();
    }

    /**
     * Tests the difference between two empty areas.
     */
    @Test
    public void differenceBetweenEmptyAreas()
    {
        assertDifference(
            firstArea -> {},
            secondArea -> {},
            difference -> {
                // Make sure there are no differences:
                assertFalse(difference.hasDifferences(), "We weren't expecting there to be any differences.");
            },
            ""
        );
    }

    /**
     * Tests the difference between an empty area and another area with 1 piece of content.
     */
    @Test
    public void difference_Empty_aA()
    {
        assertDifference(
            firstArea -> {},
            secondArea -> secondArea.putString("/a", "A"),
            difference -> {
                // Make sure there is one difference entry because there was only one piece of content:
                assertTrue(difference.hasDifferences(), "We were expecting there to be differences.");
                assertEquals(1, difference.getDifferenceStream().count(), "We were expecting one difference.");
                assertEquals(DifferenceState.ADDED, difference.getDifference("/a"), "We were expecting the content to be added.");
            },
            "/a : Added"
        );
    }

    /**
     * Tests the difference between an empty area with the same content is unchanged.
     */
    @Test
    public void difference_aA_aA()
    {
        assertDifference(
            firstArea -> firstArea.putString("/a", "A"),
            secondArea -> secondArea.putString("/a", "A"),
            difference -> {
                assertFalse (difference.hasDifferences(), "We were expecting there to be no differences because the content is the same.");
                assertEquals(0, difference.getDifferenceStream().count(), "We were expecting there to be no differences because the content is the same.");
            },
            ""
        );
    }

    /**
     * Tests the difference between an area with one piece of content and an empty area picks up that the content was deleted.
     */
    @Test
    public void difference_aA_Empty()
    {
        assertDifference(
            firstArea -> firstArea.putString("/a", "A"),
            secondArea -> {},
            difference -> {
                assertTrue  (difference.hasDifferences(), "We were expecting there to be differences.");
                assertEquals(difference.getDifferenceStream().count(), 1, "We were expecting one difference.");
                assertEquals(DifferenceState.DELETED, difference.getDifference("/a"), "We were expecting the content to be deleted.");
            },
            "/a : Deleted"
        );
    }

    /**
     * Tests that a change to value of content at the same path gives us a change difference.
     */
    @Test
    public void difference_aA_aB()
    {
        assertDifference(
            firstArea -> firstArea.putString("/a", "A"),
            secondArea -> secondArea.putString("/a", "B"),
            difference -> {
                assertTrue(difference.hasDifferences(), "We were expecting there to be differences.");
                assertEquals(1, difference.getDifferenceStream().count(), "We were expecting one difference.");
                assertEquals(DifferenceState.CHANGED, difference.getDifference("/a"), "We were expecting the content to be changed.");
            },
            "/a : Changed"
        );
    }

    /**
     * Asserts that the given areas produce the expected difference.
     * This is a common implementation which creates the engine, initializes two areas, computes the difference and checks that the difference is as expected.
     *
     * @param firstAreaInitializer     The logic to initialize the state of the first area to be compared.
     * @param secondAreaInitializer    The logic to initialize the state of the second area to be compared.
     * @param differenceConsumer       The difference that was computed by the engine for the two areas.
     * @param expectedDifferenceString The expected list string for the difference.
     */
    public void assertDifference(Consumer<StringArea> firstAreaInitializer, Consumer<StringArea> secondAreaInitializer, Consumer<Difference> differenceConsumer, String expectedDifferenceString)
    {
        // Create the engine under test:
        HashMapDifferenceEngine engine = new HashMapDifferenceEngine();

        // Create the first area to compare:
        StringHashMapArea firstArea = new StringHashMapArea();

        // Initialize the first area:
        firstAreaInitializer.accept(firstArea);

        // Create the second area to compare:
        StringHashMapArea secondArea = new StringHashMapArea();

        // Initialize the second area:
        secondAreaInitializer.accept(secondArea);

        // Get the differences between the two areas:
        Difference difference = engine.computeDifference(firstArea, secondArea);

        // Make sure we got a difference structure:
        assertNotNull(difference, "We were expecting a difference to be computed that was empty. Instead, we got null.");

        // Assert the difference structure:
        differenceConsumer.accept(difference);

        // Make sure that the difference is exactly the shape that we expected as a list string:
        assertEquals(expectedDifferenceString, difference.asListString());
    }

}
