package io.nanovc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests repository paths.
 */
public class RepoPathTests extends NanoVersionControlTestsBase
{

    /**
     * Tests creation of repository paths.
     */
    @Test
    public void PathCreation()
    {
        RepoPath repoPath = new RepoPath("/");
        assertEquals("/", repoPath.path);
    }

    /**
     * Tests that we can resolve one path from another.
     */
    @Test
    public void ResolvingPaths()
    {
        RepoPath rootPath = new RepoPath("/");
        RepoPath pathR1 = rootPath.resolve("R1");
        assertEquals("/R1", pathR1.path);

        RepoPath pathR1S1 = pathR1.resolve("S1");
        assertEquals("/R1/S1", pathR1S1.path);

        RepoPath pathR2 = pathR1.resolve("/R2");
        assertEquals("/R2", pathR2.path);

        RepoPath pathRelativeEmpty = new RepoPath("");
        assertEquals("", pathRelativeEmpty.path);
        assertEquals("/", pathRelativeEmpty.toAbsolutePath().path);
    }

    /**
     * Tests that we can convert paths to strings.
     */
    @Test
    public void PathsToString()
    {
        RepoPath rootPath = new RepoPath("/");
        assertEquals("/", rootPath.path);
        assertEquals("/", rootPath.toString());
        assertEquals("/", rootPath.toAbsolutePath().toString());

        RepoPath pathR1 = rootPath.resolve("R1");
        assertEquals("/R1", pathR1.path);
        assertEquals("/R1", pathR1.toString());
        assertEquals("/R1", pathR1.toAbsolutePath().toString());

        RepoPath pathR1S1 = pathR1.resolve("S1");
        assertEquals("/R1/S1", pathR1S1.path);
        assertEquals("/R1/S1", pathR1S1.toString());
        assertEquals("/R1/S1", pathR1S1.toAbsolutePath().toString());

        RepoPath pathR2 = pathR1.resolve("/R2");
        assertEquals("/R2", pathR2.path);
        assertEquals("/R2", pathR2.toString());
        assertEquals("/R2", pathR2.toAbsolutePath().toString());
    }

    /**
     * Tests that we are able to keep split paths and string paths intact when resolving paths for performance reasons.
     * This is useful because it means that we can avoid making unnecessarily large strings all the time.
     */
    @Test
    public void TestStringReuseInPaths()
    {
        final String ROOTPATHSTRING = "/";
        final String R1STRING = "R1";
        final String R1_ABSOLUTE_STRING = "/R1";

        RepoPath rootPath = new RepoPath(ROOTPATHSTRING);
        RepoPath r1 = rootPath.resolve(R1STRING);

        assertNotSame(rootPath, r1);
        assertSame(ROOTPATHSTRING, rootPath.path); // Yes, we are checking the physical string object in memory.
        assertNotSame(R1STRING, r1.path); // Yes, we are checking the physical string object in memory.
        assertNotSame(R1_ABSOLUTE_STRING, r1.path); // NOTE: These are not the same because the current path resolution creates new string objects while it is concatenating relative paths.
        assertEquals(R1_ABSOLUTE_STRING, r1.path); // NOTE: Although the string objects are different, the values are the same, as expected.
    }

    /**
     * Tests the reuse of paths when we have lots of them
     * */
    @Test
    public void TestStringReuseInPaths_1_000_000()
    {
        // Define the number of paths that we want:
        final int COUNT = 1_000_000;
        final int SUB_PATH_COUNT = 10;

        final String ROOTPATHSTRING = "/";

        List<RepoPath> paths = new ArrayList<>();

        RepoPath rootPath = new RepoPath(ROOTPATHSTRING);

        // Create many paths:
        for (int i = 0; i < COUNT; i++)
        {
            // Start at the root path:
            RepoPath path = rootPath;

            // Create the sub paths:
            for (int j = 0; j < SUB_PATH_COUNT; j++)
            {
                // Create the sub path to resolve:
                String subPathString = Integer.toString(i) + Integer.toString(j);
                path = path.resolve(subPathString);
            }

            // Add the path to the list:
            paths.add(path);
        }

        assertEquals(COUNT, paths.size());
    }
}
