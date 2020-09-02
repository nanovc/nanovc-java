/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.merges;

import io.nanovc.*;

/**
 * A base class for a merge engine that performs a diff between the common ancestor of the commits.
 */
public abstract class DiffFromCommonAncestorMergeEngineBase
    extends MergeEngineBase
    implements DiffFromCommonAncestorMergeEngineAPI
{
    /**
     * Merges the given changes between two commits into the given area.
     * This is a three way merge.
     *
     * @param mergedAreaToUpdate                      The content area where the resulting merged content should be placed. What ever is in this area after this call will be used for the merged commit.
     * @param commonAncestorCommit                    The first common ancestor of both commits being merged. This allows us to perform a three way diff to detect how to automatically merge changes.
     * @param sourceCommit                            The source commit that we are merging from.
     * @param destinationCommit                       The destination commit that we are merging to.
     * @param commonAncestorArea                      The content area for the common ancestor between the two commits.
     * @param sourceArea                              The content area at the source commit.
     * @param destinationArea                         The content area at the destination commit.
     * @param comparisonBetweenSourceAndDestination   The comparison between the source and destination content areas. This is useful to understand what the two areas look like and how they differ.
     * @param differenceBetweenAncestorAndSource      The difference between the content at the common ancestor and the source area. This is useful to understand what has changed between the two branches.
     * @param differenceBetweenAncestorAndDestination The difference between the content at the common ancestor and the destination area. This is useful to understand what has changed between the two branches.
     * @param contentFactory                          The factory to use for extracting content from the areas.
     * @param byteArrayIndex                          The byte array index to use when creating snap-shots for the content.
     * @param <TContent>                              The specific type of content to process.
     */
    @Override public <TContent extends ContentAPI> void mergeIntoAreaWithThreeWayDiff(AreaAPI<TContent> mergedAreaToUpdate, CommitAPI commonAncestorCommit, CommitAPI sourceCommit, CommitAPI destinationCommit, AreaAPI<TContent> commonAncestorArea, AreaAPI<TContent> sourceArea, AreaAPI<TContent> destinationArea, ComparisonAPI comparisonBetweenSourceAndDestination, DifferenceAPI differenceBetweenAncestorAndSource, DifferenceAPI differenceBetweenAncestorAndDestination, ContentFactory<TContent> contentFactory, ByteArrayIndex byteArrayIndex)
    {
        // First apply the destination content to the output content area:
        mergeArea(destinationArea, mergedAreaToUpdate, contentFactory, byteArrayIndex);
        // Now we have all the destination content.

        // Apply all the source changes on top of the result:
        for (DifferenceEntry differenceEntry : differenceBetweenAncestorAndSource)
        {
            // Get the path that we are at:
            RepoPath path = differenceEntry.path;

            // Check what change was made:
            switch (differenceEntry.state)
            {
                case ADDED:
                case CHANGED:
                {
                    // If the content was added or changed then the source value wins.

                    // Get the source content:
                    byte[] bytes = sourceArea.getContent(path).asByteArray();

                    // Pass the bytes through the byte index so that we can reuse existing bytes:
                    bytes = byteArrayIndex.addOrLookup(bytes);

                    // Create the output content:
                    TContent outputContent = contentFactory.createContent(bytes);

                    // Copy the content across:
                    mergedAreaToUpdate.putContent(path, outputContent);
                    break;
                }
                case DELETED:
                {
                    // If the content was deleted in the source then we delete it from the output.

                    // Remove the content from the output area:
                    mergedAreaToUpdate.removeContent(path);
                    break;
                }
            }
        }
    }

    /**
     * Merges the given changes between two commits into the given area.
     * This is a two way merge, which means that neither of the commits shared a common ancestor.
     *
     * @param mergedAreaToUpdate                    The content area where the resulting merged content should be placed. What ever is in this area after this call will be used for the merged commit.
     * @param sourceCommit                          The source commit that we are merging from.
     * @param destinationCommit                     The destination commit that we are merging to.
     * @param sourceArea                            The content area at the source commit.
     * @param destinationArea                       The content area at the destination commit.
     * @param comparisonBetweenSourceAndDestination The comparison between the source and destination content areas. This is useful to understand what the two areas look like and how they differ.
     * @param contentFactory                        The factory to use for extracting content from the areas.
     * @param byteArrayIndex                        The byte array index to use when creating snap-shots for the content.
     * @param <TContent>                            The specific type of content to process.
     */
    @Override public <TContent extends ContentAPI> void mergeIntoAreaWithTwoWayDiff(AreaAPI<TContent> mergedAreaToUpdate, CommitAPI sourceCommit, CommitAPI destinationCommit, AreaAPI<TContent> sourceArea, AreaAPI<TContent> destinationArea, ComparisonAPI comparisonBetweenSourceAndDestination, ContentFactory<TContent> contentFactory, ByteArrayIndex byteArrayIndex)
    {
        // First apply the destination content to the output content area:
        mergeArea(destinationArea, mergedAreaToUpdate, contentFactory, byteArrayIndex);
        // Now we have all the destination content.

        // Apply all the source changes on top of the result:
        mergeArea(sourceArea, mergedAreaToUpdate, contentFactory, byteArrayIndex);
        // Now we have merged the source changes on top of the destination branch.
    }


    /**
     * This merges the given area into the output area.
     *
     * @param <TContent>         The specific type of content to process.
     * @param areaToMerge        The area to merge.
     * @param mergedAreaToUpdate The output area to merge into.
     * @param contentFactory     The factory to use for extracting content from the areas.
     * @param byteArrayIndex     The byte array index to use when creating snap-shots for the content.
     */
    protected <TContent extends ContentAPI> void mergeArea(AreaAPI<TContent> areaToMerge, AreaAPI<TContent> mergedAreaToUpdate, ContentFactory<TContent> contentFactory, ByteArrayIndex byteArrayIndex)
    {
        // Go through the content in the area to merge:
        for (AreaEntry<TContent> areaEntry : areaToMerge)
        {
            // Get the path that we are at:
            RepoPath path = areaEntry.getPath();

            // Get the bytes for this area entry:
            byte[] bytes = areaEntry.getContent().asByteArray();

            // Pass the bytes through the byte index so that we can reuse existing bytes:
            bytes = byteArrayIndex.addOrLookup(bytes);

            // Create the output content:
            TContent outputContent = contentFactory.createContent(bytes);

            // Copy the content across:
            mergedAreaToUpdate.putContent(path, outputContent);
        }
    }
}
