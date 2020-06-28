/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.SearchQueryAPI;
import io.nanovc.SearchResultsBase;

import java.util.ArrayList;
import java.util.List;

/**
 * This contains the result of a search for {@link MemoryCommitAPI}'s.
 * Use a {@link SearchQueryAPI} to find the commits.
 */
public abstract class MemorySearchResultsBase<
    TCommit extends MemoryCommitAPI<?>,
    TSearchQuery extends MemorySearchQueryAPI<TCommit>
    >
    extends
    SearchResultsBase<
        TCommit,
        TSearchQuery
        >
    implements MemorySearchResultsAPI<
    TCommit,
    TSearchQuery
    >
{
    /**
     * The list of commits for this set of results.
     */
    protected final ArrayList<TCommit> commits = new ArrayList<>();

    /**
     * Creates a set of results for the given query.
     *
     * @param query The query that was used to create these results.
     */
    public MemorySearchResultsBase(TSearchQuery query)
    {
        super(query);
    }

    /**
     * @return The list of commits that were found for this search.
     */
    @Override
    public List<TCommit> getCommits()
    {
        return this.commits;
    }
}
