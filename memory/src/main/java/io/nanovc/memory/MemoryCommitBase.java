/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.CommitBase;
import io.nanovc.TimestampAPI;
import io.nanovc.areas.ByteArrayAreaAPI;
import io.nanovc.areas.StringAreaAPI;

import java.util.List;

/**
 * The base class for an in-memory commit in a {@link MemoryRepo}.
 * It stores information about who saved the snapshots of content, when they were saved, and why they were saved.
 * @param <TSelf> The type parameter for ourselves. We need this for strong typing of parent commits.
 */
public abstract class MemoryCommitBase<TSelf extends MemoryCommitBase<?>>
    extends CommitBase
    implements MemoryCommitAPI<TSelf>
{
    /**
     * The timestamp when the commit was created.
     */
    public TimestampAPI timestamp;

    /**
     * A snapshot of the content for this commit.
     */
    public ByteArrayAreaAPI snapshot;

    /**
     * The first parent commit.
     * If this is null then this is a root commit.
     * If there is more than one parent then the remaining parents are referenced in {@link #otherParents}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     */
    public TSelf firstParent;

    /**
     * The other parent commits.
     * If this is null then there are no other parents.
     * If there is only one parent then this list will be null (to preserve memory) and the parent referenced in {@link #firstParent}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     */
    public List<TSelf> otherParents;

    /**
     * The commit message.
     */
    public String message;

    /**
     * The commit tags that contain additional meta-data for this commit.
     * This is useful for authors, committers and other such data.
     * Consider using {@link io.nanovc.CommitTags} as a helper to create these.
     */
    public StringAreaAPI commitTags;

    /**
     * Gets the message for this commit.
     *
     * @return The message for this commit.
     */
    @Override
    public String getMessage()
    {
        return this.message;
    }

    /**
     * Sets the message for this commit.
     * @param message The message for this commit.
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Gets the timestamp when the commit was created.
     * @return The timestamp when the commit was created.
     */
    @Override
    public TimestampAPI getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * Sets he timestamp when the commit was created.
     * @param timestamp The timestamp when the commit was created.
     */
    public void setTimestamp(TimestampAPI timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * Gets the snapshot of the content for this commit.
     * @return A snapshot of the content for this commit.
     */
    public ByteArrayAreaAPI getSnapshot()
    {
        return snapshot;
    }

    /**
     * Sets the snapshot of the content for this commit.
     * @param snapshot A snapshot of the content for this commit.
     */
    public void setSnapshot(ByteArrayAreaAPI snapshot)
    {
        this.snapshot = snapshot;
    }

    /**
     * Gets the first parent commit.
     * If this is null then this is a root commit.
     * If there is more than one parent then the remaining parents are referenced in {@link #otherParents}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @return The first parent commit.
     */
    public TSelf getFirstParent()
    {
        return firstParent;
    }

    /**
     * Sets the first parent commit.
     * If this is null then this is a root commit.
     * If there is more than one parent then the remaining parents are referenced in {@link #otherParents}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @param firstParent The first parent commit.
     */
    public void setFirstParent(TSelf firstParent)
    {
        this.firstParent = firstParent;
    }

    /**
     * Gets the other parent commits.
     * If this is null then there are no other parents.
     * If there is only one parent then this list will be null (to preserve memory) and the parent referenced in {@link #firstParent}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @return The other parent commits.
     */
    public List<TSelf> getOtherParents()
    {
        return otherParents;
    }

    /**
     * Sets the other parent commits.
     * If this is null then there are no other parents.
     * If there is only one parent then this list will be null (to preserve memory) and the parent referenced in {@link #firstParent}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     * @param otherParents The other parent commits.
     */
    public void setOtherParents(List<TSelf> otherParents)
    {
        this.otherParents = otherParents;
    }

    /**
     * Gets the commit tags that contain additional meta-data for this commit.
     * This is useful for authors, committers and other such data.
     * Consider using {@link io.nanovc.CommitTags} as a helper to create these.
     * @return The commit tags that contain additional meta-data for this commit.
     */
    public StringAreaAPI getCommitTags()
    {
        return commitTags;
    }

    /**
     * Sets the commit tags that contain additional meta-data for this commit.
     * This is useful for authors, committers and other such data.
     * Consider using {@link io.nanovc.CommitTags} as a helper to create these.
     * @param commitTags The commit tags that contain additional meta-data for this commit.
     */
    public void setCommitTags(StringAreaAPI commitTags)
    {
        this.commitTags = commitTags;
    }

    @Override
    public String toString()
    {
        return message == null ? super.toString() : message;
    }
}
