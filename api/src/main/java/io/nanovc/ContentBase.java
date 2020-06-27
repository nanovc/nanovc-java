/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * A base class for {@link ContentAPI} that exists in an {@link AreaAPI}.
 * Sub class this to create your own type of content.
 */
public abstract class ContentBase implements ContentAPI
{
    /**
     * Gets the content as a byte array.
     * It is implied that the returned type is only meant for reading and should not be modified directly.
     * It will either access the efficient representation of the content if it has one or it will clone the content as the desired type each time this method is called.
     *
     * @return This content as a byte array. If there is no content then you get an empty array.
     */
    @Override
    public byte[] asByteArray()
    {
        return cloneContentAsByteArray();
    }

    /**
     * Gets the content as a byte list.
     * It is implied that the returned type is only meant for reading and should not be modified directly.
     * It will either access the efficient representation of the content if it has one or it will clone the content as the desired type each time this method is called.
     *
     * @return This content as a byte list. If there is no content then you get an empty list.
     */
    @Override
    public List<Byte> asByteList()
    {
        return cloneContentAsByteList();
    }

    /**
     * Gets the content as a byte buffer.
     * It will either access the efficient representation of the content if it has one or it will clone the content as the desired type each time this method is called.
     *
     * @return This content as a byte buffer. If there is no content then you get a byte buffer with 0 capacity.
     */
    @Override
    public ByteBuffer asByteBuffer()
    {
        return cloneContentAsByteBuffer();
    }

    /**
     * Gets the content as a byte stream.
     * It is implied that the returned type is only meant for reading and should not be modified directly.
     * It will either access the efficient representation of the content if it has one or it will clone the content as the desired type each time this method is called.
     *
     * @return This content as a byte stream. If there is no content then you get an empty stream.
     */
    @Override
    public Stream<Byte> asByteStream()
    {
        return cloneContentAsByteStream();
    }

    /**
     * The string value of this content.
     *
     * @return The string for debugging this content.
     */
    @Override
    public String toString()
    {
        // Get the content:
        byte[] content = this.asByteArray();

        // Check whether we have content:
        if (content == null)
        {
            // We don't have content.
            return "0 bytes";
        }
        else
        {
            // We do have content.
            int contentLength = content.length;

            // Check if the content is very long:
            String contentString;
            if (contentLength > 1000)
            {
                // We have a very long string.

                // Get a smaller copy of the content:
                // https://stackoverflow.com/a/19237414/231860
                byte[] shortContent = Arrays.copyOf(content, 1000);

                // Get the string to display:
                contentString = new String(shortContent);

            }
            else
            {
                // Our content is short enough to display.

                // Get the string to display:
                contentString = new String(content);
            }
            // Now we have the string of content to display.
            return String.format("%,d byte%s:\n%s", contentLength, contentLength == 1 ? "" : "s", contentString);
        }
    }
}
