/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import io.nanovc.content.ContentWithByteArray;
import io.nanovc.content.ContentWithByteBuffer;
import io.nanovc.content.ContentWithByteList;
import io.nanovc.content.ContentWithByteStream;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Stream;

/**
 * Content that is capable of being version controlled in a {@link Repo}.
 * The content is placed at a specific {@link RepoPath} in a {@link Area}.
 * This would correspond to a file in a file system.
 */
public interface Content
{
    /**
     * Gets a new clone of the content as a byte array.
     * A new copy of the byte array is created each time this method is called.
     * @return A new copy of the content byte array. If there is no content then you get an empty array.
     */
    byte[] cloneContentAsByteArray();

    /**
     * Gets the content as a byte array.
     * It is implied that the returned type is only meant for reading and should not be modified directly.
     * It will either access the efficient representation of the content if it has one or it will clone the content as the desired type each time this method is called.
     * @return This content as a byte array. If there is no content then you get an empty array.
     */
    default byte[] asByteArray()
    {
        // Check if this is already accessed most efficiently as a byte array:
        if (this instanceof ContentWithByteArray)
        {
            // This content is already accessed most efficiently as a byte array.
            return ((ContentWithByteArray)this).getEfficientByteArray();
        }
        else
        {
            // This is not already accessed most efficiently as the intended type.
            return cloneContentAsByteArray();
        }
    }

    /**
     * Gets a new clone of the content as a byte list.
     * A new copy of the list is created each time this method is called.
     * @return A new list of the content. If there is no content then you get an empty list.
     */
    List<Byte> cloneContentAsByteList();

    /**
     * Gets the content as a byte list.
     * It is implied that the returned type is only meant for reading and should not be modified directly.
     * It will either access the efficient representation of the content if it has one or it will clone the content as the desired type each time this method is called.
     * @return This content as a byte list. If there is no content then you get an empty list.
     */
    default List<Byte> asByteList()
    {
        // Check if this is already accessed most efficiently as a byte list:
        if (this instanceof ContentWithByteList)
        {
            // This content is already accessed most efficiently as a byte list.
            return ((ContentWithByteList)this).getEfficientByteList();
        }
        else
        {
            // This is not already accessed most efficiently as the intended type.
            return cloneContentAsByteList();
        }
    }

    /**
     * Gets a clone of the content as a byte buffer.
     * It is implied that the returned type is only meant for reading and should not be modified directly.
     * @return A new byte buffer for the content. If there is no content then you get a byte buffer with 0 capacity.
     */
    ByteBuffer cloneContentAsByteBuffer();

    /**
     * Gets the content as a byte buffer.
     * It will either access the efficient representation of the content if it has one or it will clone the content as the desired type each time this method is called.
     * @return This content as a byte buffer. If there is no content then you get a byte buffer with 0 capacity.
     */
    default ByteBuffer asByteBuffer()
    {
        // Check if this is already accessed most efficiently as a byte buffer:
        if (this instanceof ContentWithByteBuffer)
        {
            // This content is already accessed most efficiently as a byte buffer.
            return ((ContentWithByteBuffer)this).getEfficientByteBuffer();
        }
        else
        {
            // This is not already accessed most efficiently as the intended type.
            return cloneContentAsByteBuffer();
        }
    }

    /**
     * Gets a clone of the content as a byte stream.
     * @return A new byte buffer for the content. If there is no content then you get an empty stream.
     */
    Stream<Byte> cloneContentAsByteStream();

    /**
     * Gets the content as a byte stream.
     * It is implied that the returned type is only meant for reading and should not be modified directly.
     * It will either access the efficient representation of the content if it has one or it will clone the content as the desired type each time this method is called.
     * @return This content as a byte stream. If there is no content then you get an empty stream.
     */
    default Stream<Byte> asByteStream()
    {
        // Check if this is already accessed most efficiently as a byte stream:
        if (this instanceof ContentWithByteStream)
        {
            // This content is already accessed most efficiently as a byte stream.
            return ((ContentWithByteStream)this).getEfficientByteStream();
        }
        else
        {
            // This is not already accessed most efficiently as the intended type.
            return cloneContentAsByteStream();
        }
    }
}
