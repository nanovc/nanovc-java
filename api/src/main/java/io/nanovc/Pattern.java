/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * The interface for all patterns in Nano Version Control.
 * We use glob like syntax eg: *.json or **.json (to cross path boundaries).
 * A pattern lets you match many {@link Path}'s.
 * @param <TSelf> The specific type of pattern being implemented. This is needed so that we can get chained calls with the specific type of pattern.
 * @param <TPath> The specific type of path being matched. This is needed so that we can get chained calls with the specific type of pattern and path.
 */
public interface Pattern<TSelf extends Pattern<TSelf, TPath>, TPath extends Path<TPath>>
{

    /**
     * Finds all paths that match the pattern.
     * @param contentToSearch The content to search through
     * @return The content that matched the pattern.
     * @param <T> The specific type of content that we expect.
     */
    <T extends Content> List<AreaEntry<T>> match(Collection<AreaEntry<T>> contentToSearch);

    /**
     * Finds all paths that match the pattern.
     * @param contentToSearch The content to search through
     * @return The stream of content that matched the pattern.
     * @param <T> The specific type of content that we expect.
     */
    <T extends Content> Stream<AreaEntry<T>> matchStream(Stream<AreaEntry<T>> contentToSearch);

    /**
     * Checks whether the path of the content matches the pattern.
     *
     * @param areaEntryToCheck The content to check for a match.
     * @return True if the content matches the pattern.
     * @param <T> The specific type of content that we expect.
     */
    <T extends Content> boolean matches(AreaEntry<T> areaEntryToCheck);

    /**
     * Checks whether the path matches this pattern.
     *
     * @param path The path to check against this pattern.
     * @return True if the path  matches the pattern.
     */
    boolean matches(TPath path);

    /**
     * Checks whether the path matches this pattern.
     *
     * @param absolutePath The absolute path (starting with /) to check against this pattern.
     * @return True if the path matches the pattern.
     */
    boolean matches(String absolutePath);

    /**
     * Gets another pattern that matches this pattern OR the other pattern.
     * @param otherPattern The other pattern that we also want to match optionally.
     * @return A new pattern that matches this pattern OR the other pattern.
     */
    TSelf or(String otherPattern);

    /**
     * Gets another pattern that matches this pattern AND the other pattern.
     * @param otherPattern The other pattern that we must match.
     * @return A new pattern that matches this pattern AND the other pattern.
     */
    TSelf and(String otherPattern);

    /**
     * Gets the regex that can be used to match on the absolute paths of the content.
     * @return The pattern that the content paths are matched with.
     */
    java.util.regex.Pattern asRegex();
}
