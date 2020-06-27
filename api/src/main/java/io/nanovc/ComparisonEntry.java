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
 * An entry in a {@link ComparisonAPI} which defines the path for each piece of {@link ContentAPI} in both {@link AreaAPI}'s that were compared.
 */
@Record
public class ComparisonEntry
{
    /**
     * The path of the content in the comparison.
     */
    public final RepoPath path;

    /**
     * The state of the comparison for this entry.
     */
    public final ComparisonState state;

    /**
     * Creates a new comparison entry for the given path with the given comparison state.
     * @param path The path of the content in the comparison.
     * @param state The state of the comparison for this entry.
     */
    public ComparisonEntry(RepoPath path, ComparisonState state)
    {
        this.path = path;
        this.state = state;
    }


    @Override
    public String toString()
    {
        if (this.path == null || this.state == null)
        {
            return super.toString();
        }
        else
        {
            return this.path + ": " + this.state;
        }
    }
}
