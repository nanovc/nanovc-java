/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.*;

/**
 * The repo handler API for working with {@link MemoryRepoBase}'s.
 * This represents the public API when working with {@link RepoAPI}'s.
 * It holds common state including the {@link RepoAPI} being worked on and the {@link RepoEngineAPI} that contains the specific algorithm that we are interested in when working with the repo.
 * You can swap out the repo that is being worked on in cases where a correctly configured repo handler must work on multiple repo's.
 * The core functionality is delegated to the {@link RepoEngineAPI} which is stateless and can be reused for multiple {@link RepoAPI}'s and {@link RepoHandlerAPI}'s.
 *
 * @param <TContent>     The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>        The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>      The specific type of commit that is created in the repo.
 * @param <TSearchQuery> The specific type of search query that this engine returns.
 * @param <TSearchResults> The specific type of search results that we expect to get.
 * @param <TRepo>        The specific type of repo that this handler manages.
 * @param <TEngine>      The specific type of engine that manipulates the repo.
 */
public interface MemoryRepoHandlerAPI<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends MemoryCommitAPI<TCommit>,
    TSearchQuery extends MemorySearchQueryAPI<TCommit>,
    TSearchResults extends MemorySearchResultsAPI<TCommit, TSearchQuery>,
    TRepo extends MemoryRepoAPI<TContent, TArea, TCommit>,
    TEngine extends MemoryRepoEngineAPI<TContent, TArea, TCommit, TSearchQuery, TSearchResults, TRepo>
    >
    extends RepoHandlerAPI<
        TContent,
        TArea,
        TCommit,
        TSearchQuery,
        TSearchResults,
        TRepo,
        TEngine
        >
{
    // There is no additional public API when dealing with memory.
}
