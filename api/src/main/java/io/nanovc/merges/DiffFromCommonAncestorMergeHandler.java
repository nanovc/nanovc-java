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
 * A merge handler that performs a diff between the common ancestor of the commits.
 */
public class DiffFromCommonAncestorMergeHandler
    extends DiffFromCommonAncestorMergeHandlerBase<DiffFromCommonAncestorMergeEngineAPI>
{
    /**
     * A common merge handler that is used as the default for Nano Repos.
     */
    public static final DiffFromCommonAncestorMergeHandler COMMON_MERGE_HANDLER = new DiffFromCommonAncestorMergeHandler();

    /**
     * Creates a new merge handler with the given engine.
     *
     * @param engine The engine to use for this handler
     */
    public DiffFromCommonAncestorMergeHandler(DiffFromCommonAncestorMergeEngineAPI engine)
    {
        super(engine);
    }

    /**
     * Creates a merge handler.
     * A {@link DiffFromCommonAncestorMergeEngine} will be used.
     */
    public DiffFromCommonAncestorMergeHandler()
    {
        this(new DiffFromCommonAncestorMergeEngine());
    }
}
