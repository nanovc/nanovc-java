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
 * A constant expression with a specific value.
 */
public class ConstantExpression<T> extends Expression<T>
{
    /**
     * The value for this constant expression.
     */
    private final T value;

    /**
     * Creates a new constant expression with the given value.
     * @param valueType The specific type for this constant value. We need to provide this explicitly because of Type Erasure in Java.
     * @param value The value to use for this constant expression.
     */
    public ConstantExpression(ClassType valueType, T value)
    {
        super(valueType);
        this.value = value;
    }

    /**
     * Gets the value for this constant expression.
     * @return The value for this constant expression.
     */
    public T getValue()
    {
        return value;
    }

    /**
     * Creates a constant expression of the given value.
     * @param valueType The specific type for the constant value. We need to provide this explicitly because of Type Erasure in Java.
     * @param value The value to use for the constant expression.
     * @param <T> The specific type for the constant value.
     * @return A constant expression for the given value.
     */
    public static <T> ConstantExpression<T> of(ClassType valueType, T value)
    {
        return new ConstantExpression<>(valueType, value);
    }

    /**
     * Creates a constant expression of the given value.
     * @param valueType The specific type for the constant value. We need to provide this explicitly because of Type Erasure in Java.
     * @param value The value to use for the constant expression.
     * @param <T> The specific type for the constant value.
     * @return A constant expression for the given value.
     */
    public static <T> ConstantExpression<T> of(Class<T> valueType, T value)
    {
        return new ConstantExpression<>(ClassType.of(valueType), value);
    }

    @Override
    public String toString()
    {
        if (this.value == null || this.getReturnType() == null) return super.toString();
        return this.value.toString() + ":" + this.getReturnType().toString();
    }
}
