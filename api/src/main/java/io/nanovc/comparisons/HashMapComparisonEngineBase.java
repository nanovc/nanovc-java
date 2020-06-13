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

import java.util.Arrays;

/**
 * A base class for a comparison engine that uses hash maps to compute the comparisons between two {@link Area}'s of {@link Content}.
 * This {@link HashMapComparisonEngineBase} does not contain any state. Just the logic of how to compute the comparisons.
 * This is good where one {@link HashMapComparisonEngineBase} is going to be reused across many {@link HashMapComparisonHandler}'s.
 * This {@link HashMapComparisonEngineBase} is thread safe because it is stateless.
 * It is designed to be able to compute many comparisons between {@link Area}'s.
 */
public abstract class HashMapComparisonEngineBase extends ComparisonEngineBase implements HashMapComparisonEngineAPI
{

    /**
     * Computes a comparison between the given areas.
     *
     * @param fromArea The first area to find comparisons from.
     * @param toArea   The second are to find comparisons to.
     * @return The comparisons between the given areas.
     */
    public Comparison compare(Area<? extends Content> fromArea, Area<? extends Content> toArea)
    {
        // Create the comparison:
        HashMapComparison comparison = new HashMapComparison();

        // Go through each piece of content in the first area:
        for (AreaEntry<? extends Content> fromEntry : fromArea)
        {
            // Get the path that we are looking at:
            RepoPath path = fromEntry.path;

            // Check whether the second area has this path:
            Content toContent = toArea.getContent(path);
            if (toContent == null)
            {
                // There was no content in the "to" area, thus implying that it was deleted.
                comparison.putComparison(path, ComparisonState.DELETED);
            }
            else
            {
                // There was content at the same path. We don't know if it is the same or not yet.

                // Get the "from" content so we can compare it to the other area:
                Content fromContent = fromEntry.content;

                // Get the bytes for each piece of content:
                byte[] fromBytes = fromContent.asByteArray();
                byte[] toBytes = toContent.asByteArray();

                // Check whether the content is the same:
                boolean isContentEqual = Arrays.equals(fromBytes, toBytes);

                // Set the status of the comparison based on the equality of the content:
                comparison.putComparison(path, isContentEqual ? ComparisonState.UNCHANGED : ComparisonState.CHANGED);
            }
        }
        // Now we know what content was deleted, changed and unchanged. We don't know what was added.

        // Go through each entry in the "to" area to see if anything was added:
        for (AreaEntry<? extends Content> toEntry : toArea)
        {
            // Get the absolute path of this entry:
            String absolutePath = toEntry.path.toAbsolutePath().path;

            // Check whether we have already processed this path:
            if (!comparison.containsKey(absolutePath))
            {
                // We don't have this entry yet, thus implying that it was added.

                // Flag that it was added:
                comparison.putComparison(toEntry.path, ComparisonState.ADDED);
            }
        }
        // Now we have found all of the additions.

        return comparison;
    }
}

