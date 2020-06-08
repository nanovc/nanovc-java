package io.nanovc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the base pattern functionality.
 */
public class PatternBaseTests extends NanoVersionControlTestsBase
{
    /**
     * Tests that the delimiter is as expected.
     */
    @Test
    public void testPathDelimiter()
    {
        assertEquals("/", PathBase.DELIMITER);
    }

    /**
     * Tests that we detect absolute paths correctly.
     */
    @Test
    public void test_createRegex()
    {
        assertEquals("", PatternBase.createRegex("").pattern());

        assertEquals("/a", PatternBase.createRegex("a").pattern());
        assertEquals("/[^\\/]*", PatternBase.createRegex("*").pattern());
        assertEquals("/.*", PatternBase.createRegex("**").pattern());
        assertEquals("/a[^\\/]*", PatternBase.createRegex("a*").pattern());
        assertEquals("/a.*", PatternBase.createRegex("a**").pattern());
        assertEquals("/[^\\/]*a", PatternBase.createRegex("*a").pattern());
        assertEquals("/.*a", PatternBase.createRegex("**a").pattern());

        assertEquals("/a/", PatternBase.createRegex("a/").pattern());
        assertEquals("/a/[^\\/]*", PatternBase.createRegex("a/*").pattern());
        assertEquals("/a/.*", PatternBase.createRegex("a/**").pattern());
        assertEquals("/[^\\/]*/.*", PatternBase.createRegex("*/**").pattern());
        assertEquals("/.*/.*", PatternBase.createRegex("**/**").pattern());
        assertEquals("/.*[^\\/]*", PatternBase.createRegex("***").pattern());
        assertEquals("/.*.*", PatternBase.createRegex("****").pattern());

        assertEquals("/a/", PatternBase.createRegex("/a/").pattern());
        assertEquals("/a/[^\\/]*", PatternBase.createRegex("/a/*").pattern());
        assertEquals("/a/.*", PatternBase.createRegex("/a/**").pattern());
        assertEquals("/[^\\/]*/.*", PatternBase.createRegex("/*/**").pattern());
        assertEquals("/.*/.*", PatternBase.createRegex("/**/**").pattern());
        assertEquals("/.*[^\\/]*", PatternBase.createRegex("/***").pattern());
        assertEquals("/.*.*", PatternBase.createRegex("/****").pattern());

        assertEquals("/a\\.txt", PatternBase.createRegex("a.txt").pattern());
        assertEquals("/a\\.txt", PatternBase.createRegex("/a.txt").pattern());
        assertEquals("/a\\.txt/", PatternBase.createRegex("a.txt/").pattern());
        assertEquals("/[^\\/]*/a\\.txt", PatternBase.createRegex("*/a.txt").pattern());
        assertEquals("/.*/a\\.txt", PatternBase.createRegex("**/a.txt").pattern());
        assertEquals("/.*/.*\\.txt", PatternBase.createRegex("**/**.txt").pattern());
        assertEquals("/.*/[^\\/]*\\.txt", PatternBase.createRegex("**/*.txt").pattern());
    }
}
