package io.nanovc.content;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ByteArrayContent}.
 */
public class ByteArrayContentTests
{
    @Test
    public void creationTest()
    {
        new ByteArrayContent(new byte[0]);
    }

    @Test
    public void valueTests()
    {
        assertByteArrayContent(null, ByteArrayContent::new);
        assertByteArrayContent(new byte[0], ByteArrayContent::new);
        assertByteArrayContent(new byte[] { 65 }, ByteArrayContent::new);
    }


    /**
     * Makes sure that the given byte array content serializes as expected.
     *
     * @param content           The byte array content to test.
     * @param byteConstructor   The constructor to call using a byte array for making the string content that we want to test.
     */
    public void assertByteArrayContent(
        byte[] content,
        Function<byte[], ByteArrayContent> byteConstructor
        )
    {
        // Call the byte constructor:
        ByteArrayContent byteArrayContent = byteConstructor.apply(content);

        // Make sure that the value is as expected:
        assertArrayEquals(content, byteArrayContent.bytes);
        assertSame(content, byteArrayContent.bytes);

        // Get the bytes:
        byte[] bytes = byteArrayContent.asByteArray();

        // Make sure that they are the same arrays (for performance) if the input was not null:
        if (content != null)
        {
            // The content was not null.
            assertSame(content, bytes);
        }
        else
        {
            // The content was null.

            // Make sure that we got a zero length array:
            assertNotNull(bytes);
            assertEquals(0, bytes.length);
        }
    }
}
