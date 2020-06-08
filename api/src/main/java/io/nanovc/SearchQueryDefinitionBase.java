/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import io.nanovc.searches.commits.expressions.Expression;

import java.util.List;

/**
 * This is the definition of a query to search for {@link Commit}'s.
 * It captures any arguments for the query expression (parameters).
 * The definition can be reused multiple times to produce {@link SearchQuery}'s.
 * This is part of the {@link CRUSHED#READ} concepts.
 */
public class SearchQueryDefinitionBase implements SearchQueryDefinition
{
    /**
     * The expression to evaluate for the commit search.
     * This can be null if you want a list of commits as a result (see {@link #listOfCommitsExpression}).
     * If this is specified and the {@link #listOfCommitsExpression} then this one is used instead.
     */
    private final Expression<Commit> singleCommitExpression;

    /**
     * The expression to evaluate to get a list of commits for the search.
     * This can be null if you only want a single commit as a result (see {@link #singleCommitExpression}).
     * If this is specified and the {@link #singleCommitExpression} then the {@link #singleCommitExpression} is used instead.
     */
    private final Expression<List<Commit>> listOfCommitsExpression;

    /**
     * The parameters for the expression.
     */
    private final SearchParameters parameters;

    /**
     * Create a new search query definition.
     * @param singleCommitExpression  The expression to get a single commit for the search. Pass null if you want to search for a list of commits instead.
     * @param listOfCommitsExpression The expression to get a list of commits for the search. Pass null if you want to search for a single commit instead.
     * @param parameters The parameters to use for the search query.
     */
    public SearchQueryDefinitionBase(Expression<Commit> singleCommitExpression, Expression<List<Commit>> listOfCommitsExpression, SearchParameters parameters)
    {
        this.singleCommitExpression = singleCommitExpression;
        this.listOfCommitsExpression = listOfCommitsExpression;
        this.parameters = parameters;
    }

    @Override
    public String toString()
    {
        if (this.singleCommitExpression != null)
        {
            return this.singleCommitExpression.toString();
        }
        else
        {
            if (this.listOfCommitsExpression != null)
            {
                return this.listOfCommitsExpression.toString();
            }
            else
            {
                return super.toString();
            }
        }
    }

    /**
     * The expression to evaluate for the commit search.
     * This can be null if you want a list of commits as a result (see {@link #getListOfCommitsExpression}).
     * If this is specified and the {@link #getListOfCommitsExpression} then this one is used instead.
     */
    @Override
    public Expression<Commit> getSingleCommitExpression()
    {
        return this.singleCommitExpression;
    }

    /**
     * The expression to evaluate to get a list of commits for the search.
     * This can be null if you only want a single commit as a result (see {@link #getSingleCommitExpression}).
     * If this is specified and the {@link #getSingleCommitExpression} then the {@link #getSingleCommitExpression} is used instead.
     */
    @Override
    public Expression<List<Commit>> getListOfCommitsExpression()
    {
        return this.listOfCommitsExpression;
    }

    /**
     * The parameters for the expression.
     */
    @Override
    public SearchParameters getParameters()
    {
        return this.parameters;
    }
}
