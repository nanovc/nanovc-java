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
 * The interface for comparison handlers.
 * This represents the public API when computing {@link Comparison}'s.
 * It holds common state being worked on and the {@link ComparisonEngine} that contains the specific algorithm that we are interested in when computing comparisons.
 * The core functionality is delegated to the {@link ComparisonEngine} which is stateless and can be reused across multiple threads.
 *
 * @param <TEngine>  The specific type of engine that computes the comparisons.
 */
public interface ComparisonHandler<TEngine extends ComparisonEngine>
{

    /**
     * Gets the engine that is used to compute the comparisons.
     * An alternate (but compatible) engine can be plugged in to modify the algorithm being used compute the comparisons.
     *
     * @return The engine that is used to compute the comparisons.
     */
    TEngine getEngine();

    /**
     * Sets the engine that is used to compute the comparisons.
     * An alternate (but compatible) engine can be plugged in to modify the algorithm being used compute the comparisons.
     *
     * @param engine The engine that is used to compute the comparisons.
     */
    void setEngine(TEngine engine);

    /**
     * Compares the given areas.
     * @param fromArea The first area to compare from.
     * @param toArea The second are to compare to.
     * @return The comparison between the given areas.
     */
    Comparison compare(Area<? extends Content> fromArea, Area<? extends Content> toArea);
}
