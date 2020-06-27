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
 * An expression to get the tip of a list of commits.
 * This is usually combined with {@link AllRepoCommitsExpression}.
 */
public class TipOfExpression extends CommitExpression
{
    /**
     * The operand to get the tip of.
     */
    private final CommitsExpression operand;

    /**
     * Creates a new expression to get the tip of the commits given by the operand.
     * @param operand The operand to get the tip of the commits from.
     */
    public TipOfExpression(CommitsExpression operand)
    {
        this.operand = operand;
    }

    /**
     * The operand to get the tip of.
     * @return The operand to get the tip of.
     */
    public CommitsExpression getOperand()
    {
        return operand;
    }

    @Override
    public String toString()
    {
        if (this.operand == null) return super.toString();
        return "tipOf(" + this.operand.toString() + ")";
    }
}
