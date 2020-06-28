/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.differences;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;
import io.nanovc.DifferenceAPI;
import io.nanovc.DifferenceEngineAPI;

/**
 * A difference engine that uses hash maps to compute the differences between two {@link AreaAPI}'s of {@link ContentAPI}.
 * This {@link HashMapDifferenceEngineAPI} does not contain any state. Just the logic of how to compute the differences.
 * This is good where one {@link HashMapDifferenceEngineAPI} is going to be reused across many {@link HashMapDifferenceHandler}'s.
 * This {@link HashMapDifferenceEngineAPI} is thread safe because it is stateless.
 * It is designed to be able to compute many differences between {@link AreaAPI}'s.
 */
public interface HashMapDifferenceEngineAPI
    extends DifferenceEngineAPI
{

    /**
     * Computes a difference between the given areas.
     *
     * @param fromArea The first area to find differences from.
     * @param toArea   The second are to find differences to.
     * @return The differences between the given areas.
     */
    DifferenceAPI computeDifference(AreaAPI<? extends ContentAPI> fromArea, AreaAPI<? extends ContentAPI> toArea);
}

