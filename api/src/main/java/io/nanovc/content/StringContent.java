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
 * Content that wraps a {@link String}.
 * The encoding to bytes is defined by the character set associated with the content.
 */
public class StringContent extends ContentWithByteArrayBase implements ContentWithByteArray, ImmutableContent
{
    /**
     * The string content that is being represented.
     */
    public final String value;

    /**
     * The character set to use to encode the string.
     */
    public final Charset charset;

    /**
     * Creates a content wrapper around the given string and uses the given character set to encode it into bytes.
     * @param value The string value to wrap as content.
     * @param charset The character set to use to encode the string as bytes. See {@link StandardCharsets} for common character sets.
     */
    public StringContent(String value, Charset charset)
    {
        Objects.requireNonNull(charset);
        this.value = value;
        this.charset = charset;
    }

    /**
     * Creates new string content with UTF8 encoding.
     * @param value The string value to wrap as content. The string is encoded with UTF8.
     */
    public StringContent(String value)
    {
        this(value, StandardCharsets.UTF_8);
    }

    /**
     * Creates a content wrapper around the given string bytes and uses the given character set to encode it to a string.
     * @param bytes   The bytes for the string content. It is assumed that the string is encoded with the given charset.
     * @param charset The character set to use to encode the string as bytes. See {@link StandardCharsets} for common character sets.
     */
    public StringContent(byte[] bytes, Charset charset)
    {
        Objects.requireNonNull(bytes);
        Objects.requireNonNull(charset);
        this.value = new String(bytes, charset);
        this.charset = charset;
    }

    /**
     * Creates a content wrapper around the given string bytes and uses UTF8 to encode it to a string.
     * @param bytes   The bytes for the string content. It is assumed that the string is encoded with the UTF8 charset.
     */
    public StringContent(byte[] bytes)
    {
        Objects.requireNonNull(bytes);
        this.value = new String(bytes, StandardCharsets.UTF_8);
        this.charset = StandardCharsets.UTF_8;
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
        else return this.value.getBytes(this.charset);
    }

    @Override
    public String toString()
    {
        return '\'' + value + '\'';
    }
}
