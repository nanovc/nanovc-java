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
 * This means that an area might have content with different encodings for each piece of content.
 */
public class EncodedStringContent
    extends StringContentBase
{

    /**
     * The character set to use to encode the string.
     */
    public final Charset charset;

    /**
     * Creates a content wrapper around the given string and uses the given character set to encode it into bytes.
     * @param value The string value to wrap as content.
     * @param charset The character set to use to encode the string as bytes. See {@link StandardCharsets} for common character sets.
     */
    public EncodedStringContent(String value, Charset charset)
    {
        super(value);
        Objects.requireNonNull(charset);
        this.charset = charset;
    }

    /**
     * Creates a content wrapper around the given string bytes and uses the given character set to encode it to a string.
     * @param bytes   The bytes for the string content. It is assumed that the string is encoded with the given charset.
     * @param charset The character set to use to encode the string as bytes. See {@link StandardCharsets} for common character sets.
     */
    public EncodedStringContent(byte[] bytes, Charset charset)
    {
        super(bytes, charset);
        Objects.requireNonNull(charset);
        this.charset = charset;
    }

    /**
     * Gets the character set to use to encode the string content as it was provided when the content was created.
     *
     * @return The character set used to encode the string content.
     */
    @Override public Charset getCharset()
    {
        return this.charset;
    }

    /**
     * Gets a new instance of this content as {@link StringContent} using {@link StandardCharsets#UTF_8} as the character set.
     * @return A new instance of this content as {@link StringContent}. It uses {@link StandardCharsets#UTF_8} as the character set.
     */
    public StringContent asStringContent()
    {
        return new StringContent(this.value);
    }

}
