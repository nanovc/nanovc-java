/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.comparisons;

import io.nanovc.Area;
import io.nanovc.Comparison;
import io.nanovc.ComparisonEngine;
import io.nanovc.Content;

/**
 * A comparison engine that uses hash maps to compute the comparisons between two {@link Area}'s of {@link Content}.
 * This {@link HashMapComparisonEngineAPI} does not contain any state. Just the logic of how to compute the comparisons.
 * This is good where one {@link HashMapComparisonEngineAPI} is going to be reused across many {@link HashMapComparisonHandler}'s.
 * This {@link HashMapComparisonEngineAPI} is thread safe because it is stateless.
 * It is designed to be able to compute many comparisons between {@link Area}'s.
 */
public interface HashMapComparisonEngineAPI extends ComparisonEngine
{
    /**
     * Computes a comparison between the given areas.
     *
     * @param fromArea The first area to find comparisons from.
     * @param toArea   The second are to find comparisons to.
     * @return The comparisons between the given areas.
     */
    Comparison compare(Area<? extends Content> fromArea, Area<? extends Content> toArea);
}

