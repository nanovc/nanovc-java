/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

/**
 * The engine for working with a nano version control repository.
 * This represents the internal API for a {@link RepoHandlerAPI}.
 * It must contain ALL the actual logic for manipulating and extracting meaningful information from the {@link RepoAPI}.
 * A Repo Engine does not contain any state. Just the logic of how to manipulate a repo.
 * Therefore you need to pass the repo into all the calls.
 * This is good where one Repo Engine is going to be reused across many Repos and handlers.
 * A repo engine is thread safe because it is stateless.
 * It is designed to be able to process many repo's.
 *
 * @param <TContent>     The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>        The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>      The specific type of commit that is created in the repo.
 * @param <TSearchQuery> The specific type of search query that this engine returns.
 * @param <TRepo>        The specific type of repo that this engine is for.
 */
public class RepoEngineBase<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends CommitAPI,
    TSearchQuery extends SearchQueryAPI<TCommit>,
    TSearchResults extends SearchResultsAPI<TCommit, TSearchQuery>,
    TRepo extends RepoAPI<TContent, TArea, TCommit>
    >
    implements RepoEngineAPI<
        TContent,
        TArea,
        TCommit,
        TSearchQuery,
        TSearchResults,
        TRepo
        >
{
    // NOTE: There is no generic API for all engines. Each specific technique will determine an appropriate private API between the handler and the engine.
}
