package io.nanovc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link DifferenceAPI}'s.
 */
public class DifferenceTests extends NanoVersionControlTestsBase
{
    @Test
    public void testDifferenceAPI()
    {
        // Create the difference:
        MockDifference difference = new MockDifference();

        // Make sure the difference is empty to begin with:
        assertFalse(difference.hasDifferences());

        // Put some differences:
        difference.putDifference("/", DifferenceState.CHANGED);
        assertTrue(difference.hasDifferences());

        difference.putDifference("/Changed", DifferenceState.CHANGED);
        difference.putDifference("/Added", DifferenceState.ADDED);
        difference.putDifference("/Deleted", DifferenceState.DELETED);

        // Make sure that the structure looks as expected:
        String expected = "/ : Changed\n" +
                          "/Added : Added\n" +
                          "/Changed : Changed\n" +
                          "/Deleted : Deleted";
        assertEquals(expected, difference.asListString());
    }

    /**
     * A mock implementation of the {@link DifferenceAPI} API.
     */
    private static class MockDifference implements DifferenceAPI
    {
        /**
         * The internal storage for the difference state.
         * The absolute path of the content is the key.
         */
        private final HashMap<String, DifferenceState> state = new HashMap<>();

        /**
         * Creates and puts the given content into this map.
         * If content at this path already exists, it is replaced.
         *
         * @param path  The path in the repo where the content must be put.
         * @param state The state of the difference for this path.
         */
        @Override
        public void putDifference(RepoPath path, DifferenceState state)
        {
            this.state.put(path.toAbsolutePath().path, state);
        }

        /**
         * Creates and puts the given content into this map.
         * If content at this path already exists, it is replaced.
         *
         * @param absolutePath The absolute path in the repo where the content must be put.
         * @param state        The state of the difference for this path.
         */
        @Override
        public void putDifference(String absolutePath, DifferenceState state)
        {
            this.state.put(absolutePath, state);
        }

        /**
         * Gets the state of the difference at the given path.
         *
         * @param path The path of the state of the content to get.
         * @return The state of the difference at the given path. Null if there is no state at the path.
         */
        @Override
        public DifferenceState getDifference(RepoPath path)
        {
            return this.state.get(path.toAbsolutePath().path);
        }

        /**
         * Gets the state of the difference at the given path.
         *
         * @param absolutePath The absolute path in the repo of the state of the content to get.
         * @return The state of the difference at the given path. Null if there is no state at the path.
         */
        @Override
        public DifferenceState getDifference(String absolutePath)
        {
            return this.state.get(absolutePath);
        }

        /**
         * Removes the state of the content at the given path if there is content.
         *
         * @param path The path in the repo of the state of the content to remove.
         */
        @Override
        public void removeDifference(RepoPath path)
        {
            this.state.remove(path.toAbsolutePath().path);
        }

        /**
         * Removes the state of the content at the given path if there is content.
         *
         * @param absolutePath The absolute path in the repo of the state of the content to remove.
         */
        @Override
        public void removeDifference(String absolutePath)
        {
            this.state.remove(absolutePath);
        }

        /**
         * Replaces all the content with the given stream of content.
         * This should clear all the existing content in the area and replace it with only the given content.
         *
         * @param entryStream The difference entries to replace all the current difference entries with.
         */
        @Override
        public void replaceAllDifferences(Stream<DifferenceEntry> entryStream)
        {
            this.state.clear();
            entryStream.forEach(differenceEntry -> this.putDifference(differenceEntry.path, differenceEntry.state));
        }

        /**
         * Returns true if there are any differences or false if it is empty.
         *
         * @return true if any differences exist.
         */
        @Override
        public boolean hasDifferences()
        {
            return this.state.size() > 0;
        }

        /**
         * Returns an iterator over elements of type {@code T}.
         *
         * @return an Iterator.
         */
        @Override
        public Iterator<DifferenceEntry> iterator()
        {
            return new Iterator<DifferenceEntry>()
            {

                /**
                 * The iterator for the state map.
                 */
                private final Iterator<Map.Entry<String, DifferenceState>> entrySetIterator = state.entrySet().iterator();


                @Override
                public boolean hasNext()
                {
                    return entrySetIterator.hasNext();
                }

                @Override
                public DifferenceEntry next()
                {
                    // Get the next entry:
                    Map.Entry<String, DifferenceState> nextEntry = entrySetIterator.next();

                    return new DifferenceEntry(RepoPath.at(nextEntry.getKey()), nextEntry.getValue());
                }
            };
        }

        /**
         * Gets the stream of all the differences.
         *
         * @return The stream of differences.
         */
        @Override
        public Stream<DifferenceEntry> getDifferenceStream()
        {
            return this.state.entrySet().stream().map(entry -> new DifferenceEntry(RepoPath.at(entry.getKey()), entry.getValue()));
        }
    }
}
