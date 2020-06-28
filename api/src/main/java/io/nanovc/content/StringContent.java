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

/**
 * Content that wraps a {@link String}.
 * The encoding is {@link StandardCharsets#UTF_8}.
 * This means that an Area with {@link StringContent} only has a guaranteed encoding for all pieces of content in that area.
 * Since this encoding is the most common usage scenario for {@link StringContent}, we decided to use the shorter name for that scenario.
 * If you want to specify your own encoding then use {@link EncodedStringContent} instead.
 */
public class StringContent
    extends StringContentBase
{
    /**
     * The character set used for String Content.
     * This is {@link StandardCharsets#UTF_8}.
     */
    public static final Charset STRING_CONTENT_CHARSET = StandardCharsets.UTF_8;

    /**
     * Creates a content wrapper around the given string and uses the given character set to encode it into bytes.
     * @param value The string value to wrap as content.
     */
    public StringContent(String value)
    {
        super(value);
    }

    /**
     * Creates a content wrapper around the given string bytes and uses UTF8 to encode it to a string.
     * @param bytes   The bytes for the string content. It is assumed that the string is encoded with the UTF8 charset.
     */
    public StringContent(byte[] bytes)
    {
        super(bytes, STRING_CONTENT_CHARSET);
    }

    /**
     * Gets the character set to use to encode the string content.
     * @return The character set used to encode the string content. This is UTF_8 for {@link StringContent}.
     */
    public Charset getCharset()
    {
        return STRING_CONTENT_CHARSET;
    }

    /**
     * Gets a new instance of this content as {@link EncodedStringContent} using {@link StandardCharsets#UTF_8} as the character set.
     * @return A new instance of this content as {@link EncodedStringContent}. It uses {@link StandardCharsets#UTF_8} as the character set.
     */
    public EncodedStringContent asEncodedStringContent()
    {
        return new EncodedStringContent(this.value, STRING_CONTENT_CHARSET);
    }

    /**
     * Gets a new instance of this content as {@link EncodedStringContent} using the specified character set.
     * @param charsetToUse The character set to use for the {@link EncodedStringContent}.
     * @return A new instance of this content as {@link EncodedStringContent}. It uses the specified character set.
     */
    public EncodedStringContent asEncodedStringContent(Charset charsetToUse)
    {
        return new EncodedStringContent(this.value, charsetToUse);
    }
}
