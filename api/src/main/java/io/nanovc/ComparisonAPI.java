/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * A comparison between two {@link AreaAPI}'s of {@link ContentAPI}.
 * A comparison includes information about unchanged {@link ContentAPI}.
 * The comparison contains an entry for each {@link PathAPI} in both {@link AreaAPI}'s
 * and a corresponding {@link ComparisonState} to indicate how the content compares between the two {@link AreaAPI}'s.
 *
 * This structure is useful to understand the complete picture between the two {@link AreaAPI}'s,
 * even for content that has had no changes.
 * For a structure that doesn't have unchanged entries, see {@link DifferenceAPI}.
 */
public interface ComparisonAPI extends Iterable<ComparisonEntry>
{
    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param path    The path in the repo where the content must be put.
     * @param state   The state of the comparison for this path.
     */
    void putComparison(RepoPath path, ComparisonState state);

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param absolutePath The absolute path in the repo where the content must be put.
     * @param state   The state of the comparison for this path.
     */
    void putComparison(String absolutePath, ComparisonState state);

    /**
     * Gets the state of the comparison at the given path.
     *
     * @param path The path of the state of the content to get.
     * @return The state of the comparison at the given path. Null if there is no state at the path.
     */
    ComparisonState getComparison(RepoPath path);

    /**
     * Gets the state of the comparison at the given path.
     *
     * @param absolutePath The absolute path in the repo of the state of the content to get.
     * @return The state of the comparison at the given path. Null if there is no state at the path.
     */
    ComparisonState getComparison(String absolutePath);

    /**
     * Removes the state of the content at the given path if there is content.
     *
     * @param path The path in the repo of the state of the content to remove.
     */
    void removeComparison(RepoPath path);

    /**
     * Removes the state of the content at the given path if there is content.
     *
     * @param absolutePath The absolute path in the repo of the state of the content to remove.
     */
    void removeComparison(String absolutePath);

    /**
     * Replaces all the content with the given stream of content.
     * This should clear all the existing content in the area and replace it with only the given content.
     *
     * @param entryStream The comparison entries to replace all the current comparison entries with.
     */
    void replaceAllComparisons(Stream<ComparisonEntry> entryStream);

    /**
     * Returns true if there are any comparisons or false if it is empty.
     *
     * @return true if any comparisons exist.
     */
    boolean hasComparisons();

    /**
     * Gets the stream of all the comparisons.
     *
     * @return The stream of comparisons.
     */
    Stream<ComparisonEntry> getComparisonStream();

    /**
     * Gets the structure of this comparison as a list of paths.
     * This is useful for debugging.
     * New lines are made with '\n' instead of the system line separator, so that it's easier to unit test with.
     * @return The structure of this area as a list of paths.
     */
    default String asListString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        // Get the stream of content:
        Stream<ComparisonEntry> stream = getComparisonStream();

        // Make sure it's sorted by the absolute path:
        stream = stream.sorted(Comparator.comparing(areaEntry -> areaEntry.path.toAbsolutePath().path));

        // Print out the structure:
        stream.forEachOrdered(entry ->
                                     {
                                         // Add a line separator if necessary:
                                         if (stringBuilder.length() > 0) stringBuilder.append("\n");

                                         // Append the path:
                                         stringBuilder.append(entry.path.toAbsolutePath().path);
                                         stringBuilder.append(" : ");
                                         stringBuilder.append(entry.state.prettyName);
                                     });

        return stringBuilder.toString();
    }
}
