/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.differences;

import io.nanovc.*;

/**
 * The base class for difference handlers that use {@link java.util.HashMap}'s internally.
 * This represents the public API when computing {@link DifferenceAPI}'s.
 * It holds common state being worked on and the {@link DifferenceEngineAPI} that contains the specific algorithm that we are interested in when computing differences.
 * The core functionality is delegated to the {@link DifferenceEngineAPI} which is stateless and can be reused across multiple threads.
 * @param <TEngine>  The specific type of engine that computes the differences.
 */
public abstract class HashMapDifferenceHandlerBase<TEngine extends HashMapDifferenceEngineAPI>
    extends DifferenceHandlerBase<TEngine>
    implements HashMapDifferenceHandlerAPI<TEngine>
{
    /**
     * Creates a new handler with the given engine for computing the differences.
     *
     * @param engine The engine to use for computing for computing the differences.
     */
    public HashMapDifferenceHandlerBase(TEngine engine)
    {
        super(engine);
    }

    /**
     * Computes a difference between the given areas.
     *
     * @param fromArea The first area to find differences from.
     * @param toArea   The second are to find differences to.
     * @return The differences between the given areas.
     */
    @Override
    public DifferenceAPI computeDifference(AreaAPI<? extends ContentAPI> fromArea, AreaAPI<? extends ContentAPI> toArea)
    {
        return this.getEngine().computeDifference(fromArea, toArea);
    }
}
