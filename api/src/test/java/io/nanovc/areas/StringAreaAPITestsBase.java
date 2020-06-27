package io.nanovc.areas;

import io.nanovc.RepoPath;
import io.nanovc.content.StringContent;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base tests for various implementations of Map Content Areas that store {@link StringContent}.
 * These are common tests that we expect all map implementations to pass.
 */
public abstract class StringAreaAPITestsBase<TMapArea extends StringAreaAPI & Map<String, StringContent>>
    extends StringMapAreaTestsBase<TMapArea>
{
    /**
     * Tests the API for the {@link StringHashMapArea}.
     */
    @Test
    public void testStringAPI()
    {
        // Make sure the area is empty to begin with:
        assertFalse(area.hasAnyContent());
        assertEquals(0, area.size());

        String expected;

        // Make sure that the structure looks as expected:
        expected = "";
        assertEquals(expected, area.asListString());

        // Put some content:
        area.putString("/", "Hello World");
        assertTrue(area.hasAnyContent());
        assertEquals(1, area.size());
        assertTrue(area.hasContent("/"));
        assertTrue(area.hasString("/"));
        assertTrue(area.hasString(RepoPath.atRoot()));
        assertFalse(area.hasContent(""));
        assertFalse(area.hasContent("/BAD"));

        // Make sure that the structure looks as expected:
        expected = "/ : 'Hello World'";
        assertEquals(expected, area.asListString());

        // Add some more content:
        area.putString(RepoPath.atRoot().resolve("content").resolve("detail"), "Detail");
        assertTrue(area.hasAnyContent());
        assertEquals(2, area.size());

        // Make sure that the structure looks as expected:
        expected = "/ : 'Hello World'\n" +
                   "/content/detail : 'Detail'";
        assertEquals(expected, area.asListString());

        // Remove the root content:
        area.removeString(RepoPath.atRoot());

        // Make sure that the structure looks as expected:
        expected = "/content/detail : 'Detail'";
        assertEquals(expected, area.asListString());

        // Remove all the content:
        area.clear();

        // Make sure that the structure looks as expected:
        expected = "";
        assertEquals(expected, area.asListString());

    }

    /**
     * Tests that we can get content from the area using repo paths.
     */
    @Test
    public void testGettingContentWithRepoPathsAndStringAPI()
    {
        // Add some content:
        area.putString(RepoPath.atRoot(), "Root");
        area.putString(RepoPath.atRoot().resolve("A"), "A1");

        // Make sure we can get the content:
        assertEquals("Root", area.getString(RepoPath.atRoot()));
        assertEquals("Root", area.getString("/"));

        assertEquals("A1", area.getString(RepoPath.atRoot().resolve("A")));
        assertEquals("A1", area.getString("/A"));
    }
}
