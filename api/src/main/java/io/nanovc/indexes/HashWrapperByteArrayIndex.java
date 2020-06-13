/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.indexes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * Size and time efficient index for byte arrays so that it's quick to look up existing byte arrays by value.
 * This creates a wrapper object for each byte array that is indexed, effectively giving the byte array Value-Equality semantics.
 * This is useful in cases where we want to re-use references to bytes arrays that already exist for efficient memory lookups.
 */
public class HashWrapperByteArrayIndex implements ByteArrayIndex
{
    /**
     * The hash map of all the byte arrays that we have indexed so far.
     */
    protected final HashMap<ByteArrayWrapper, byte[]> index = new HashMap<>();

    /**
     * Checks whether the index already has bytes with the given values and returns the indexed bytes if it already exists.
     * If the bytes cannot be found then the bytes are indexed and returned.
     * @param bytes The bytes to search for by value.
     * @return The indexed bytes that match the value of the input bytes. This will be the same instance as the input if we have not previously indexed these values before.
     */
    public byte[] addOrLookup(byte[] bytes)
    {
        // Make sure we have an input:
        if (bytes == null) return null;
        // Now we know that the input is not null.

        // Create the wrapper for the array:
        // NOTE: This wrapper will be stored in the index if it is the first time it appears.
        //       If it already exists in the index then this wrapper instance is released for garbage collection.
        ByteArrayWrapper wrapper = new ByteArrayWrapper(bytes);

        // Lookup the wrapper in our index:
        byte[] result = this.index.computeIfAbsent(wrapper, byteArrayWrapper -> byteArrayWrapper.bytes);

        return result;
    }

    /**
     * Clears the byte array index of all the arrays that have been indexed so far.
     */
    @Override
    public void clear()
    {
        this.index.clear();
    }

    /**
     * A wrapper for a byte array so that we can use if for Value-Equality.
     */
    public static class ByteArrayWrapper
    {
        /**
         * The byte array that is being wrapped.
         */
        public final byte[] bytes;

        /**
         * The hash for this array.
         */
        public final int hash;

        /**
         * Creates a new wrapper around this array.
         * @param bytes The bytes to wrap. Can't be null.
         */
        public ByteArrayWrapper(byte[] bytes)
        {
            // Make sure we have bytes:
            Objects.requireNonNull(bytes);

            // Save the reference to the bytes.
            this.bytes = bytes;

            // Pre-compute the hash for this array:
            this.hash = Arrays.hashCode(bytes);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof ByteArrayWrapper)) return false;

            ByteArrayWrapper that = (ByteArrayWrapper) o;

            // Make sure the hashes match and the values:
            return this.hash == that.hash && Arrays.equals(bytes, that.bytes);
        }

        @Override
        public int hashCode()
        {
            return this.hash;
        }
    }

}
