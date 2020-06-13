/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

/**
 * A path in the repository.
 * This is lighter weight version of Java Paths.
 * We don't need the file system functionality.
 * Absolute paths start with a /
 * Relative paths do not start with a /.
 */
public class RepoPath extends PathBase<RepoPath>
{
    /**
     * Creates the given repo path.
     * @param relativeOrAbsolutePath The relative or absolute path to resolve. Absolute paths start with a / {@link #DELIMITER}. Relative paths don't.
     */
    public RepoPath(String relativeOrAbsolutePath)
    {
        super(relativeOrAbsolutePath);
    }

    /**
     * A factory method to create a new instance of the specific path.
     *
     * @param relativeOrAbsolutePath The relative or absolute path to resolve. Absolute paths start with a / {@link #DELIMITER}. Relative paths don't.
     * @return The new instance at the given path.
     */
    @Override
    protected RepoPath createInstance(String relativeOrAbsolutePath)
    {
        return new RepoPath(relativeOrAbsolutePath);
    }

    /**
     * Creates a repo path at the given location.
     * This is a convenience factory method to construct repo paths.
     * @param relativeOrAbsolutePath The relative or absolute path for the repository. Absolute paths start with /. Relative paths don't.
     * @return A repo path at the given location.
     */
    public static RepoPath at(String relativeOrAbsolutePath)
    {
        return new RepoPath(relativeOrAbsolutePath);
    }

    /**
     * Creates a repo path at the root.
     * This is a convenience factory method to construct repo paths.
     * @return A repo path at the root.
     */
    public static RepoPath atRoot()
    {
        return new RepoPath(DELIMITER);
    }


}
