package io.nanovc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Comparison}'s.
 */
public class ComparisonTests extends NanoVersionControlTestsBase
{
    @Test
    public void testComparisonAPI()
    {
        // Create the comparison:
        MockComparison comparison = new MockComparison();

        // Make sure the comparison is empty to begin with:
        assertFalse(comparison.hasComparisons());

        // Put some comparisons:
        comparison.putComparison("/", ComparisonState.CHANGED);
        assertTrue(comparison.hasComparisons());

        comparison.putComparison("/Unchanged", ComparisonState.UNCHANGED);
        comparison.putComparison("/Changed", ComparisonState.CHANGED);
        comparison.putComparison("/Added", ComparisonState.ADDED);
        comparison.putComparison("/Deleted", ComparisonState.DELETED);

        // Make sure that the structure looks as expected:
        String expected = "/ : Changed\n" +
                          "/Added : Added\n" +
                          "/Changed : Changed\n" +
                          "/Deleted : Deleted\n" +
                          "/Unchanged : Unchanged";
        assertEquals(expected, comparison.asListString());
    }

    /**
     * A mock implementation of the {@link Comparison} API.
     */
    private static class MockComparison implements Comparison
    {
        /**
         * The internal storage for the comparison state.
         * The absolute path of the content is the key.
         */
        private final HashMap<String, ComparisonState> state = new HashMap<>();

        /**
         * Creates and puts the given content into this map.
         * If content at this path already exists, it is replaced.
         *
         * @param path  The path in the repo where the content must be put.
         * @param state The state of the comparison for this path.
         */
        @Override
        public void putComparison(RepoPath path, ComparisonState state)
        {
            this.state.put(path.toAbsolutePath().path, state);
        }

        /**
         * Creates and puts the given content into this map.
         * If content at this path already exists, it is replaced.
         *
         * @param absolutePath The absolute path in the repo where the content must be put.
         * @param state        The state of the comparison for this path.
         */
        @Override
        public void putComparison(String absolutePath, ComparisonState state)
        {
            this.state.put(absolutePath, state);
        }

        /**
         * Gets the state of the comparison at the given path.
         *
         * @param path The path of the state of the content to get.
         * @return The state of the comparison at the given path. Null if there is no state at the path.
         */
        @Override
        public ComparisonState getComparison(RepoPath path)
        {
            return this.state.get(path.toAbsolutePath().path);
        }

        /**
         * Gets the state of the comparison at the given path.
         *
         * @param absolutePath The absolute path in the repo of the state of the content to get.
         * @return The state of the comparison at the given path. Null if there is no state at the path.
         */
        @Override
        public ComparisonState getComparison(String absolutePath)
        {
            return this.state.get(absolutePath);
        }

        /**
         * Removes the state of the content at the given path if there is content.
         *
         * @param path The path in the repo of the state of the content to remove.
         */
        @Override
        public void removeComparison(RepoPath path)
        {
            this.state.remove(path.toAbsolutePath().path);
        }

        /**
         * Removes the state of the content at the given path if there is content.
         *
         * @param absolutePath The absolute path in the repo of the state of the content to remove.
         */
        @Override
        public void removeComparison(String absolutePath)
        {
            this.state.remove(absolutePath);
        }

        /**
         * Replaces all the content with the given stream of content.
         * This should clear all the existing content in the area and replace it with only the given content.
         *
         * @param entryStream The comparison entries to replace all the current comparison entries with.
         */
        @Override
        public void replaceAllComparisons(Stream<ComparisonEntry> entryStream)
        {
            this.state.clear();
            entryStream.forEach(comparisonEntry -> this.putComparison(comparisonEntry.path, comparisonEntry.state));
        }

        /**
         * Returns true if there are any comparisons or false if it is empty.
         *
         * @return true if any comparisons exist.
         */
        @Override
        public boolean hasComparisons()
        {
            return this.state.size() > 0;
        }

        /**
         * Returns an iterator over elements of type {@code T}.
         *
         * @return an Iterator.
         */
        @Override
        public Iterator<ComparisonEntry> iterator()
        {
            return new Iterator<ComparisonEntry>()
            {

                /**
                 * The iterator for the state map.
                 */
                private final Iterator<Map.Entry<String, ComparisonState>> entrySetIterator = state.entrySet().iterator();


                @Override
                public boolean hasNext()
                {
                    return entrySetIterator.hasNext();
                }

                @Override
                public ComparisonEntry next()
                {
                    // Get the next entry:
                    Map.Entry<String, ComparisonState> nextEntry = entrySetIterator.next();

                    return new ComparisonEntry(RepoPath.at(nextEntry.getKey()), nextEntry.getValue());
                }
            };
        }

        /**
         * Gets the stream of all the comparisons.
         *
         * @return The stream of comparisons.
         */
        @Override
        public Stream<ComparisonEntry> getComparisonStream()
        {
            return this.state.entrySet().stream().map(entry -> new ComparisonEntry(RepoPath.at(entry.getKey()), entry.getValue()));
        }
    }
}
