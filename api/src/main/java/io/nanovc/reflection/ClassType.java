/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.reflection;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A token to store the runtime type information for an object.
 * Specifically, this focuses on storing the generic parameter types. eg: ArrayList&lt;String&gt;
 * Due to Type Erasure in Java (https://docs.oracle.com/javase/tutorial/java/generics/erasure.html)
 */
public class ClassType
{
    /**
     * The class for this class type.
     */
    private final Class<?> classType;

    /**
     * The generic parameter types.
     */
    private final ClassType[] parameterTypes;

    /**
     * Creates a new class type token for storing the runtime type of the object.
     *
     * @param classType The class that this class type is for.
     */
    public ClassType(Class<?> classType)
    {
        this.classType = classType;
        this.parameterTypes = null;
    }

    /**
     * Creates a new class type token for storing the runtime type of the object.
     *
     * @param classType      The class that this class type is for.
     * @param parameterTypes The generic parameter types for this class.
     */
    public ClassType(Class<?> classType, ClassType... parameterTypes)
    {
        this.classType = classType;
        this.parameterTypes = parameterTypes;
    }

    /**
     * Creates a new class type token for storing the runtime type of the object.
     *
     * @param classType The class that this class type is for.
     * @param <T>       The specific class that this class type is for.
     * @return A class type token that captures the type information at runtime.
     */
    public static <T> ClassType of(Class<T> classType)
    {
        return new ClassType(classType);
    }

    /**
     * Creates a new class type token for storing the runtime type of the object.
     *
     * @param classType       The class that this class type is for.
     * @param parameter1Class The first parameter type.
     * @param <T>             The specific class that this class type is for.
     * @return A class type token that captures the type information at runtime.
     * @param <P1> The specific type for the parameter.
     */
    public static <T, P1> ClassType of(Class<T> classType, Class<P1> parameter1Class)
    {
        return new ClassType(classType, ClassType.of(parameter1Class));
    }

    /**
     * Creates a new class type token for storing the runtime type of the object.
     *
     * @param classType       The class that this class type is for.
     * @param parameter1Class The first parameter type.
     * @param parameter2Class The second parameter type.
     * @param <T>             The specific class that this class type is for.
     * @param <P1>            The specific class for the first generic type parameter.
     * @param <P2>            The specific class for the second generic type parameter.
     * @return A class type token that captures the type information at runtime.
     */
    public static <T, P1, P2> ClassType of(Class<T> classType, Class<P1> parameter1Class, Class<P2> parameter2Class)
    {
        return new ClassType(classType, ClassType.of(parameter1Class), ClassType.of(parameter2Class));
    }

    @Override
    public String toString()
    {
        if (this.classType == null) return super.toString();

        // Check whether we have generic parameters:
        if (this.parameterTypes != null && this.parameterTypes.length > 0)
        {
            // We have generic parameters.
            StringJoiner stringJoiner = new StringJoiner(",", this.classType.getSimpleName() + "<", ">");
            for (ClassType parameterType : this.parameterTypes)
            {
                stringJoiner.add(parameterType.toString());
            }
            return stringJoiner.toString();
        }
        else
        {
            // There are no generic parameters.
            return this.classType.getSimpleName();
        }
    }

    /**
     * Creates a new instance by calling the parameterless constructor.
     *
     * @return A new instance of the class represented by this class type.
     * @throws NoSuchMethodException       if a matching method is not found.
     * @throws IllegalAccessException      if this {@code Constructor} object
     *                                     is enforcing Java language access control and the underlying
     *                                     constructor is inaccessible.
     * @throws IllegalArgumentException    if the number of actual
     *                                     and formal parameters differ; if an unwrapping
     *                                     conversion for primitive arguments fails; or if,
     *                                     after possible unwrapping, a parameter value
     *                                     cannot be converted to the corresponding formal
     *                                     parameter type by a method invocation conversion; if
     *                                     this constructor pertains to an enum type.
     * @throws InstantiationException      if the class that declares the
     *                                     underlying constructor represents an abstract class.
     * @throws InvocationTargetException   if the underlying constructor
     *                                     throws an exception.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     by this method fails.
     */
    public Object newInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        return this.classType.getDeclaredConstructor().newInstance();
    }

    /**
     * Gets the class for this class type.
     *
     * @return The class for this class type.
     */
    public Class<?> getClassType()
    {
        return classType;
    }

    /**
     * Gets the generic parameter types.
     *
     * @return The generic parameter types.
     */
    public ClassType[] getParameterTypes()
    {
        return parameterTypes;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ClassType)) return false;
        ClassType classType1 = (ClassType) o;
        return Objects.equals(getClassType(), classType1.getClassType()) &&
               Arrays.equals(getParameterTypes(), classType1.getParameterTypes());
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hash(getClassType());
        result = 31 * result + Arrays.hashCode(getParameterTypes());
        return result;
    }
}
