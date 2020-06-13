package io.nanovc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the base path functionality.
 */
public class PathBaseTests extends NanoVersionControlTestsBase
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
    public void test_isAbsolute()
    {
        // Null path:
        assertFalse(PathBase.isAbsolute(null));

        // Absolute Paths:
        assertTrue(PathBase.isAbsolute("/"));
        assertTrue(PathBase.isAbsolute("/ "));
        assertTrue(PathBase.isAbsolute("/a"));
        assertTrue(PathBase.isAbsolute("/a/b"));

        // Relative Paths:
        assertFalse(PathBase.isAbsolute(""));
        assertFalse(PathBase.isAbsolute(" /"));
    }

    /**
     * Tests that we the ending delimiter correctly.
     */
    @Test
    public void test_hasEndingDelimiter()
    {
        // Null Path:
        assertFalse(PathBase.hasEndingDelimiter(null));

        // Absolute Paths:
        assertTrue(PathBase.hasEndingDelimiter("/"));
        assertFalse(PathBase.hasEndingDelimiter("/ "));
        assertFalse(PathBase.hasEndingDelimiter("/a"));
        assertTrue(PathBase.hasEndingDelimiter("/a/"));
        assertFalse(PathBase.hasEndingDelimiter("/a/b"));
        assertTrue(PathBase.hasEndingDelimiter("/a/b/"));

        // Relative Paths:
        assertFalse(PathBase.hasEndingDelimiter(""));
        assertTrue(PathBase.hasEndingDelimiter(" /"));
    }
}
