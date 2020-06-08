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
 * A base class for a merge engine that performs diffs between the content of the commits.
 */
public abstract class DiffingMergeEngineBase extends MergeEngineBase implements DiffingMergeEngineAPI
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
    @Override
    public <TContent extends Content> void mergeIntoAreaWithThreeWayDiff(Area<TContent> mergedAreaToUpdate, Commit commonAncestorCommit, Commit sourceCommit, Commit destinationCommit, Area<TContent> commonAncestorArea, Area<TContent> sourceArea, Area<TContent> destinationArea, Comparison comparisonBetweenSourceAndDestination, Difference differenceBetweenAncestorAndSource, Difference differenceBetweenAncestorAndDestination, ContentFactory<TContent> contentFactory, ByteArrayIndex byteArrayIndex)
    {
        // Go through each path in the comparison:
        for (ComparisonEntry comparisonEntry : comparisonBetweenSourceAndDestination)
        {
            // Get the path for this entry:
            RepoPath path = comparisonEntry.path;

            // Process the comparison to get the new bytes to use:
            byte[] bytes;
            switch (comparisonEntry.state)
            {
                case ADDED:
                case UNCHANGED:
                {
                    // The content was added or unchanged between the two branches.

                    // Get the content from the source area:
                    TContent sourceContent = sourceArea.getContent(path);

                    // Get the bytes for the content:
                    bytes = sourceContent.asByteArray();

                    break;
                }

                case CHANGED:
                {
                    // The content has changed between both the source and destination commits.

                    // Check against the common ancestor what changes have been made:
                    DifferenceState sourceDifference = differenceBetweenAncestorAndSource.getDifference(path);
                    DifferenceState destinationDifference = differenceBetweenAncestorAndDestination.getDifference(path);

                    // Handle each permutation of the differences:
                    if (sourceDifference == null)
                    {
                        // There was no difference between the common ancestor and the source commit.

                        if (destinationDifference == null)
                        {
                            // There was no difference between the common ancestor and the destination commit.

                            // We don't expect this case because then the comparison should have flagged it as an unchanged state.
                            bytes = null;
                        }
                        else
                        {
                            // There was a difference between the common ancestor and the destination commit.
                            switch (destinationDifference)
                            {
                                case ADDED:
                                case CHANGED:
                                {
                                    // The content was added or changed between the common ancestor and the destination commit
                                    // but there was no change between the common ancestor and the source commit
                                    // therefore the destination difference wins.

                                    // Get the content from the destination area:
                                    TContent destinationContent = destinationArea.getContent(path);

                                    // Get the bytes for the content:
                                    bytes = destinationContent.asByteArray();

                                    break;
                                }
                                case DELETED:
                                default:
                                {
                                    // The content was deleted between the common ancestor and the destination commit
                                    // but there was no change between the common ancestor and the source commit
                                    // therefore the destination difference wins and we delete the content from the merged result.

                                    // Don't put the content into the merged result because it was deleted.
                                    bytes = null;

                                    break;
                                }
                            }
                        }
                    }
                    else
                    {
                        // There was a difference between the common ancestor and the source commit.
                        switch (sourceDifference)
                        {
                            case ADDED:
                            case CHANGED:
                            {
                                // The content was added or changed between the common ancestor and the source commit.


                                if (destinationDifference == null)
                                {
                                    // There was no difference between the common ancestor and the destination commit
                                    // but there was a change between the common ancestor and the source commit
                                    // therefore the change between source wins.

                                    // Get the content from the source area:
                                    TContent sourceContent = sourceArea.getContent(path);

                                    // Get the bytes for the content:
                                    bytes = sourceContent.asByteArray();
                                }
                                else
                                {
                                    // There was a difference between the common ancestor and the destination commit.
                                    switch (destinationDifference)
                                    {
                                        case ADDED:
                                        case CHANGED:
                                        {
                                            // The content was added or changed between the common ancestor and the destination commit
                                            // but it has also been changed between the common ancestor and the source commit
                                            // therefore we need to merge the conflict for changes in both branches.

                                            // Get the content from the source area:
                                            TContent sourceContent = sourceArea.getContent(path);

                                            // Get the content from the destination area:
                                            TContent destinationContent = destinationArea.getContent(path);

                                            // Merge Conflicts for changes in both branches.
                                            bytes = resolveConflictForChangesInSourceAndDestinationBranches(path, sourceCommit, sourceContent, sourceDifference, destinationCommit, destinationContent, destinationDifference);

                                            break;
                                        }
                                        case DELETED:
                                        {
                                            // The content was deleted between the common ancestor and the destination commit
                                            // but it has also been changed between the common ancestor and the source commit
                                            // therefore we need to merge the conflict for changes in both branches.

                                            // Get the content from the source area:
                                            TContent sourceContent = sourceArea.getContent(path);

                                            // Merge Conflicts for changes in the source branch but deletion in the destination branch.
                                            bytes = resolveConflictForChangesInSourceBranchButDeletionInDestinationBranch(path, sourceCommit, sourceContent, sourceDifference, destinationCommit, destinationDifference);

                                            break;
                                        }
                                        default:
                                        {
                                            // Don't copy across:
                                            bytes = null;
                                        }
                                    }
                                }
                                break;
                            }

                            case DELETED:
                            {
                                // The content was deleted between the common ancestor and the source commit.


                                if (destinationDifference == null)
                                {
                                    // There was no difference between the common ancestor and the destination commit
                                    // but the content was also deleted between the common ancestor and the source commit
                                    // therefore the deletion in the source commit wins.

                                    // Don't put this content into the merged area because it was deleted.
                                    bytes = null;
                                }
                                else
                                {
                                    // There was a difference between the common ancestor and the destination commit.
                                    switch (destinationDifference)
                                    {
                                        case ADDED:
                                        case CHANGED:
                                        {
                                            // The content was added or changed between the common ancestor and the destination commit
                                            // but the content was also deleted between the common ancestor and the source commit
                                            // therefore we need to merge the conflict for a change in the destination branch and a deletion in the source branch.

                                            // Get the content from the destination area:
                                            TContent destinationContent = destinationArea.getContent(path);

                                            // Merge conflicts for a change in the destination branch and a deletion in the source branch
                                            bytes = resolveConflictForDeletionInSourceBranchButChangeInDestinationBranch(path, sourceCommit, sourceDifference, destinationCommit, destinationContent, destinationDifference);

                                            break;
                                        }
                                        case DELETED:
                                        default:
                                        {
                                            // The content was deleted between the common ancestor and the destination commit.
                                            // but the content was also deleted between the common ancestor and the source commit
                                            // therefore we don't put the content in the merged area.

                                            // Deleted from both:
                                            bytes = null;

                                            break;
                                        }
                                    }
                                }
                                break;
                            }

                            default:
                            {
                                // Don't copy across:
                                bytes = null;
                            }
                        }
                    }
                    break;
                }
                case DELETED:
                default:
                {
                    // The content was deleted from the source to the destination branch.

                    // Don't copy the content across.
                    bytes = null;
                    break;
                }
            }
            // Now we have the bytes to put into the merged area or null to not put any bytes.

            // Check whether we want to put content into the merged area:
            if (bytes != null)
            {
                // We want to put content into the merged area.

                // Make sure to pass the bytes through our byte index:
                bytes = byteArrayIndex.addOrLookup(bytes);

                // Create the merged content:
                TContent mergedContent = contentFactory.createContent(bytes);

                // Copy the content across:
                mergedAreaToUpdate.putContent(path, mergedContent);
            }
        }
    }

    /**
     * Resolves the conflict when there were changes for the given content in both the source and destination branches.
     *
     * @param path                  The path of the content that is conflicting.
     * @param sourceCommit          The commit in the source branch that is conflicted.
     * @param sourceContent         The content in the source branch that is conflicted at this path.
     * @param sourceDifference      The difference between the common ancestor and the source commit at this path.
     * @param destinationCommit     The commit in the destination branch that is conflicted.
     * @param destinationContent    The content in the destination branch that is conflicted at this path.
     * @param destinationDifference The difference between the common ancestor and the destination commit at this path.
     * @param <TContent>            The specific type of content that is conflicted. Return null to not include any content in the merged area for this path.
     * @return The merged bytes to use for the resolved conflict.
     */
    protected abstract <TContent extends Content> byte[] resolveConflictForChangesInSourceAndDestinationBranches(RepoPath path, Commit sourceCommit, TContent sourceContent, DifferenceState sourceDifference, Commit destinationCommit, TContent destinationContent, DifferenceState destinationDifference);

    /**
     * Resolves the conflict when there were changes for the given content in the source branch but a deletion in the destination branch.
     *
     * @param path                  The path of the content that is conflicting.
     * @param sourceCommit          The commit in the source branch that is conflicted.
     * @param sourceContent         The content in the source branch that is conflicted at this path.
     * @param sourceDifference      The difference between the common ancestor and the source commit at this path.
     * @param destinationCommit     The commit in the destination branch that is conflicted.
     * @param destinationDifference The difference between the common ancestor and the destination commit at this path.
     * @param <TContent>            The specific type of content that is conflicted. Return null to not include any content in the merged area for this path.
     * @return The merged bytes to use for the resolved conflict.
     */
    protected abstract <TContent extends Content> byte[] resolveConflictForChangesInSourceBranchButDeletionInDestinationBranch(RepoPath path, Commit sourceCommit, TContent sourceContent, DifferenceState sourceDifference, Commit destinationCommit, DifferenceState destinationDifference);

    /**
     * Resolves the conflict when there was a deletion of the given content in the source branch but a change in the destination branch.
     *
     * @param path                  The path of the content that is conflicting.
     * @param sourceCommit          The commit in the source branch that is conflicted.
     * @param sourceDifference      The difference between the common ancestor and the source commit at this path.
     * @param destinationCommit     The commit in the destination branch that is conflicted.
     * @param destinationContent    The content in the destination branch that is conflicted at this path.
     * @param destinationDifference The difference between the common ancestor and the destination commit at this path.
     * @param <TContent>            The specific type of content that is conflicted. Return null to not include any content in the merged area for this path.
     * @return The merged bytes to use for the resolved conflict.
     */
    protected abstract <TContent extends Content> byte[] resolveConflictForDeletionInSourceBranchButChangeInDestinationBranch(RepoPath path, Commit sourceCommit, DifferenceState sourceDifference, Commit destinationCommit, TContent destinationContent, DifferenceState destinationDifference);

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
     * @param <TContent>                            The specific type of content that we are merging in this call.
     */
    @Override
    public <TContent extends Content> void mergeIntoAreaWithTwoWayDiff(Area<TContent> mergedAreaToUpdate, Commit sourceCommit, Commit destinationCommit, Area<TContent> sourceArea, Area<TContent> destinationArea, Comparison comparisonBetweenSourceAndDestination, ContentFactory<TContent> contentFactory, ByteArrayIndex byteArrayIndex)
    {
        // Go through each path in the comparison:
        for (ComparisonEntry comparisonEntry : comparisonBetweenSourceAndDestination)
        {
            // Get the path for this entry:
            RepoPath path = comparisonEntry.path;

            // Process the comparison to get the new bytes to use:
            byte[] bytes;
            switch (comparisonEntry.state)
            {
                case ADDED:
                {
                    // The content was added in the source branch but does not exist in the destination branch.

                    // Get the content from the source area:
                    TContent sourceContent = sourceArea.getContent(path);

                    // Get the bytes for the content:
                    bytes = sourceContent.asByteArray();

                    break;
                }

                case DELETED:
                case UNCHANGED:
                {
                    // The content was unchanged or it doesn't exist in the source branch. It does exist in the destination area.

                    // Get the content from the destination area:
                    TContent destinationContent = destinationArea.getContent(path);

                    // Get the bytes for the content:
                    bytes = destinationContent.asByteArray();

                    break;
                }

                case CHANGED:
                {
                    // The content has changed between both the source and destination commits.

                    // Get the content from the source area:
                    TContent sourceContent = sourceArea.getContent(path);

                    // Get the content from the destination area:
                    TContent destinationContent = destinationArea.getContent(path);

                    // Merge Conflicts for changes in both branches.
                    bytes = resolveConflictForTwoWayMerge(path, sourceCommit, sourceContent, destinationCommit, destinationContent);

                    break;
                }
                default:
                {
                    // Don't copy the content across.
                    bytes = null;
                    break;
                }
            }
            // Now we have the bytes to put into the merged area or null to not put any bytes.

            // Check whether we want to put content into the merged area:
            if (bytes != null)
            {
                // We want to put content into the merged area.

                // Make sure to pass the bytes through our byte index:
                bytes = byteArrayIndex.addOrLookup(bytes);

                // Create the merged content:
                TContent mergedContent = contentFactory.createContent(bytes);

                // Copy the content across:
                mergedAreaToUpdate.putContent(path, mergedContent);
            }
        }
    }

    /**
     * Resolves the conflict when there were changes for the given content in the source and destination branches when doing a two way merge.
     * A two way merge means that neither of the commits shared a common ancestor.
     *
     * @param path               The path of the content that is conflicting.
     * @param sourceCommit       The commit in the source branch that is conflicted.
     * @param sourceContent      The content in the source branch that is conflicted.
     * @param destinationCommit  The commit in the destination branch that is conflicted.
     * @param destinationContent The content in the destination branch that is conflicted at this path.
     * @param <TContent>         The specific type of content that is conflicted. Return null to not include any content in the merged area for this path.
     * @return The merged bytes to use for the resolved conflict.
     */
    protected abstract <TContent extends Content> byte[] resolveConflictForTwoWayMerge(RepoPath path, Commit sourceCommit, TContent sourceContent, Commit destinationCommit, TContent destinationContent);
}
