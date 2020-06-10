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
 * The base class for difference handlers.
 * This represents the public API when computing {@link Difference}'s.
 * It holds common state being worked on and the {@link DifferenceEngine} that contains the specific algorithm that we are interested in when computing differences.
 * The core functionality is delegated to the {@link DifferenceEngine} which is stateless and can be reused across multiple threads.
 *
 * @param <TEngine>  The specific type of engine that computes the differences.
 */

public abstract class DifferenceHandlerBase<TEngine extends DifferenceEngine> implements DifferenceHandler<TEngine>
{
    /**
     * The engine that actually computes the differences for this handler.
     */
    private TEngine engine;

    /**
     * Creates a new handler with the given engine for computing the differences.
     * @param engine The engine to use for computing for computing the differences.
     */
    public DifferenceHandlerBase(TEngine engine)
    {
        this.engine = engine;
    }

    /**
     * Gets the engine that actually computes the differences for this handler.
     * @return The engine that actually computes the differences for this handler.
     */
    @Override
    public TEngine getEngine()
    {
        return engine;
    }

    /**
     * Sets the engine that actually computes the differences for this handler.
     * @param engine The engine that is used to compute the differences.
     */
    @Override
    public void setEngine(TEngine engine)
    {
        this.engine = engine;
    }
}