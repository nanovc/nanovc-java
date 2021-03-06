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
 * An base class that actually computes the differences between two {@link AreaAPI}'s of {@link ContentAPI}.
 * This represents the internal API for a {@link DifferenceHandlerAPI}.
 * It must contain ALL the actual logic for computing the differences between two {@link AreaAPI}'s.
 * Please make sure that the {@link DifferenceHandlerAPI} does not contain any actual functionality because it means that new higher level engines can't be created and swapped out because they might have a dependency on the {@link DifferenceHandlerAPI}.
 * A {@link DifferenceEngineAPI} does not contain any state. Just the logic of how to compute the differences.
 * This is good where one {@link DifferenceEngineAPI} is going to be reused across many {@link DifferenceHandlerAPI}'s.
 * A {@link DifferenceEngineAPI} is thread safe because it is stateless.
 * It is designed to be able to compute many differences between {@link AreaAPI}'s.
 */
public interface DifferenceEngineAPI
{
    // NOTE: There is no generic API for all engines. Each specific technique will determine an appropriate private API between the handler and the engine.
}
