/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.indexes;

/**
 * An efficient index for byte arrays so that it's quick to look up existing byte arrays by value.
 * This is useful in cases where we want to re-use references to bytes arrays that already exist for efficient memory lookups.
 */
public interface ByteArrayIndex
{
    /**
     * Checks whether the index already has bytes with the given values and returns the indexed bytes if it already exists.
     * If the bytes cannot be found then the bytes are indexed and returned.
     * @param bytes The bytes to search for by value.
     * @return The indexed bytes that match the value of the input bytes. This will be the same instance as the input if we have not previously indexed these values before.
     */
    byte[] addOrLookup(byte[] bytes);

    /**
     * Clears the byte array index of all the arrays that have been indexed so far.
     */
    void clear();
}
