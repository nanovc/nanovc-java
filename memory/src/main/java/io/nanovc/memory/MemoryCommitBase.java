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
import io.nanovc.CommitBase;
import io.nanovc.TimestampAPI;
import io.nanovc.areas.ByteArrayAreaAPI;
import io.nanovc.areas.StringAreaAPI;

import java.util.ArrayList;
import java.util.Collections;
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
    @Override
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
    @Override
    public void setTimestamp(TimestampAPI timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * Gets the snapshot of the content for this commit.
     * @return A snapshot of the content for this commit.
     */
    @Override
    public ByteArrayAreaAPI getSnapshot()
    {
        return snapshot;
    }

    /**
     * Sets the snapshot of the content for this commit.
     * @param snapshot A snapshot of the content for this commit.
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void setOtherParents(List<TSelf> otherParents)
    {
        this.otherParents = otherParents;
    }

    /**
     * Gets all the parent commits.
     * This always returns a list, even when there are no parents (meaning this is a root commit).
     * If this is empty then there are no other parents and this is a root commit.
     * For better performance, consider using {@link #getFirstParent()} and {@link #getOtherParents()}
     * since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     *
     * @return All the parent commits. The first commit in the list is considered the first parent. The rest are considered the other parents.
     */
    @Override
    public List<TSelf> getAllParents()
    {
        // Check for various cases:
        if (this.firstParent == null)
        {
            // We don't have a first parent.

            // Check whether we have other parents:
            if (this.otherParents == null)
            {
                // We have no other parents and no first parent, meaning no parent commits at all.
                return Collections.emptyList();
            }
            else
            {
                // We have other parents, which is strange to have when there is no first parent.
                return this.otherParents;
            }
        }
        else
        {
            // We have a first parent.

            // Check whether we have other parents:
            if (this.otherParents == null)
            {
                // We have no other parents but only a first parent.
                return Collections.singletonList(this.firstParent);
            }
            else
            {
                // We have a list of other parents and a first parent.

                // Check whether there are actually other parents in the list:
                if (this.otherParents.size() == 0)
                {
                    // We have a first parent but the list of other parents is empty.
                    // Just return the first parent:
                    return Collections.singletonList(this.firstParent);
                }
                else
                {
                    // We have a first parent and other parents in the list.
                    // Combine the parents:
                    ArrayList<TSelf> combined = new ArrayList<>(1 + this.otherParents.size());
                    combined.add(this.firstParent);
                    combined.addAll(this.otherParents);
                    return combined;
                }
            }
        }
    }

    /**
     * Sets all the parent commits.
     * If this is empty then there are no other parents and this is a root commit.
     * For better performance, consider using {@link #setFirstParent(TSelf)} and {@link #setOtherParents(List)}
     * since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     *
     * @param allParents All the parent commits. The first commit in the list is considered the first parent. The rest are considered the other parents.
     */
    @Override
    public void setAllParents(List<TSelf> allParents)
    {
        // Check for various cases:
        if (allParents == null || allParents.size() == 0)
        {
            // There are no parent commits at all.

            // Clear all the parents:
            this.firstParent = null;
            this.otherParents = null;
        }
        else
        {
            // There is a list of all the parents.

            // Save the first parent commit:
            this.firstParent = allParents.get(0);

            // Check whether we have exactly one:
            if (allParents.size() == 1)
            {
                // We have exactly one parent commit.

                // Clear the list of other parents to save memory:
                this.otherParents = null;
            }
            else
            {
                // We have more than one parent commit.

                // Extract the other parent commits into a new list:
                this.otherParents = new ArrayList<>(allParents.size() - 1);
                for (int i = 1; i < allParents.size(); i++)
                {
                    // Add the parent commit:
                    this.otherParents.add(allParents.get(i));
                }
            }
        }
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

    /**
     * Gets all the parent commits.
     * This always returns a list, even when there are no parents (meaning this is a root commit).
     * If this is empty then there are no other parents and this is a root commit.
     * For better performance, consider using {@link #getFirstParentCommit()} and {@link #getOtherParentCommits()}
     * since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     *
     * @return All the parent commits. The first commit in the list is considered the first parent. The rest are considered the other parents.
     */
    @Override
    public List<CommitAPI> getAllParentCommits()
    {
        // Get all the parents as a strongly typed list:
        List<TSelf> allParents = this.getAllParents();

        // Create a new list for the parent commits:
        ArrayList<CommitAPI> result = new ArrayList<>(allParents.size());

        // Copy the parents across:
        result.addAll(allParents);

        return result;
    }

    /**
     * Gets the first parent commit.
     * If this is null then this is a root commit.
     * If there is more than one parent then the remaining parents are referenced in {@link #getOtherParentCommits()}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     *
     * @return The first parent commit.
     */
    @Override
    public CommitAPI getFirstParentCommit()
    {
        return this.firstParent;
    }

    /**
     * Gets the other parent commits.
     * If this is null then there are no other parents.
     * If there is only one parent then this list will be null (to preserve memory) and the parent referenced in {@link #getFirstParentCommit()}.
     * We split this so that we can avoid creating a whole list for each commit.
     * Since most commits only have 1 parent, we can avoid creating a whole list unnecessarily.
     *
     * @return The other parent commits.
     */
    @Override
    public List<CommitAPI> getOtherParentCommits()
    {
        // Check for various cases:
        if (this.otherParents == null || this.otherParents.size() == 0)
        {
            // We don't have any other parents.
            return null;
        }
        else
        {
            // We have other parents.

            // Create a new list for the parent commits:
            ArrayList<CommitAPI> result = new ArrayList<>(this.otherParents.size());

            // Copy the parents across:
            result.addAll(otherParents);

            return result;
        }
    }

    @Override
    public String toString()
    {
        return message == null ? super.toString() : message;
    }
}
