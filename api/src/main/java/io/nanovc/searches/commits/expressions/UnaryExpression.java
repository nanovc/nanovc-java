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
 * The base class for all unary expressions which have one operator.
 * @param <R> The specific type for the operand.
 * @param <T> The return type for this expression.
 */
public abstract class UnaryExpression<R,T> extends Expression<T>
{
    /**
     * The specific type for the operand for this unary expression.
     * We need to provide this explicitly because of Type Erasure in Java.
     */
    private final ClassType operandType;

    /**
     * The operand for this unary expression.
     */
    private final Expression<R> operand;

    /**
     * Creates a new unary expression with the given operand.
     * @param returnType  The specific return type for this expression. We need to provide this explicitly because of Type Erasure in Java.
     * @param operandType The specific type for the operand to this unary expression. We need to provide this explicitly because of Type Erasure in Java.
     * @param operand The operand for this unary expression.
     */
    public UnaryExpression(ClassType returnType, ClassType operandType, Expression<R> operand)
    {
        super(returnType);
        this.operandType = operandType;
        this.operand = operand;
    }

    /**
     * Gets the specific type for the operand for this unary expression.
     * We need to provide this explicitly because of Type Erasure in Java.
     * @return The specific type for the operand for this unary expression.
     */
    public ClassType getOperandType()
    {
        return operandType;
    }

    /**
     * The operand for this unary expression.
     * @return The operand for this unary expression.
     */
    public Expression<R> getOperand()
    {
        return this.operand;
    }
}
