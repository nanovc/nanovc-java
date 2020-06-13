/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.Area;
import io.nanovc.Content;
import io.nanovc.RepoBase;

import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * The base class for a repository of data that is only stored in memory.
 * NOTE: This repo represents the state of the version controlled data.
 *
 * @param <TContent> The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>    The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>  The specific type of commit that is created in the repo.
 */
public abstract class MemoryRepoBase<
    TContent extends Content,
    TArea extends Area<TContent>,
    TCommit extends MemoryCommitBase<TCommit>
    >
    extends RepoBase<
    TContent,
    TArea,
    TCommit
    >
    implements MemoryRepoAPI<
        TContent,
        TArea,
        TCommit
        >
{
    /**
     * The list of all dangling commits in the repo.
     * A dangling commit is one that is at the tip of a commit chain and does not have a branch or tag pointing at it.
     * This is useful to get access to commits that have been "lost" because there are no branches or tags pointing at them or their children.
     */
    protected final LinkedHashSet<TCommit> danglingCommits = new LinkedHashSet<>();

    /**
     * The references to the tips of each branch.
     * The key is the branch name.
     * The value is the last commit in that branch.
     */
    protected final HashMap<String, TCommit> branchTips = new HashMap<>();

    /**
     * The references to the commits that have been tagged.
     * The key is the tag name.
     * The value is the commit that has been tagged.
     */
    protected final HashMap<String, TCommit> tags = new HashMap<>();

    /**
     * The references to the commits that have been tagged.
     * The key is the tag name.
     * The value is the commit that has been tagged.
     * @return The references to the commits that have been tagged.
     */
    public LinkedHashSet<TCommit> getDanglingCommits()
    {
        return danglingCommits;
    }

    /**
     * The references to the tips of each branch.
     * The key is the branch name.
     * The value is the last commit in that branch.
     * @return The references to the tips of each branch.
     */
    public HashMap<String, TCommit> getBranchTips()
    {
        return branchTips;
    }

    /**
     * The references to the commits that have been tagged.
     * The key is the tag name.
     * The value is the commit that has been tagged.
     * @return The references to the commits that have been tagged.
     */
    public HashMap<String, TCommit> getTags()
    {
        return tags;
    }
}
