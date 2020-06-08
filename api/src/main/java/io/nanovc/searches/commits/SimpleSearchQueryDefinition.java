/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.searches.commits;

import io.nanovc.*;
import io.nanovc.searches.commits.expressions.Expression;

import java.util.List;

/**
 * This is the definition of a query to search for {@link Commit}'s.
 * It captures any arguments for the query expression (parameters).
 * The definition can be reused multiple times to produce {@link SearchQuery}'s.
 */
public class SimpleSearchQueryDefinition extends SearchQueryDefinitionBase
{

    /**
     * Create a new search query definition.
     *
     * @param singleCommitExpression  The expression to get a single commit for the search. Pass null if you want to search for a list of commits instead.
     * @param listOfCommitsExpression The expression to get a list of commits for the search. Pass null if you want to search for a single commit instead.
     * @param parameters              The parameters to use for the search query.
     */
    public SimpleSearchQueryDefinition(Expression<Commit> singleCommitExpression, Expression<List<Commit>> listOfCommitsExpression, SearchParameters parameters)
    {
        super(singleCommitExpression, listOfCommitsExpression, parameters);
    }

    /**
     * Creates a new search query definition for getting a single commit based on the given expression.
     * @param singleCommitExpression The expression to get a single commit for the search.
     * @return A search query definition that gets a single commit.
     */
    public static SimpleSearchQueryDefinition forSingleCommit(Expression<Commit> singleCommitExpression)
    {
        return new SimpleSearchQueryDefinition(singleCommitExpression, null, new HashMapSearchParameters());
    }

    /**
     * Creates a new search query definition for getting a single commit based on the given expression.
     * @param listOfCommitsExpression The expression to get a list of commits for the search.
     * @return A search query definition that gets a single commit.
     */
    public static SimpleSearchQueryDefinition forListOfCommits(Expression<List<Commit>> listOfCommitsExpression)
    {
        return new SimpleSearchQueryDefinition(null, listOfCommitsExpression, new HashMapSearchParameters());
    }
}
