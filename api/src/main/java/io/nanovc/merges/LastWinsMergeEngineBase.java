/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.merges;

import io.nanovc.CommitAPI;
import io.nanovc.ContentAPI;
import io.nanovc.DifferenceState;
import io.nanovc.RepoPath;

/**
 * A merge handler where the last change (in time) wins when a merge conflict is detected.
 */
public abstract class LastWinsMergeEngineBase
    extends DiffingMergeEngineBase
    implements LastWinsMergeEngineAPI
{
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
     * @return The merged bytes to use for the resolved conflict. Return null to not include any content in the merged area for this path.
     */
    @Override
    protected <TContent extends ContentAPI> byte[] resolveConflictForChangesInSourceAndDestinationBranches(RepoPath path, CommitAPI sourceCommit, TContent sourceContent, DifferenceState sourceDifference, CommitAPI destinationCommit, TContent destinationContent, DifferenceState destinationDifference)
    {
        // Check which commit is last:
        if (sourceCommit.getTimestamp().getInstant().isAfter(destinationCommit.getTimestamp().getInstant()))
        {
            // The source commit is after the destination commit.
            return sourceContent.asByteArray();
        }
        else
        {
            // The destination commit is after the source commit.
            return destinationContent.asByteArray();
        }
    }

    /**
     * Resolves the conflict when there were changes for the given content in the source branch but a deletion in the destination branch.
     *
     * @param path                  The path of the content that is conflicting.
     * @param sourceCommit          The commit in the source branch that is conflicted.
     * @param sourceContent         The content in the source branch that is conflicted at this path.
     * @param sourceDifference      The difference between the common ancestor and the source commit at this path.
     * @param destinationCommit     The commit in the destination branch that is conflicted.
     * @param destinationDifference The difference between the common ancestor and the destination commit at this path.
     * @return The merged bytes to use for the resolved conflict. Return null to not include any content in the merged area for this path.
     */
    @Override
    protected <TContent extends ContentAPI> byte[] resolveConflictForChangesInSourceBranchButDeletionInDestinationBranch(RepoPath path, CommitAPI sourceCommit, TContent sourceContent, DifferenceState sourceDifference, CommitAPI destinationCommit, DifferenceState destinationDifference)
    {
        // Check which commit is last:
        if (sourceCommit.getTimestamp().getInstant().isAfter(destinationCommit.getTimestamp().getInstant()))
        {
            // The source commit is after the destination commit.
            return sourceContent.asByteArray();
        }
        else
        {
            // The destination commit is after the source commit.
            return null;
        }
    }

    /**
     * Resolves the conflict when there was a deletion of the given content in the source branch but a change in the destination branch.
     *
     * @param path                  The path of the content that is conflicting.
     * @param sourceCommit          The commit in the source branch that is conflicted.
     * @param sourceDifference      The difference between the common ancestor and the source commit at this path.
     * @param destinationCommit     The commit in the destination branch that is conflicted.
     * @param destinationContent    The content in the destination branch that is conflicted at this path.
     * @param destinationDifference The difference between the common ancestor and the destination commit at this path.
     * @return The merged bytes to use for the resolved conflict. Return null to not include any content in the merged area for this path.
     */
    @Override
    protected <TContent extends ContentAPI> byte[] resolveConflictForDeletionInSourceBranchButChangeInDestinationBranch(RepoPath path, CommitAPI sourceCommit, DifferenceState sourceDifference, CommitAPI destinationCommit, TContent destinationContent, DifferenceState destinationDifference)
    {
        // Check which commit is last:
        if (sourceCommit.getTimestamp().getInstant().isAfter(destinationCommit.getTimestamp().getInstant()))
        {
            // The source commit is after the destination commit.
            return null;
        }
        else
        {
            // The destination commit is after the source commit.
            return destinationContent.asByteArray();
        }
    }

    /**
     * Resolves the conflict when there were changes for the given content in the source and destination branches when doing a two way merge.
     * A two way merge means that neither of the commits shared a common ancestor.
     *
     * @param path               The path of the content that is conflicting.
     * @param sourceCommit       The commit in the source branch that is conflicted.
     * @param destinationCommit  The commit in the destination branch that is conflicted.
     * @param destinationContent The content in the destination branch that is conflicted at this path.
     * @return The merged bytes to use for the resolved conflict.
     */
    @Override
    protected <TContent extends ContentAPI> byte[] resolveConflictForTwoWayMerge(RepoPath path, CommitAPI sourceCommit, TContent sourceContent, CommitAPI destinationCommit, TContent destinationContent)
    {
        // Check which commit is last:
        if (sourceCommit.getTimestamp().getInstant().isAfter(destinationCommit.getTimestamp().getInstant()))
        {
            // The source commit is after the destination commit.
            return sourceContent.asByteArray();
        }
        else
        {
            // The destination commit is after the source commit.
            return destinationContent.asByteArray();
        }
    }
}
