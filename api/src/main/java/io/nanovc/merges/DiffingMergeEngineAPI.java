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
import io.nanovc.indexes.ByteArrayIndex;

/**
 * This captures the functionality that is exposed by a Diffing Merge Engine.
 * The internal API between a {@link DiffingMergeHandlerBase} and its {@link DiffingMergeEngineBase}.
 */
public interface DiffingMergeEngineAPI extends MergeEngine
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
     * @param <TContent>                              The specific type of content that we are merging in this call.
     */
    <TContent extends Content> void mergeIntoAreaWithThreeWayDiff(
        Area<TContent> mergedAreaToUpdate,
        Commit commonAncestorCommit, Commit sourceCommit, Commit destinationCommit,
        Area<TContent> commonAncestorArea, Area<TContent> sourceArea, Area<TContent> destinationArea,
        Comparison comparisonBetweenSourceAndDestination,
        Difference differenceBetweenAncestorAndSource, Difference differenceBetweenAncestorAndDestination,
        ContentFactory<TContent> contentFactory,
        ByteArrayIndex byteArrayIndex
    );

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
     * @param byteArrayIndex                          The byte array index to use when creating snap-shots for the content.
     * @param <TContent>                            The specific type of content that we are merging in this call.
     */
    <TContent extends Content> void mergeIntoAreaWithTwoWayDiff(
        Area<TContent> mergedAreaToUpdate,
        Commit sourceCommit, Commit destinationCommit,
        Area<TContent> sourceArea, Area<TContent> destinationArea,
        Comparison comparisonBetweenSourceAndDestination,
        ContentFactory<TContent> contentFactory,
        ByteArrayIndex byteArrayIndex
    );
}
