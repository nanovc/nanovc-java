/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.comparisons;

import io.nanovc.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * A comparison that is backed by a hash map.
 * The key is the absolute repo path for the content.
 * The value is the {@link ComparisonState}.
 */
public class HashMapComparison
    extends HashMap<String, ComparisonState>
    implements ComparisonAPI
{
    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param path    The path in the repo where the content must be put.
     * @param state   The state of the comparison for this path.
     */
    @Override
    public void putComparison(RepoPath path, ComparisonState state)
    {
        this.put(path.toAbsolutePath().path, state);
    }

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param absolutePath The absolute path in the repo where the content must be put.
     * @param state   The state of the comparison for this path.
     */
    @Override
    public void putComparison(String absolutePath, ComparisonState state)
    {
        this.put(absolutePath, state);
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
        return this.get(path.toAbsolutePath().path);
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
        return this.get(absolutePath);
    }

    /**
     * Removes the state of the content at the given path if there is content.
     *
     * @param path The path in the repo of the state of the content to remove.
     */
    @Override
    public void removeComparison(RepoPath path)
    {
        this.remove(path.toAbsolutePath().path);
    }

    /**
     * Removes the state of the content at the given path if there is content.
     *
     * @param absolutePath The absolute path in the repo of the state of the content to remove.
     */
    @Override
    public void removeComparison(String absolutePath)
    {
        this.remove(absolutePath);
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
        this.clear();
        entryStream.forEach(entry -> this.putComparison(entry.path, entry.state));
    }

    /**
     * Returns an iterator over all the comparison entries.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<ComparisonEntry> iterator()
    {
        return new Iterator<ComparisonEntry>()
        {
            private final Iterator<Entry<String, ComparisonState>> entrySetIterator = entrySet().iterator();

            @Override
            public boolean hasNext()
            {
                return entrySetIterator.hasNext();
            }

            @Override
            public ComparisonEntry next()
            {
                // Get the next entry:
                Entry<String, ComparisonState> nextEntry = entrySetIterator.next();

                return new ComparisonEntry(RepoPath.at(nextEntry.getKey()), nextEntry.getValue());
            }
        };
    }

    /**
     * Returns true if there are any comparisons or false if it is empty.
     *
     * @return true if any comparisons exist.
     */
    @Override
    public boolean hasComparisons()
    {
        return this.size() > 0;
    }

    /**
     * Gets the stream of all the comparisons.
     *
     * @return The stream of comparisons.
     */
    @Override
    public Stream<ComparisonEntry> getComparisonStream()
    {
        return this.entrySet().stream().map(entry -> new ComparisonEntry(RepoPath.at(entry.getKey()), entry.getValue()));
    }
}
