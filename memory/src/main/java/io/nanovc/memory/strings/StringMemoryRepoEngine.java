/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.strings;

import io.nanovc.SearchQueryDefinitionAPI;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.content.StringContent;
import io.nanovc.memory.MemoryCommit;
import io.nanovc.memory.MemorySearchQuery;
import io.nanovc.memory.MemorySearchResults;

/**
 * The engine for working with a nano version control repository in memory.
 * A Repo Engine does not contain any state. Just the logic of how to manipulate a repo.
 * Therefore you need to pass the repo into all the calls.
 * This is good where one Repo Engine is going to be reused across many Repos.
 * A repo engine is thread safe because it is stateless.
 */
public class StringMemoryRepoEngine
    extends StringMemoryRepoEngineBase<
    StringContent,
    StringHashMapArea,
    MemoryCommit,
    MemorySearchQuery,
    MemorySearchResults,
    StringMemoryRepo
    >
{
    /**
     * Creates a repo that is associated with this repo engine.
     *
     * @return The Repo that has been created and is now associated with this engine.
     */
    @Override
    public StringMemoryRepo createRepo()
    {
        return new StringMemoryRepo();
    }

    /**
     * Creates a new commit.
     *
     * @return A new commit.
     */
    @Override
    public MemoryCommit createCommit()
    {
        return new MemoryCommit();
    }

    /**
     * A factory method to create the specific search query to use.
     *
     * @param searchQueryDefinition The definition of the search query that is being created.
     * @return A new search query.
     */
    @Override
    public MemorySearchQuery createSearchQuery(SearchQueryDefinitionAPI searchQueryDefinition)
    {
        return new MemorySearchQuery(searchQueryDefinition);
    }

    /**
     * A factory method to create the specific search results that are needed.
     *
     * @param searchQuery The search query to reuse for this search.
     * @return New search results.
     */
    @Override
    public MemorySearchResults createSearchResults(MemorySearchQuery searchQuery)
    {
        return new MemorySearchResults(searchQuery);
    }

}
