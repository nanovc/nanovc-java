/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.content;

/**
 * Content that wraps a byte array.
 */
public class ByteArrayContent
    extends ContentWithByteArrayBase
    implements ContentWithByteArrayAPI, ImmutableContent
{
    /**
     * The byte content that is being represented.
     */
    public final byte[] bytes;

    /**
     * Creates content for the given byte array.
     * @param bytes The byte array for the content.
     */
    public ByteArrayContent(byte[] bytes)
    {
        this.bytes = bytes;
    }

    /**
     * Gets the content as a byte array.
     * It is assumed that getting this data structure is efficient for this content.
     *
     * @return The content as a byte array. If the content is null then you get an empty array.
     */
    @Override
    public byte[] getEfficientByteArray()
    {
        if (this.bytes == null) return new byte[0];
        else return this.bytes;
    }

    @Override
    public String toString()
    {
        // Check whether we have bytes.
        if (this.bytes == null)
        {
            // We do not have any bytes.
            return "byte[0]";
        }
        else
        {
            // We have bytes.

            // Check how long the bytes are:
            if (bytes.length <= 100)
            {
                // The bytes are small enough to display.
                return "byte[" + bytes.length + "] ➡ '" + new String(this.bytes) + '\'';
            }
            else
            {
                // The bytes are large.
                return "byte[" + bytes.length + "]  ➡ '" + new String(this.bytes, 0, 50) + "..." + new String(this.bytes, this.bytes.length - 50, 50) + '\'';
            }
        }
    }
}
