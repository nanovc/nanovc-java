/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.searches.commits;

import io.nanovc.CommitAPI;
import io.nanovc.SearchParametersAPI;
import io.nanovc.SearchQueryDefinitionAPI;
import io.nanovc.reflection.ClassType;
import io.nanovc.searches.commits.expressions.ConstantExpression;

import java.util.LinkedHashMap;

/**
 * The parameters for a search for {@link CommitAPI}'s.
 * These parameters are used in a {@link SearchQueryDefinitionAPI}.
 * This concept is similar to a parameterised query for SQL.
 */
public class LinkedHashMapSearchParameters extends LinkedHashMap<String, ConstantExpression<?>> implements SearchParametersAPI
{
    /**
     * Gets the constant expression for the given parameter name.
     *
     * @param parameterName The name of the parameter to get.
     * @return The constant expression for the parameter with the given name. Null if there is no parameter with this name.
     */
    @Override
    public ConstantExpression<?> getParameter(String parameterName)
    {
        // Get the parameter or null if it doesn't exist:
        return this.get(parameterName);
    }

    /**
     * Gets the value of the parameter with the given name.
     * If the type of the value for the parameter is not of the expected type then null is returned.
     *
     * @param parameterName         The name of the parameter to get.
     * @param expectedParameterType The expected type of the parameter. If the actual parameter doesn't match the expected type then null is returned.
     * @param <T>                   The expected type of value to get from the parameter. If the parameter is not of the expected type then null is returned.
     * @return The constant expression for the parameter with the given name. Null if there is no parameter with this name.
     */
    @Override
    public <T> T getParameterValue(String parameterName, ClassType expectedParameterType)
    {
        // Get the parameter:
        ConstantExpression<?> parameter = this.get(parameterName);
        // Check whether we have this parameter:
        if (parameter == null)
        {
            // We don't have this parameter.
            return null;
        }
        else
        {
            // We have this parameter.

            // Get the actual parameter type:
            Class<?> parameterType = parameter.getReturnType().getClassType();

            // Check whether the parameter type is as expected for the return value:
            if (parameterType.equals(expectedParameterType))
            {
                // The parameter matches teh expected parameter type exactly.
                // Return the value:
                return (T)parameter.getValue();
            }
            else
            {
                // The parameter doesn't match the expceted type exactly.
                return null;
            }
        }
    }
}
