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
 * The base class for all binary expressions which take two arguments.
 *
 * @param <L> The specific type for the return type of the left operand.
 * @param <R> The specific type for the return type of the right operand.
 * @param <T> The return type for this expression.
 */
public abstract class BinaryExpression<L, R, T> extends Expression<T>
{
    /**
     * The specific type for the left operand.
     * We need to provide this explicitly because of Type Erasure in Java.
     */
    private final ClassType leftType;

    /**
     * The specific type for the right operand.
     * We need to provide this explicitly because of Type Erasure in Java.
     */
    private final ClassType rightType;

    /**
     * The left operand for this binary expression.
     */
    private final Expression<L> left;

    /**
     * The right operand for this binary expression.
     */
    private final Expression<R> right;

    /**
     * Creates a new binary expression with the given left and right operands.
     *
     * @param returnType The specific return type for this expression. We need to provide this explicitly because of Type Erasure in Java.
     * @param leftType The specific return type for the left operand. We need to provide this explicitly because of Type Erasure in Java.
     * @param rightType The specific return type for the right operand. We need to provide this explicitly because of Type Erasure in Java.
     * @param left  The left operand for this binary expression.
     * @param right The right operand for this binary expression.
     */
    public BinaryExpression(ClassType returnType, ClassType leftType, ClassType rightType, Expression<L> left, Expression<R> right)
    {
        super(returnType);
        this.leftType = leftType;
        this.rightType = rightType;
        this.left = left;
        this.right = right;
    }

    /**
     * Gets the specific type for the left operand.
     * We need to provide this explicitly because of Type Erasure in Java.
     * @return The specific type for the left operand.
     */
    public ClassType getLeftType()
    {
        return leftType;
    }

    /**
     * Gets the specific type for the right operand.
     * We need to provide this explicitly because of Type Erasure in Java.
     * @return The specific type for the right operand.
     */
    public ClassType getRightType()
    {
        return rightType;
    }

    /**
     * The left operand for this binary expression.
     *
     * @return The left operand for this binary expression.
     */
    public Expression<L> getLeft()
    {
        return this.left;
    }

    /**
     * The right operand for this binary expression.
     *
     * @return The right operand for this binary expression.
     */
    public Expression<R> getRight()
    {
        return this.right;
    }
}
