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
 * A logical boolean constant value.
 */
public class LogicalConstantExpression extends ConstantExpression<Boolean>
{
    /**
     * Creates a new constant expression with the given value.
     *
     * @param value The value to use for this logical constant expression.
     */
    public LogicalConstantExpression(boolean value)
    {
        super(ClassType.of(Boolean.class), value);
    }

    /**
     * Creates a new true logical constant expression.
     * @return A new true logical constant expression.
     */
    public static LogicalConstantExpression True()
    {
        return new LogicalConstantExpression(true);
    }

    /**
     * Creates a new false logical constant expression.
     * @return A new false logical constant expression.
     */
    public static LogicalConstantExpression False()
    {
        return new LogicalConstantExpression(false);
    }

    /**
     * Creates a new true logical constant expression for the given value.
     * @param logicalValue The logical value to use for the constant expression.
     * @return A new logical constant expression with the given value.
     */
    public static LogicalConstantExpression of(boolean logicalValue)
    {
        return new LogicalConstantExpression(logicalValue);
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
