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
 * A base class for a handler that merges commits.
 * The merge handler represents the public API between the handler and the engine.
 * @param <TEngine> The specific type of engine that this handler holds.
 */
public abstract class MergeHandlerBase<TEngine extends MergeEngine> implements MergeHandler<TEngine>
{
    /**
     * The merge engine that is being held by this handler.
     * The engine does all the work for merging commits.
     */
    private TEngine engine;

    /**
     * Creates a new merge handler with the given engine.
     * @param engine The engine to use for this handler
     */
    public MergeHandlerBase(TEngine engine)
    {
        this.engine = engine;
    }

    /**
     * Creates a merge handler.
     * The engine will need to be set explicitly.
     */
    public MergeHandlerBase()
    {
    }

    /**
     * Gets the engine being used by this handler.
     * The engine does all the work for merging commits.
     *
     * @return The engine that is being used by this handler
     */
    @Override
    public TEngine getEngine()
    {
        return this.engine;
    }

    /**
     * Sets the engine to use for this handler.
     * The engine does all the work for merging commits.
     *
     * @param engine The engine to use for this handler.
     */
    @Override
    public void setEngine(TEngine engine)
    {
        this.engine = engine;
    }
}
