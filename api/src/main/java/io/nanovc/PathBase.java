/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import java.util.Arrays;
import java.util.Objects;

/**
 * A base class for specific types of paths in a {@link Repo}.
 * This is lighter weight version of Java Paths.
 * We don't need the file system functionality.
 * Absolute paths start with a / ({@link #DELIMITER})
 * Relative paths do not start with a / ({@link #DELIMITER})
 * @param <TSelf> The specific type of path being implemented. This is needed so that we can get chained calls with the specific type of path.
 */
public abstract class PathBase<TSelf extends Path<TSelf>> implements Path<TSelf>
{

    /**
     * The delimiter used for sub folders in a path.
     * Creates the given repo path.
     */
    public static final String DELIMITER = "/";

    /**
     * The path in the repository.
     * This uses / to denote folder boundaries. See ({@link #DELIMITER}).
     */
    public final String path;

    /**
     * Creates the given path.
     * @param relativeOrAbsolutePath The relative or absolute path to resolve. Absolute paths start with a / {@link #DELIMITER}. Relative paths don't.
     */
    public PathBase(String relativeOrAbsolutePath)
    {
        this.path = relativeOrAbsolutePath;
    }

    /**
     * A factory method to create a new instance of the specific path.
     * @param relativeOrAbsolutePath The relative or absolute path to resolve. Absolute paths start with a / {@link #DELIMITER}. Relative paths don't.
     * @return The new instance at the given path.
     */
    protected abstract TSelf createInstance(String relativeOrAbsolutePath);

    /**
     * Resolves the relative path from the current path.
     * It supports relative and absolute paths.
     *
     * @param relativeOrAbsolutePath The relative or absolute path to resolve. If it is relative then it is appended to the current path. If it is absolute then the absolute path is returned and the current path is ignored.
     * @return The resolved path.
     */
    @Override
    public TSelf resolve(String relativeOrAbsolutePath)
    {

        // Check whether the input is relative or absolute:
        if (isAbsolute(relativeOrAbsolutePath))
        {
            // Just use the input path as an absolute path:
            // NOTE: It doesn't matter whether the current path is absolute or relative, always use the input absolute path as the output.
            return createInstance(relativeOrAbsolutePath);
        }
        else
        {
            // The input is a relative path.

            // Check whether the current path already has a delimiter:
            if (hasEndingDelimiter(this.path))
            {
                // The current path ends with the delimiter.
                return createInstance(this.path + relativeOrAbsolutePath);
            }
            else
            {
                // The current path does not have a delimiter.
                return createInstance(String.join(DELIMITER, this.path, relativeOrAbsolutePath));
            }
        }
    }

    /**
     * This ensures that the path has a delimiter at the end.
     * If the path already has a delimiter then the same instance is returned.
     *
     * @return The path with a delimiter at the end. This existing instance is returned if it already has a delimiter at the end.
     */
    @Override
    public TSelf ensureEndsWithDelimiter()
    {
        // Check whether the current path already has a delimiter:
        if (hasEndingDelimiter(this.path))
        {
            // The current path ends with the delimiter.
            return (TSelf) this;
        }
        else
        {
            // The current path does not have a delimiter.
            return createInstance(this.path + DELIMITER);
        }
    }

    /**
     * Returns an absolute path from this path.
     * If it already is an absolute path it returns itself without making a new instance.
     *
     * @return An absolute path for the current path.
     */
    @Override
    public TSelf toAbsolutePath()
    {
        if (isAbsolute(this.path))
        {
            // The current path is absolute.
            return (TSelf)this;
        }
        else
        {
            // The current path is relative.
            // Prepend the delimiter to make it absolute:
            return createInstance(DELIMITER + this.path);
        }
    }

    /**
     * A string version of the current repo path.
     *
     * @return a string representation of the repo path.
     */
    @Override
    public String toString()
    {
        return this.path == null ? "" : this.path;
    }

    /**
     * Splits the path into separate parts, broken up by the path delimiter '/'.
     * If it's an absolute path then the first part is the entry straight under the root (the blank string for the root is stripped off).
     * @return The separate parts of the path.
     */
    @Override
    public String[] splitIntoParts()
    {
        // Split the path by the delimiter:
        String[] parts = this.path.split(DELIMITER);

        // Remove any blank parts:
        String[] cleanParts = Arrays.stream(parts).filter(part -> part != null && !part.isEmpty()).toArray(String[]::new);

        return cleanParts;
    }

    /**
     * Checks whether the given path is relative or absolute.
     *
     * @param relativeOrAbsolutePath The relative or absolute path to check. If it starts with the delimiter / then it is absolute. If it doesnt then it's relative.
     * @return True if this is an absolute path, meaning that it starts with a delimiter. False if it does not start with the delimiter.
     */
    static boolean isAbsolute(String relativeOrAbsolutePath)
    {
        return (relativeOrAbsolutePath != null && !relativeOrAbsolutePath.isEmpty()) && relativeOrAbsolutePath.startsWith(DELIMITER);
    }

    /**
     * Checks whether the given path has an ending path delimiter.
     *
     * @param relativeOrAbsolutePath The relative or absolute path to check.
     * @return True if this is an ending delimiter. False if it does not end with the delimiter or if the input was null or empty.
     */
    static boolean hasEndingDelimiter(String relativeOrAbsolutePath)
    {
        return (relativeOrAbsolutePath != null && !relativeOrAbsolutePath.isBlank()) && relativeOrAbsolutePath.endsWith(DELIMITER);
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof PathBase)) return false;

        PathBase<?> pathBase = (PathBase<?>) o;

        return Objects.equals(path, pathBase.path);
    }

    @Override
    public int hashCode()
    {
        return path != null ? path.hashCode() : 0;
    }
}
