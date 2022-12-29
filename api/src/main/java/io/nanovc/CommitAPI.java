/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import io.nanovc.areas.ByteArrayAreaAPI;
import io.nanovc.areas.StringAreaAPI;

import java.util.List;

/**
 * An interface for a commit in a {@link RepoAPI}.
 * It stores information about who saved the snapshots of content, when they were saved, and why they were saved.
 */
public interface CommitAPI
{
    /**
     * Gets the message for this commit.
     * @return The message for this commit.
     */
    String getMessage();

    /**
     * Gets the timestamp when the commit was created.
     * @return The timestamp when the commit was created.
     */
    TimestampAPI getTimestamp();

    /**
     * Gets the snapshot of the content for this commit.
     * @return A snapshot of the content for this commit.
     */
    ByteArrayAreaAPI getSnapshot();

    /**
     * Gets the commit tags that contain additional meta-data for this commit.
     * This is useful for authors, committers and other such data.
     * Consider using {@link io.nanovc.CommitTags} as a helper to create these.
     * @return The commit tags that contain additional meta-data for this commit.
     */
    StringAreaAPI getCommitTags();

    /**
     * Gets the first parent commit.
     * If this is null then this is a root commit.
     * If there is more than one parent then the remaining parents are referenced in {@link #getOtherParentCommits()}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @return The first parent commit.
     */
    CommitAPI getFirstParentCommit();

    /**
     * Gets the other parent commits.
     * If this is null then there are no other parents.
     * If there is only one parent then this list will be null (to preserve memory) and the parent referenced in {@link #getFirstParentCommit()}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @return The other parent commits.
     */
    List<CommitAPI> getOtherParentCommits();

    /**
     * Gets all the parent commits.
     * This always returns a list, even when there are no parents (meaning this is a root commit).
     * If this is empty then there are no other parents and this is a root commit.
     * For better performance, consider using {@link #getFirstParentCommit()} and {@link #getOtherParentCommits()}
     * since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @return All the parent commits. The first commit in the list is considered the first parent. The rest are considered the other parents.
     */
    List<CommitAPI> getAllParentCommits();

}
