/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.strings;

import io.nanovc.Area;
import io.nanovc.content.StringContent;
import io.nanovc.memory.MemoryCommitBase;
import io.nanovc.memory.MemoryRepoEngineAPI;
import io.nanovc.memory.MemorySearchQueryBase;
import io.nanovc.memory.MemorySearchResultsBase;

/**
 * The interface for the engine for working with a nano version control repository of strings in memory.
 * A Repo Engine does not contain any state. Just the logic of how to manipulate a repo.
 * Therefore you need to pass the repo into all the calls.
 * This is good where one Repo Engine is going to be reused across many Repos.
 * A repo engine is thread safe because it is stateless.
 *
 * @param <TContent> The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>    The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>  The specific type of commit that is created in the repo.
 * @param <TRepo>    The specific type of repo that this engine is for.
 */
public interface StringMemoryRepoEngineAPI<
    TContent extends StringContent,
    TArea extends Area<TContent>,
    TCommit extends MemoryCommitBase<TCommit>,
    TSearchQuery extends MemorySearchQueryBase<TCommit>,
    TSearchResults extends MemorySearchResultsBase<TCommit, TSearchQuery>,
    TRepo extends StringMemoryRepoBase<TContent, TArea, TCommit>
    >
    extends MemoryRepoEngineAPI<
        TContent,
        TArea,
        TCommit,
        TSearchQuery,
        TSearchResults,
        TRepo
        >
{
}
