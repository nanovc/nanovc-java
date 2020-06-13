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
 * This is the interface for a commit search query.
 * It's useful for searching for {@link Commit}'s.
 * The search query It stores the state of the search for each query.
 * You need one instance of this per query.
 * It stores state for arguments to the query (parameters).
 * The search query is evaluated to produce CommitSearchResults which is a stream of Commits.
 * This is part of the {@link CRUSHED#READ} concepts.
 * @param <TCommit>  The specific type of commit that is created in the repo.
 */
public interface SearchQuery<TCommit extends Commit>
{
    /**
     * Gets the definition that was used to create this search query.
     * @return The definition that was used to create this search query.
     */
    SearchQueryDefinition getDefinition();
}
