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
 * An expression that checks the operands for equality.
 * @param <T> The specific type to check for equality for both operands.
 */
public class EqualExpression<T> extends BinaryExpression<T, T, Boolean>
{
    /**
     * Creates a new expression that checks equality between the given left and right operands.
     *
     * @param operandType The specific type of each operand. We need to provide this explicitly because of Type Erasure in Java.
     * @param left  The left operand to check for equality with the right operand.
     * @param right The right operand to check for equality with the left operand.
     */
    public EqualExpression(ClassType operandType, Expression<T> left, Expression<T> right)
    {
        super(ClassType.of(Boolean.class), operandType, operandType, left, right);
    }

    @Override
    public String toString()
    {
        if (this.getLeft() == null || this.getRight() == null) return super.toString();
        return "(" + this.getLeft().toString() + "==" + this.getRight().toString() + ")";
    }
}
