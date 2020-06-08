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
 * A class with helper functions for bit manipulations and conversions.
 */
public class Bits
{
    /**
     * Converts 1 byte of an array to an integer.
     * It implies that the 3 missing bytes are zero.
     * The lowest byte index has the most significant bits in the returned integer. This corresponds to {@link java.nio.ByteOrder}.BIG_ENDIAN.
     * byte[] { 0b00000001 } = 0b00000001_00000000_00000000_00000000 = 16_777_216
     * Java seems more naturally performance with an int in tight loops rather than a long. https://stackoverflow.com/questions/19844048/why-is-long-slower-than-int-in-x64-java
     * @param oneByte The array that is expected to have exactly 1 byte in it. The 3 missing least significant bytes are assumed to be zero.
     * @return The integer for this byte array.
     */
    public static int byte1ToInt(byte[] oneByte)
    {
        // Unroll the loop directly:
        // https://stackoverflow.com/a/27610608/231860
        return (oneByte[0] << 24);
    }

    /**
     * Converts 2 bytes of an array to an integer.
     * It implies that the 2 missing bytes are zero.
     * The lowest byte index has the most significant bits in the returned integer. This corresponds to {@link java.nio.ByteOrder}.BIG_ENDIAN.
     * byte[] { 0b00000001, 0b00000010 } = 0b00000001_00000010_0000_0000_0000_0000 = 16_908_288
     * Java seems more naturally performance with an int in tight loops rather than a long. https://stackoverflow.com/questions/19844048/why-is-long-slower-than-int-in-x64-java
     * @param twoBytes The array that is expected to have exactly 2 bytes in it. The 2 missing least significant bytes are assumed to be zero.
     * @return The integer for this byte array.
     */
    public static int byte2ToInt(byte[] twoBytes)
    {
        // Unroll the loop directly:
        // https://stackoverflow.com/a/27610608/231860
        return (twoBytes[0] << 24) | (twoBytes[1] << 16);
    }

    /**
     * Converts 3 bytes of an array to an integer.
     * It implies that the 1 missing least significant byte is zero.
     * The lowest byte index has the most significant bits in the returned integer. This corresponds to {@link java.nio.ByteOrder}.BIG_ENDIAN.
     * byte[] { 0b00000001, 0b00000010, 0b00000100 } = 0b00000001_00000010_00000100_0000_0000 = 16_909_312
     * Java seems more naturally performance with an int in tight loops rather than a long. https://stackoverflow.com/questions/19844048/why-is-long-slower-than-int-in-x64-java
     * @param threeBytes The array that is expected to have exactly 3 bytes in it. The 1 missing least significant byte is assumed to be zero.
     * @return The integer for this byte array.
     */
    public static int byte3ToInt(byte[] threeBytes)
    {
        // Unroll the loop directly:
        // https://stackoverflow.com/a/27610608/231860
        return (threeBytes[0] << 24) | (threeBytes[1] << 16) | (threeBytes[2] << 8);
    }


    /**
     * Converts 4 bytes of an array to an integer.
     * The lowest byte index has the most significant bits in the returned integer. This corresponds to {@link java.nio.ByteOrder}.BIG_ENDIAN.
     * byte[] { 0b00000001, 0b00000010, 0b00000100, 0b00001000 } = 0b00000001_00000010_00000100_0000_1000 = 16_909_320
     * Java seems more naturally performance with an int in tight loops rather than a long. https://stackoverflow.com/questions/19844048/why-is-long-slower-than-int-in-x64-java
     * @param fourBytes The array that is expected to have exactly 4 bytes in it.
     * @return The integer for this byte array.
     */
    public static int byte4ToInt(byte[] fourBytes)
    {
        // Unroll the loop directly:
        // https://stackoverflow.com/a/27610608/231860
        return (fourBytes[0]<<24) | (fourBytes[1]<<16) | (fourBytes[2]<<8) | (fourBytes[3]);
    }

    /**
     * Converts 4 bytes of an array to an integer starting at the given index.
     * The lowest byte index has the most significant bits in the returned integer. This corresponds to {@link java.nio.ByteOrder}.BIG_ENDIAN.
     * byte[] { 0b00000001, 0b00000010, 0b00000100, 0b00001000 } = 0b00000001_00000010_00000100_0000_1000 = 16_909_320
     * Java seems more naturally performance with an int in tight loops rather than a long. https://stackoverflow.com/questions/19844048/why-is-long-slower-than-int-in-x64-java
     * @param bytes The array that we want to extract an integer from.
     * @param startIndex The index in the byte array to start from.
     * @param count The number of bytes to read from the start index. If the value is zero or negative, this returns 0. If it is between 1 and 3 then it treats the bytes as the most significant bytes and the missing bytes to make up the integer are assumed to be zero. If the count is four or more then it only converts the first 4 bytes to an integer.
     * @return The integer for this byte array.
     */
    public static int byteArrayToInt(byte[] bytes, int startIndex, int count)
    {
        // Make sure we have a count:
        if (count < 0) return 0;
        // Now we know we have a positive count.

        // Check how many bytes we want to get:
        switch (count)
        {
            case 1: return (bytes[startIndex] << 24); // 3 least significant bytes are zero.
            case 2: return (bytes[startIndex] << 24) | (bytes[startIndex + 1] << 16); // 2 least significant bytes are zero.
            case 3: return (bytes[startIndex] << 24) | (bytes[startIndex + 1] << 16) | (bytes[startIndex + 2] << 8); // 1 least significant byte is zero.
            default: return (bytes[startIndex] << 24) | (bytes[startIndex + 1] << 16) | (bytes[startIndex + 2] << 8) | (bytes[startIndex + 3]); // bytes after the 4th byte are ignored.
        }
    }
}
