/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.searches.commits.expressions;

import io.nanovc.SearchQueryDefinition;
import io.nanovc.reflection.ClassType;

/**
 * The base class for expressions for {@link SearchQueryDefinition}'s.
 * @param <T> The type of value that this expression returns.
 */
public abstract class Expression<T>
{
    /**
     * The specific return type for this expression.
     * We need to keep this because of Type Erasure in Java.
     */
    private final ClassType returnType;

    /**
     * Creates a new expression with the specific return type.
     * We need to provide this explicitly because of Type Erasure in Java.
     * @param returnType The specific return type for this expression. We need to provide this explicitly because of Type Erasure in Java.
     */
    public Expression(ClassType returnType)
    {
        this.returnType = returnType;
    }

    /**
     * Gets the specific return type for this expression.
     * We need to keep this because of Type Erasure in Java.
     * @return The specific return type for this expression.
     */
    public ClassType getReturnType()
    {
        return returnType;
    }

    //#region Expression Factory Methods (for convenience)

    /**
     * Creates a new expression with this expression as the left operand and the given argument as the right operand to check for equality.
     * @param rightExpression The right expression to check for equality with this expression.
     * @return A new expression that checks equality between this expression and the given right expression.
     */
    public EqualExpression<T> Equals(Expression<T> rightExpression)
    {
        return new EqualExpression<>(this.returnType,this, rightExpression);
    }

    /**
     * Creates a new expression with this expression as the left operand and the given value as a constant argument as the right operand to check for equality.
     * @param value The value to use as a constant for the right operand when checking for equality.
     * @return A new expression that checks equality between this expression and the given right constant.
     */
    public EqualExpression<T> EqualsConstant(T value)
    {
        return new EqualExpression<>(this.returnType,this, ConstantExpression.of(this.returnType, value));
    }

    /**
     * Creates a new expression with this expression as the left operand and the given argument as the right operand to check for inequality.
     * @param rightExpression The right expression to check for inequality with this expression.
     * @return A new expression that checks inequality between this expression and the given right expression.
     */
    public NotEqualExpression<T> NotEquals(Expression<T> rightExpression)
    {
        return new NotEqualExpression<>(this.returnType,this, rightExpression);
    }

    /**
     * Creates a new expression with this expression as the left operand and the given value as a constant argument as the right operand to check for inequality.
     * @param value The value to use as a constant for the right operand when checking for inequality.
     * @return A new expression that checks inequality between this expression and the given right constant.
     */
    public NotEqualExpression<T> NotEqualsConstant(T value)
    {
        return new NotEqualExpression<>(this.returnType,this, ConstantExpression.of(this.returnType, value));
    }

    //#endregion


    /**
     * Returns a string representation of the expression.
     *
     * @return a string representation of the expression.
     */
    @Override
    public String toString()
    {
        if (this.returnType == null) return super.toString();
        return "?:" + this.returnType.toString();
    }
}
