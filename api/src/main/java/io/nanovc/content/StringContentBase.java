/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.content;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A base class for Content that wraps a {@link String}.
 * The encoding is determined by the sub-class.
 * If you want to specify your own encoding then use {@link EncodedStringContent} instead.
 */
public abstract class StringContentBase
    extends ContentWithByteArrayBase
    implements ContentWithByteArrayAPI, ImmutableContent
{
    /**
     * The string content that is being represented.
     */
    public final String value;

    /**
     * Creates a content wrapper around the given string and uses the given character set to encode it into bytes.
     * @param value The string value to wrap as content.
     */
    protected StringContentBase(String value)
    {
        this.value = value;
    }

    /**
     * Creates a content wrapper around the given string bytes and uses the given character set to encode it to a string.
     * @param bytes   The bytes for the string content. It is assumed that the string is encoded with the given charset.
     * @param charset The character set to use to encode the string as bytes. See {@link StandardCharsets} for common character sets.
     */
    protected StringContentBase(byte[] bytes, Charset charset)
    {
        Objects.requireNonNull(bytes);
        Objects.requireNonNull(charset);
        this.value = new String(bytes, charset);
    }

    /**
     * Gets the character set to use to encode the string content.
     * @return The character set used to encode the string content.
     */
    public abstract Charset getCharset();

    /**
     * Gets the string content that is being represented.
     * @return The string content that is being represented.
     */
    public String getValue()
    {
        return value;
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
        if (this.value == null) return new byte[0];
        else return this.value.getBytes(getCharset());
    }

    @Override
    public String toString()
    {
        return '\'' + value + '\'';
    }
}
