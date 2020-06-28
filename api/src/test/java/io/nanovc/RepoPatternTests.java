package io.nanovc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests repository patterns.
 * Patterns are used to define a range of paths in a {@link RepoAPI}.
 */
public class RepoPatternTests extends NanoVersionControlTestsBase
{
    /**
     * Tests creation of repository patterns.
     */
    @Test
    public void PatternCreation()
    {
        RepoPattern repoPattern = RepoPattern.matching("**/*.json");
        assertNotNull(repoPattern);
    }

    /**
     * Tests matching of repo patterns against content.
     */
    @Test
    public void PatternMatching()
    {
        // Create the content:
        List<AreaEntry<ContentAPI>> content = new ArrayList<>();
        content.add(new AreaEntry<>(RepoPath.at("/"), null));
        content.add(new AreaEntry<>(RepoPath.at("/a"), null));
        content.add(new AreaEntry<>(RepoPath.at("/a/1.json"), null));
        content.add(new AreaEntry<>(RepoPath.at("/a/2.json"), null));
        content.add(new AreaEntry<>(RepoPath.at("/a/b/3.json"), null));
        content.add(new AreaEntry<>(RepoPath.at("/4.json"), null));
        content.add(new AreaEntry<>(RepoPath.at("/5.json"), null));

        assertContentMatches(content, "/", "/");
        assertContentMatches(content, "*", "/,/a,/4.json,/5.json");
        assertContentMatches(content, "/*", "/,/a,/4.json,/5.json");
        assertContentMatches(content, "**", "/,/a,/a/1.json,/a/2.json,/a/b/3.json,/4.json,/5.json");
        assertContentMatches(content, "/**", "/,/a,/a/1.json,/a/2.json,/a/b/3.json,/4.json,/5.json");
        assertContentMatches(content, "*.json", "/4.json,/5.json");
        assertContentMatches(content, "/*.json", "/4.json,/5.json");
        assertContentMatches(content, "**.json", "/a/1.json,/a/2.json,/a/b/3.json,/4.json,/5.json");
        assertContentMatches(content, "**/*.json", "/a/1.json,/a/2.json,/a/b/3.json");
        assertContentMatches(content, "**/**.json", "/a/1.json,/a/2.json,/a/b/3.json");
        assertContentMatches(content, "/**/*.json", "/a/1.json,/a/2.json,/a/b/3.json");
        assertContentMatches(content, "/a/*.json", "/a/1.json,/a/2.json");
        assertContentMatches(content, "/a/**.json", "/a/1.json,/a/2.json,/a/b/3.json");
        assertContentMatches(content, "/a/b/*.json", "/a/b/3.json");
        assertContentMatches(content, "/*/b/*.json", "/a/b/3.json");
        assertContentMatches(content, "/**/b/*.json", "/a/b/3.json");
        assertContentMatches(content, "**/b/*.json", "/a/b/3.json");
        assertContentMatches(content, "**3.json", "/a/b/3.json");
        assertContentMatches(content, "**b**", "/a/b/3.json");
    }

    /**
     * Tests that the given repo pattern matches the content.
     * @param content The content to match.
     * @param globPattern The repo pattern to test.
     * @param expectedMatch The comma separated paths of the expected matches.
     */
    private void assertContentMatches(List<AreaEntry<ContentAPI>> content, String globPattern, String expectedMatch)
    {
        // Create the pattern:
        RepoPattern repoPattern = RepoPattern.matching(globPattern);

        // Get the matches:
        List<AreaEntry<ContentAPI>> matches = repoPattern.match(content);

        // Make sure the matches are as expected:
        assertEquals(expectedMatch, matches.stream().map(areaEntry -> areaEntry.path.toAbsolutePath().path).collect(Collectors.joining(",")));
    }


}
