/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.searches.commits.expressions;

import io.nanovc.reflection.ClassType;

/**
 * The base class for logical expressions which have one boolean operand and return a boolean value.
 */
public abstract class LogicalUnaryExpression extends UnaryExpression<Boolean, Boolean>
{
    /**
     * Creates a new logical unary expression with the given operand.
     *
     * @param operand The operand for this logical unary expression.
     */
    public LogicalUnaryExpression(Expression<Boolean> operand)
    {
        super(ClassType.of(Boolean.class), ClassType.of(Boolean.class), operand);
    }

    //#region Expression Factory Methods (for convenience)

    /**
     * Creates a new expression that is the logical NOT of this constant.
     * @return A new expression that is the logical NOT of this constant.
     */
    public NotExpression Not()
    {
        return new NotExpression(this);
    }

    /**
     * Creates a new AND expression with this as the left operand and the given expression as the right operand.
     * @param right The right operand to use for the AND operator.
     * @return A new AND expression for this operand and the given right operand.
     */
    public AndExpression And(Expression<Boolean> right)
    {
        return new AndExpression(this, right);
    }

    /**
     * Creates a new OR expression with this as the left operand and the given expression as the right operand.
     * @param right The right operand to use for the OR operator.
     * @return A new OR expression for this operand and the given right operand.
     */
    public OrExpression Or(Expression<Boolean> right)
    {
        return new OrExpression(this, right);
    }

    //#endregion
}
