/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.CommitAPI;
import io.nanovc.TimestampAPI;
import io.nanovc.areas.ByteArrayAreaAPI;
import io.nanovc.areas.StringAreaAPI;

import java.util.List;

/**
 * The API for an in-memory commit in a {@link MemoryRepoBase}.
 * It stores information about who saved the snapshots of content, when they were saved, and why they were saved.
 * @param <TSelf> The type parameter for ourselves. We need this for strong typing of parent commits.
 */
public interface MemoryCommitAPI<TSelf extends MemoryCommitAPI<?>>
    extends CommitAPI
{

    /**
     * Sets the message for this commit.
     * @param message The message for this commit.
     */
    void setMessage(String message);

    /**
     * Sets he timestamp when the commit was created.
     * @param timestamp The timestamp when the commit was created.
     */
    void setTimestamp(TimestampAPI timestamp);

    /**
     * Gets the snapshot of the content for this commit.
     * @return A snapshot of the content for this commit.
     */
    ByteArrayAreaAPI getSnapshot();

    /**
     * Sets the snapshot of the content for this commit.
     * @param snapshot A snapshot of the content for this commit.
     */
    void setSnapshot(ByteArrayAreaAPI snapshot);

    /**
     * Gets the first parent commit.
     * If this is null then this is a root commit.
     * If there is more than one parent then the remaining parents are referenced in {@link #getOtherParents}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @return The first parent commit.
     */
    TSelf getFirstParent();

    /**
     * Sets the first parent commit.
     * If this is null then this is a root commit.
     * If there is more than one parent then the remaining parents are referenced in {@link #getOtherParents}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @param firstParent The first parent commit.
     */
    void setFirstParent(TSelf firstParent);

    /**
     * Gets the other parent commits.
     * If this is null then there are no other parents.
     * If there is only one parent then this list will be null (to preserve memory) and the parent referenced in {@link #getFirstParent}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @return The other parent commits.
     */
    List<TSelf> getOtherParents();

    /**
     * Sets the other parent commits.
     * If this is null then there are no other parents.
     * If there is only one parent then this list will be null (to preserve memory) and the parent referenced in {@link #getFirstParent}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @param otherParents The other parent commits.
     */
    void setOtherParents(List<TSelf> otherParents);

    /**
     * Gets the commit tags that contain additional meta-data for this commit.
     * This is useful for authors, committers and other such data.
     * Consider using {@link io.nanovc.CommitTags} as a helper to create these.
     * @return The commit tags that contain additional meta-data for this commit.
     */
    StringAreaAPI getCommitTags();

    /**
     * Sets the commit tags that contain additional meta-data for this commit.
     * This is useful for authors, committers and other such data.
     * Consider using {@link io.nanovc.CommitTags} as a helper to create these.
     * @param commitTags The commit tags that contain additional meta-data for this commit.
     */
    void setCommitTags(StringAreaAPI commitTags);

    /**
     * Gets all the parent commits.
     * This always returns a list, even when there are no parents (meaning this is a root commit).
     * If this is empty then there are no other parents and this is a root commit.
     * For better performance, consider using {@link #getFirstParent()} and {@link #getOtherParents()}
     * since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @return All the parent commits. The first commit in the list is considered the first parent. The rest are considered the other parents.
     */
    List<TSelf> getAllParents();

    /**
     * Sets all the parent commits.
     * If this is empty then there are no other parents and this is a root commit.
     * For better performance, consider using {@link #setFirstParent(MemoryCommitAPI)} and {@link #setOtherParents(List)}
     * since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @param allParents All the parent commits. The first commit in the list is considered the first parent. The rest are considered the other parents.
     */
    void setAllParents(List<TSelf> allParents);
}
