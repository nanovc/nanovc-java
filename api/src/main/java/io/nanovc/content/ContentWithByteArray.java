/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.content;

import io.nanovc.Content;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Content that is most efficiently accessed as a byte array.
 * By using this interface, we are able to avoid creating clones of the content if it is already of the desired type.
 */
public interface ContentWithByteArray extends Content
{
    /**
     * Gets the content as a byte array.
     * It is assumed that getting this data structure is efficient for this content.
     * @return The content as a byte array. If the content is null then you get an empty array.
     */
    byte[] getEfficientByteArray();

    /**
     * Gets a new clone of the content as a byte list.
     * A new copy of the list is created each time this method is called.
     *
     * @return A new list of the content. If there is no content then you get an empty list.
     */
    @Override
    default List<Byte> cloneContentAsByteList()
    {
        // Make sure we have content:
        byte[] content = getEfficientByteArray();
        if (content == null || content.length == 0)
        {
            // We do not have any content.
            return Collections.EMPTY_LIST;
        }
        else
        {
            // We have content.

            // Create the list:
            List<Byte> byteList = new ArrayList<>();
            for (int i = 0; i < content.length; i++)
            {
                byteList.add(content[i]);
            }
            return byteList;
        }
    }

    /**
     * Gets a clone of the content as a byte buffer.
     * It is implied that the returned type is only meant for reading and should not be modified directly.
     *
     * @return A new byte buffer for the content. If there is no content then you get a byte buffer with 0 capacity.
     */
    @Override
    default ByteBuffer cloneContentAsByteBuffer()
    {
        // Make sure we have content:
        byte[] content = getEfficientByteArray();
        if (content == null || content.length == 0)
        {
            // We do not have any content.
            return ByteBuffer.allocate(0);
        }
        else
        {
            // We have content.

            return ByteBuffer.wrap(content);
        }
    }

    /**
     * Gets a clone of the content as a byte stream.
     *
     * @return A new byte buffer for the content. If there is no content then you get an empty stream.
     */
    @Override
    default Stream<Byte> cloneContentAsByteStream()
    {
        // Make sure we have content:
        byte[] content = getEfficientByteArray();
        if (content == null || content.length == 0)
        {
            // We do not have any content.
            return Stream.empty();
        }
        else
        {
            // We have content.

            return cloneContentAsByteList().stream();
        }
    }
}
