package io.nanovc.content;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link EncodedStringContent}.
 */
public class EncodedStringContentTests
{
    @Test
    public void creationTest()
    {
        new EncodedStringContent("", StandardCharsets.ISO_8859_1);
        new EncodedStringContent(new byte[0], StandardCharsets.ISO_8859_1);
    }

    @Test
    public void latin1EncodingTests()
    {
        assertStringEncoding(null, StandardCharsets.ISO_8859_1, EncodedStringContent::new, new byte[0], EncodedStringContent::new, "");
        assertStringEncoding("", StandardCharsets.ISO_8859_1, EncodedStringContent::new, new byte[0], EncodedStringContent::new, "");
        assertStringEncoding("A", StandardCharsets.ISO_8859_1, EncodedStringContent::new, new byte[] { 65 }, EncodedStringContent::new, "A");
    }

    @Test
    public void utf8EncodingTests()
    {
        assertStringEncoding(null, StandardCharsets.UTF_8, EncodedStringContent::new, new byte[0], EncodedStringContent::new, "");
        assertStringEncoding("", StandardCharsets.UTF_8, EncodedStringContent::new, new byte[0], EncodedStringContent::new, "");
        assertStringEncoding("A", StandardCharsets.UTF_8, EncodedStringContent::new, new byte[] { 65 }, EncodedStringContent::new, "A");
    }

    @Test
    public void utf16EncodingTests()
    {
        assertStringEncoding(null, StandardCharsets.UTF_16, EncodedStringContent::new, new byte[0], EncodedStringContent::new, "");
        assertStringEncoding("", StandardCharsets.UTF_16, EncodedStringContent::new, new byte[0], EncodedStringContent::new, "");
        assertStringEncoding("A", StandardCharsets.UTF_16, EncodedStringContent::new, new byte[] { -2, -1, 0, 65 }, EncodedStringContent::new, "A");
    }

    /**
     * Makes sure that the given string content serializes as expected.
     *
     * @param content           The string content to test.
     * @param charset           The character set to use for the encoding.
     * @param stringConstructor The constructor to call using a string for making the string content that we want to test.
     * @param expectedBytes     The bytes that we expect to get.
     * @param byteConstructor   The constructor to call using a byte array for making the string content that we want to test.
     * @param expectedContent   The expected content to get out. This might be different to the input when the input is null.
     */
    public void assertStringEncoding(
        String content,
        Charset charset,
        BiFunction<String, Charset, EncodedStringContent> stringConstructor,
        byte[] expectedBytes,
        BiFunction<byte[], Charset, EncodedStringContent> byteConstructor,
        String expectedContent
        )
    {
        // Call the string constructor:
        EncodedStringContent EncodedStringContent = stringConstructor.apply(content, charset);

        // Make sure the value is as expected:
        assertEquals(content, EncodedStringContent.value);

        // Get the encoding:
        byte[] bytes = EncodedStringContent.asByteArray();

        // Make sure it matches the expected value:
        assertArrayEquals(expectedBytes, bytes);

        // Call the byte constructor:
        EncodedStringContent EncodedStringContentFromBytes = byteConstructor.apply(bytes, charset);

        // Make sure that the instances are different:
        assertNotSame(EncodedStringContent, EncodedStringContentFromBytes);

        // Make sure that the value is as expected:
        assertEquals(expectedContent, EncodedStringContentFromBytes.value);

        // Get the bytes again:
        byte[] bytes2 = EncodedStringContentFromBytes.asByteArray();

        // Make sure that they are not the same arrays because we don't expect that for strings:
        assertNotSame(bytes, bytes2);
    }
}
