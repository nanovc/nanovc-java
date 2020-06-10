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
import io.nanovc.Timestamp;
import io.nanovc.areas.ByteArrayArea;

import java.util.List;

/**
 * The base class for an in-memory commit in a {@link MemoryRepoBase}.
 * It stores information about who saved the snapshots of content, when they were saved, and why they were saved.
 * @param <TSelf> The type parameter for ourselves. We need this for strong typing of parent commits.
 */
public abstract class MemoryCommitBase<TSelf extends MemoryCommitBase> extends CommitBase
{
    /**
     * The timestamp when the commit was created.
     */
    public Timestamp timestamp;

    /**
     * A snapshot of the content for this commit.
     */
    public ByteArrayArea snapshot;

    /**
     * The first parent commit.
     * If this is null then this is a root commit.
     * If there is more than one parent then the remaining parents are referenced in {@link #otherParents}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     */
    public TSelf firstParent;

    /**
     * The first parent commit.
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
     * Gets the timestamp when the commit was created.
     */
    @Override
    public Timestamp getTimestamp()
    {
        return this.timestamp;
    }

    @Override
    public String toString()
    {
        return message == null ? super.toString() : message;
    }
}