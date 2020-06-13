/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.merges;

/**
 * A merge handler where the last change (in time) wins when a merge conflict is detected.
 */
public class LastWinsMergeHandler extends DiffingMergeHandlerBase<LastWinsMergeEngine>
{
    /**
     * Creates a new merge handler with the given engine.
     * The change from the last commit wins.
     *
     * @param lastWinsMergeEngine The engine to use for this handler
     */
    public LastWinsMergeHandler(LastWinsMergeEngine lastWinsMergeEngine)
    {
        super(lastWinsMergeEngine);
    }

    /**
     * Creates a merge handler where the change from the last commit wins.
     * A new engine is created automatically.
     */
    public LastWinsMergeHandler()
    {
        this(new LastWinsMergeEngine());
    }
}
