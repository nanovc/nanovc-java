/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.differences;

import io.nanovc.*;

import java.util.Arrays;

/**
 * A base class for a difference engine that uses hash maps to compute the differences between two {@link AreaAPI}'s of {@link ContentAPI}.
 * This {@link HashMapDifferenceEngineBase} does not contain any state. Just the logic of how to compute the differences.
 * This is good where one {@link HashMapDifferenceEngineBase} is going to be reused across many {@link HashMapDifferenceHandler}'s.
 * This {@link HashMapDifferenceEngineBase} is thread safe because it is stateless.
 * It is designed to be able to compute many differences between {@link AreaAPI}'s.
 */
public abstract class HashMapDifferenceEngineBase
    extends DifferenceEngineBase
    implements HashMapDifferenceEngineAPI
{

    /**
     * Computes a difference between the given areas.
     *
     * @param fromArea The first area to find differences from.
     * @param toArea   The second are to find differences to.
     * @return The differences between the given areas.
     */
    public DifferenceAPI computeDifference(AreaAPI<? extends ContentAPI> fromArea, AreaAPI<? extends ContentAPI> toArea)
    {
        // Create the difference:
        HashMapDifference difference = new HashMapDifference();

        // Go through each piece of content in the first area:
        for (AreaEntry<? extends ContentAPI> fromEntry : fromArea)
        {
            // Get the path that we are looking at:
            RepoPath path = fromEntry.path;

            // Check whether the second area has this path:
            ContentAPI toContent = toArea.getContent(path);
            if (toContent == null)
            {
                // There was no content in the "to" area, thus implying that it was deleted.
                difference.putDifference(path, DifferenceState.DELETED);
            }
            else
            {
                // There was content at the same path. We don't know if it is the same or not yet.

                // Get the "from" content so we can compare it to the other area:
                ContentAPI fromContent = fromEntry.content;

                // Get the bytes for each piece of content:
                byte[] fromBytes = fromContent.asByteArray();
                byte[] toBytes = toContent.asByteArray();

                // Check whether the content is the same:
                boolean isContentEqual = Arrays.equals(fromBytes, toBytes);

                // Set the status of the comparison based on the equality of the content:
                if (!isContentEqual) difference.putDifference(path, DifferenceState.CHANGED);
            }
        }
        // Now we know what content was deleted, changed and unchanged. We don't know what was added.

        // Go through each entry in the "to" area to see if anything was added:
        for (AreaEntry<? extends ContentAPI> toEntry : toArea)
        {
            // Get the path that we are looking at:
            RepoPath path = toEntry.path;

            // Get the absolute path of this entry:
            String absolutePath = path.toAbsolutePath().path;

            // Check whether we have already processed this path:
            if (!difference.containsKey(absolutePath))
            {
                // We don't have this entry yet, however it might not be there because the content was the same so we need to check.
                // Check whether the original from-area had this path, and if it did then it means that it was unchanged:
                if (!fromArea.hasContent(path))
                {
                    // The source area does not have content at this path, meaning that the content must have been the ADDED.
                    // Flag that it was added:
                    difference.putDifference(toEntry.path, DifferenceState.ADDED);
                }
            }
        }
        // Now we have found all of the additions.

        return difference;
    }
}

