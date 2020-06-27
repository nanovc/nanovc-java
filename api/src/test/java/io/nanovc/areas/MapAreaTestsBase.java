package io.nanovc.areas;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;
import io.nanovc.RepoPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base tests for various implementations of Map Content Areas.
 * These are common tests that we expect all map implementations to pass.
 */
public abstract class MapAreaTestsBase<TContent extends ContentAPI, TMapArea extends AreaAPI<TContent> & Map<String, TContent>>
{
    /**
     * This is the map area being tested.
     */
    public TMapArea area;

    /**
     * This creates the map area that is being tested before each test runs.
     */
    @BeforeEach
    public void createMapAreaBeforeEachTest()
    {
        this.area = createMapAreaUnderTest();
    }

    /**
     * Creates the map area being tested.
     * @return A new instance of the map area under test.
     */
    protected abstract TMapArea createMapAreaUnderTest();

    /**
     * Creates the specific type of content for the given value.
     * @param content Creates the specific type of content that we want to put into the content area.
     * @return The specific type of content for the given value.
     */
    protected abstract TContent createContent(String content);

    /**
     * Reads the value of the content.
     * @param content The content to read out of.
     * @return The string value of the content.
     */
    protected abstract String readContent(TContent content);

    /**
     * Tests the API for the {@link MapArea}.
     */
    @Test
    public void testAPI()
    {
        // Make sure the area is empty to begin with:
        assertFalse(area.hasAnyContent());
        assertEquals(0, area.size());

        String expected;

        // Make sure that the structure looks as expected:
        expected = "";
        assertEquals(expected, area.asListString());

        // Put some content:
        area.putContent("/", createContent("Hello World"));
        assertTrue(area.hasAnyContent());
        assertEquals(1, area.size());
        assertTrue(area.hasContent("/"));
        assertTrue(area.hasContent(RepoPath.atRoot()));
        assertFalse(area.hasContent(""));
        assertFalse(area.hasContent("/BAD"));

        // Make sure that the structure looks as expected:
        expected = "/ : " + createContent("Hello World").toString();
        assertEquals(expected, area.asListString());

        // Add some more content:
        area.putContent(RepoPath.atRoot().resolve("content").resolve("detail"), createContent("Detail"));
        assertTrue(area.hasAnyContent());
        assertEquals(2, area.size());

        // Make sure that the structure looks as expected:
        expected = "/ : " + createContent("Hello World").toString()+ "\n" +
                   "/content/detail : " + createContent("Detail").toString();
        assertEquals(expected, area.asListString());

        // Remove the root content:
        area.removeContent(RepoPath.atRoot());

        // Make sure that the structure looks as expected:
        expected = "/content/detail : " + createContent("Detail").toString();
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
    public void testGettingContentWithRepoPaths()
    {
        // Add some content:
        area.putContent(RepoPath.atRoot(), createContent("Root"));
        area.putContent(RepoPath.atRoot().resolve("A"), createContent("A1"));

        // Make sure we can get the content:
        assertEquals("Root", readContent(area.getContent(RepoPath.atRoot())));
        assertEquals("Root", readContent(area.getContent("/")));

        assertEquals("A1", readContent(area.getContent(RepoPath.atRoot().resolve("A"))));
        assertEquals("A1", readContent(area.getContent("/A")));
    }
}
