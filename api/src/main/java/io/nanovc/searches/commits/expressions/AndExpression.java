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
 * The expression for the logical AND operator.
 */
public class AndExpression extends LogicalBinaryExpression
{
    /**
     * Creates an expression for the logical AND of the two operands.
     *
     * @param left  The left operand for this AND expression.
     * @param right The right operand for this AND expression.
     */
    public AndExpression(Expression<Boolean> left, Expression<Boolean> right)
    {
        super(left, right);
    }

    @Override
    public String toString()
    {
        if (this.getLeft() == null || this.getRight() == null) return super.toString();
        return "(" + this.getLeft().toString() +" AND " + this.getRight().toString() + ")";
    }
}
