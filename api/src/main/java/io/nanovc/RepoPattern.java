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
 * A base class for specific types of patterns in a {@link Repo}.
 * We use glob like syntax eg: *.json or **.json (to cross path boundaries).
 * Absolute paths start with a / ({@link PathBase#DELIMITER})
 * Relative paths do not start with a / ({@link PathBase#DELIMITER})
 */
public class RepoPattern extends PatternBase<RepoPattern, RepoPath>
{

    /**
     * Creates a pattern that matches the given paths.
     * @param globPattern The pattern of paths to match. We use glob like syntax eg: *.json or **.json (to cross path boundaries).
     */
    public RepoPattern(String globPattern)
    {
        super(globPattern);
    }

    /**
     * A factory method to create a new instance of the specific path.
     *
     * @param globPattern The pattern of paths to match. We use glob like syntax eg: *.json or **.json (to cross path boundaries).
     * @return The new instance at the given path.
     */
    @Override
    protected RepoPattern createInstance(String globPattern)
    {
        return new RepoPattern(globPattern);
    }

    /**
     * Creates a repo pattern that matches the given {@link RepoPath}'s.
     * @param globPattern The pattern of paths to match. We use glob like syntax eg: *.json or **.json (to cross path boundaries).
     * @return The pattern that matches the given {@link RepoPath}'s.
     */
    public static RepoPattern matching(String globPattern)
    {
        return new RepoPattern(globPattern);
    }
}
