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
 * An entry in an {@link AreaAPI} which defines the path for each piece of {@link ContentAPI} in the {@link AreaAPI}.
 * @param <TContent> The specific type of content that this entry contains.
 */
public class AreaEntry<TContent extends ContentAPI>
{
    /**
     * The path of the content in the area.
     */
    public final RepoPath path;

    /**
     * The content in the area at the path for this entry.
     */
    public final TContent content;

    /**
     * Creates a new area entry for the given path and content.
     * @param path The path for the content.
     * @param content The content at the path.
     */
    public AreaEntry(RepoPath path, TContent content)
    {
        this.path = path;
        this.content = content;
    }
}
