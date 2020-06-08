package io.nanovc.content;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link StringContent}.
 */
public class StringContentTests
{
    @Test
    public void creationTest()
    {
        new StringContent("");
        new StringContent("", StandardCharsets.UTF_8);
        new StringContent(new byte[0]);
        new StringContent(new byte[0], StandardCharsets.UTF_8);
    }

    @Test
    public void defaultEncodingTests()
    {
        assertStringEncoding(null, StringContent::new, new byte[0], StringContent::new, "");
        assertStringEncoding("", StringContent::new, new byte[0], StringContent::new, "");
        assertStringEncoding("A", StringContent::new, new byte[] { 65 }, StringContent::new, "A");
    }

    @Test
    public void utf8EncodingTests()
    {
        assertStringEncoding(null, s -> new StringContent(s, StandardCharsets.UTF_8) , new byte[0], bytes -> new StringContent(bytes, StandardCharsets.UTF_8), "");
        assertStringEncoding("", s -> new StringContent(s, StandardCharsets.UTF_8) , new byte[0], bytes -> new StringContent(bytes, StandardCharsets.UTF_8), "");
        assertStringEncoding("A", s -> new StringContent(s, StandardCharsets.UTF_8) , new byte[] { 65 }, bytes -> new StringContent(bytes, StandardCharsets.UTF_8), "A");
    }

    /**
     * Makes sure that the given string content serializes as expected.
     *
     * @param content           The string content to test.
     * @param stringConstructor The constructor to call using a string for making the string content that we want to test.
     * @param expectedBytes     The bytes that we expect to get.
     * @param byteConstructor   The constructor to call using a byte array for making the string content that we want to test.
     * @param expectedContent   The expected content to get out. This might be different to the input when the input is null.
     */
    public void assertStringEncoding(
        String content,
        Function<String, StringContent> stringConstructor,
        byte[] expectedBytes,
        Function<byte[], StringContent> byteConstructor,
        String expectedContent
        )
    {
        // Call the string constructor:
        StringContent stringContent = stringConstructor.apply(content);

        // Make sure the value is as expected:
        assertEquals(content, stringContent.value);

        // Get the encoding:
        byte[] bytes = stringContent.asByteArray();

        // Make sure it matches the expected value:
        assertArrayEquals(expectedBytes, bytes);

        // Call the byte constructor:
        StringContent stringContentFromBytes = byteConstructor.apply(bytes);

        // Make sure that the instances are different:
        assertNotSame(stringContent, stringContentFromBytes);

        // Make sure that the value is as expected:
        assertEquals(expectedContent, stringContentFromBytes.value);

        // Get the bytes again:
        byte[] bytes2 = stringContentFromBytes.asByteArray();

        // Make sure that they are not the same arrays because we don't expect that for strings:
        assertNotSame(bytes, bytes2);
    }
}
