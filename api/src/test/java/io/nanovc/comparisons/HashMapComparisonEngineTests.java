package io.nanovc.comparisons;

import io.nanovc.Comparison;
import io.nanovc.ComparisonState;
import io.nanovc.areas.StringArea;
import io.nanovc.areas.StringHashMapArea;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link HashMapComparisonEngine}.
 */
public class HashMapComparisonEngineTests
{
    /**
     * Tests that we can create the {@link HashMapComparisonEngine}.
     */
    @Test
    public void testCreation()
    {
        HashMapComparisonEngine engine;
        engine = new HashMapComparisonEngine();
    }

    /**
     * Tests the comparison between two empty areas.
     */
    @Test
    public void comparisonBetweenEmptyAreas()
    {
        assertComparison(
            firstArea -> {},
            secondArea -> {},
            comparison -> {
                // Make sure there are no comparisons:
                assertFalse(comparison.hasComparisons(), "We weren't expecting there to be any comparisons.");
            },
            ""
        );
    }

    /**
     * Tests the comparison between an empty area and another area with 1 piece of content.
     */
    @Test
    public void comparison_Empty_aA()
    {
        assertComparison(
            firstArea -> {},
            secondArea -> secondArea.putString("/a", "A"),
            comparison -> {
                // Make sure there is one comparison entry because there was only one piece of content:
                assertTrue(comparison.hasComparisons(), "We were expecting there to be comparisons.");
                assertEquals(1, comparison.getComparisonStream().count(), "We were expecting one comparison.");
                assertEquals(ComparisonState.ADDED, comparison.getComparison("/a"), "We were expecting the content to be added.");
            },
            "/a : Added"
        );
    }

    /**
     * Tests the comparison between an empty area with the same content is unchanged.
     */
    @Test
    public void comparison_aA_aA()
    {
        assertComparison(
            firstArea -> firstArea.putString("/a", "A"),
            secondArea -> secondArea.putString("/a", "A"),
            comparison -> {
                assertTrue(comparison.hasComparisons(), "We were expecting there to be comparisons.");
                assertEquals(1, comparison.getComparisonStream().count(), "We were expecting one comparison.");
                assertEquals(ComparisonState.UNCHANGED, comparison.getComparison("/a"), "We were expecting the content to be unchanged.");
            },
            "/a : Unchanged"
        );
    }

    /**
     * Tests the comparison between an area with one piece of content and an empty area picks up that the content was deleted.
     */
    @Test
    public void comparison_aA_Empty()
    {
        assertComparison(
            firstArea -> firstArea.putString("/a", "A"),
            secondArea -> {},
            comparison -> {
                assertTrue(comparison.hasComparisons(), "We were expecting there to be comparisons.");
                assertEquals(1, comparison.getComparisonStream().count(), "We were expecting one comparison.");
                assertEquals(ComparisonState.DELETED, comparison.getComparison("/a"), "We were expecting the content to be deleted.");
            },
            "/a : Deleted"
        );
    }

    /**
     * Tests that a change to value of content at the same path gives us a change comparison.
     */
    @Test
    public void comparison_aA_aB()
    {
        assertComparison(
            firstArea -> firstArea.putString("/a", "A"),
            secondArea -> secondArea.putString("/a", "B"),
            comparison -> {
                assertTrue(comparison.hasComparisons(), "We were expecting there to be comparisons.");
                assertEquals(1, comparison.getComparisonStream().count(), "We were expecting one comparison.");
                assertEquals(ComparisonState.CHANGED, comparison.getComparison("/a"), "We were expecting the content to be changed.");
            },
            "/a : Changed"
        );
    }

    /**
     * Asserts that the given areas produce the expected comparison.
     * This is a common implementation which creates the engine, initializes two areas, computes the comparison and checks that the comparison is as expected.
     *
     * @param firstAreaInitializer     The logic to initialize the state of the first area to be compared.
     * @param secondAreaInitializer    The logic to initialize the state of the second area to be compared.
     * @param comparisonConsumer       The comparison that was computed by the engine for the two areas.
     * @param expectedComparisonString The expected list string for the comparison.
     */
    public void assertComparison(Consumer<StringArea> firstAreaInitializer, Consumer<StringArea> secondAreaInitializer, Consumer<Comparison> comparisonConsumer, String expectedComparisonString)
    {
        // Create the engine under test:
        HashMapComparisonEngine engine = new HashMapComparisonEngine();

        // Create the first area to compare:
        StringHashMapArea firstArea = new StringHashMapArea();

        // Initialize the first area:
        firstAreaInitializer.accept(firstArea);

        // Create the second area to compare:
        StringHashMapArea secondArea = new StringHashMapArea();

        // Initialize the second area:
        secondAreaInitializer.accept(secondArea);

        // Get the comparisons between the two areas:
        Comparison comparison = engine.compare(firstArea, secondArea);

        // Make sure we got a comparison structure:
        assertNotNull(comparison, "We were expecting a comparison to be computed that was empty. Instead, we got null.");

        // Assert the comparison structure:
        comparisonConsumer.accept(comparison);

        // Make sure that the comparison is exactly the shape that we expected as a list string:
        assertEquals(expectedComparisonString, comparison.asListString());
    }
}
