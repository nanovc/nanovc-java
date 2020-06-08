/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.searches.commits.expressions;

/**
 * An expression to get all the commits in a branch of a {@link io.nanovc.Repo}.
 */
public class BranchCommitsExpression extends CommitsExpression
{
    /**
     * The name of the branch that we need to get all the commits for.
     */
    private final String branchName;

    /**
     * Creates a new expression to get all the commits for a branch of the repo.
     * @param branchName The name of the branch that we need to get all the commits for.
     */
    public BranchCommitsExpression(String branchName)
    {
        this.branchName = branchName;
    }

    /**
     * Creates a new expression to return all commits in the repo.
     * @param branchName The name of the branch to get all the commits for.
     * @return A new expression to return all commits in the repo.
     */
    public static BranchCommitsExpression of(String branchName)
    {
        return new BranchCommitsExpression(branchName);
    }

    @Override
    public String toString()
    {
        if (this.branchName == null) return super.toString();
        return "[Commits for branch: " + this.branchName + "]";
    }
}
