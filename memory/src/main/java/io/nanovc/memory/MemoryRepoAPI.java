/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;
import io.nanovc.RepoAPI;

import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * The interface for a repository of data that is only stored in memory.
 * NOTE: This repo represents the state of the version controlled data.
 *
 * @param <TContent> The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>    The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>  The specific type of commit that is created in the repo.
 */
public interface MemoryRepoAPI<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends MemoryCommitAPI<TCommit>
    >
    extends RepoAPI<
        TContent,
        TArea,
        TCommit
        >
{
    /**
     * The references to the commits that have been tagged.
     * The key is the tag name.
     * The value is the commit that has been tagged.
     * @return The references to the commits that have been tagged.
     */
    LinkedHashSet<TCommit> getDanglingCommits();

    /**
     * The references to the tips of each branch.
     * The key is the branch name.
     * The value is the last commit in that branch.
     * @return The references to the tips of each branch.
     */
    HashMap<String, TCommit> getBranchTips();

    /**
     * The references to the commits that have been tagged.
     * The key is the tag name.
     * The value is the commit that has been tagged.
     * @return The references to the commits that have been tagged.
     */
    HashMap<String, TCommit> getTags();
}
