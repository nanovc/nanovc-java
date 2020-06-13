/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.strings;

import io.nanovc.*;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.clocks.ClockWithVMNanos;
import io.nanovc.comparisons.HashMapComparisonHandler;
import io.nanovc.content.StringContent;
import io.nanovc.differences.HashMapDifferenceHandler;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import io.nanovc.memory.MemoryCommit;
import io.nanovc.memory.MemoryRepoHandlerTestBase;
import io.nanovc.memory.MemorySearchQuery;
import io.nanovc.memory.MemorySearchResults;
import io.nanovc.merges.LastWinsMergeHandler;
import io.nanovc.searches.commits.SimpleSearchQueryDefinition;
import io.nanovc.searches.commits.expressions.AllRepoCommitsExpression;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests common usage scenarios for the {@link StringMemoryRepoHandler}.
 */
public class StringMemoryRepoHandlerTests extends MemoryRepoHandlerTestBase<
    StringContent,
    StringHashMapArea,
    MemoryCommit,
    MemorySearchQuery,
    MemorySearchResults,
    StringMemoryRepo,
    StringMemoryRepoEngine,
    StringMemoryRepoHandler
    >
{

    /**
     * Creates the specific type of handler under test.
     *
     * @return A new instance of the handler under test.
     */
    @Override
    protected StringMemoryRepoHandler createNewRepoHandler()
    {
        return new StringMemoryRepoHandler();
    }

    /**
     * Tests the constructor API.
     */
    @Test
    public void testConstructorAPI()
    {
        StringMemoryRepoHandler repoHandler;

        // Create the repo handler with each of the constructors:
        repoHandler = new StringMemoryRepoHandler();
        repoHandler = new StringMemoryRepoHandler(new StringMemoryRepo());
        repoHandler = new StringMemoryRepoHandler(new StringMemoryRepo(), new HashWrapperByteArrayIndex(), new ClockWithVMNanos(), new StringMemoryRepoEngine(), new HashMapDifferenceHandler(), new HashMapComparisonHandler(), new LastWinsMergeHandler());
    }

    /**
     * This tests that we can commit multiple commits in a branch and that the commits track their parents.
     */
    @Test
    public void testMultipleCommitsInBranch()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Hello World");

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commitToBranch(area, "master", "Commit 1");
        assertNotNull(commit1);

        // Make a change:
        area.putString("/A", "A");

        // Commit the changes:
        MemoryCommit commit2 = repoHandler.commitToBranch(area, "master", "Commit 2");
        assertNotNull(commit2);
        assertNotSame(commit1, commit2);

        // Make sure that the second commit has the first commit as a parent:
        assertSame(commit1, commit2.firstParent, "The second commit should have the first commit as a parent.");
    }

    /**
     * This tests that we can create a new branch from an existing commit.
     */
    @Test
    public void testCreateBranchFromExistingCommit()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Hello World");

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commitToBranch(area, "master", "Commit 1");
        assertNotNull(commit1);

        // Make a change:
        area.putString("/A", "A");

        // Create a new branch at the existing commit:
        repoHandler.createBranchAtCommit(commit1, "feature");

        // Make sure that the repo has two branches:
        assertEquals(2, repoHandler.getRepo().getBranchTips().size());
        assertEquals(2, repoHandler.getBranchNames().size());
        assertTrue(repoHandler.getBranchNames().containsAll(Arrays.asList("master", "feature")));

        // Make sure that both branches point at the same commit:
        assertEquals(commit1, repoHandler.getRepo().getBranchTips().get("master"));
        assertEquals(commit1, repoHandler.getRepo().getBranchTips().get("feature"));
        assertEquals(commit1, repoHandler.getLatestCommitForBranch("master"));
        assertEquals(commit1, repoHandler.getLatestCommitForBranch("feature"));
    }

    /**
     * This tests that we can commit a string.
     */
    @Test
    public void testCommitString()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Hello World");

        // Make sure the content area is as expected:
        assertEquals("/ : 'Hello World'", area.asListString());

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commit(area, "Initial Structure");
        assertNotNull(commit1);

        // Checkout the content:
        StringHashMapArea checkout1 = repoHandler.checkout(commit1);

        // Make sure the content areas are different instances:
        assertNotSame(area, checkout1);

        // Make sure the content area is as expected:
        assertEquals("/ : 'Hello World'", checkout1.asListString());

        // Add more content:
        area.putString("/greeting", "Hello Again");
        area.putString("/greeting/count", "2");

        // Make sure the content area is as expected:
        assertEquals("/ : 'Hello World'\n" +
                            "/greeting : 'Hello Again'\n" +
                            "/greeting/count : '2'", area.asListString());

        // Commit the changes:
        MemoryCommit commit2 = repoHandler.commit(area, "Added a greeting, times 2");
        assertNotNull(commit2);
        assertNotSame(commit1, commit2);

        // Checkout the second commit:
        StringHashMapArea checkout2 = repoHandler.checkout(commit2);

        // Make sure the content areas are different instances:
        assertNotSame(area, checkout2);
        assertNotSame(checkout1, checkout2);

        // Make sure the content area is as expected:
        assertEquals("/ : 'Hello World'\n" +
                            "/greeting : 'Hello Again'\n" +
                            "/greeting/count : '2'", checkout2.asListString());
    }

    /**
     * This tests that we can commit to various branches.
     */
    @Test
    public void testBranching()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Hello World");

        // Make sure the content area is as expected:
        assertEquals("/ : 'Hello World'", area.asListString());

        // Make sure that we don't have any branches before we start:
        assertEquals(0, repoHandler.getRepo().getBranchTips().size());
        assertFalse(repoHandler.getRepo().getBranchTips().containsKey("master"));
        assertFalse(repoHandler.getBranchNames().contains("master"));

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commitToBranch(area, "master", "Initial Structure");

        // Make sure that the repo has a branch:
        assertEquals(1, repoHandler.getRepo().getBranchTips().size());
        assertTrue(repoHandler.getRepo().getBranchTips().containsKey("master"));
        assertTrue(repoHandler.getBranchNames().contains("master"));

        // Make sure that the branch points at the commit that we created:
        assertSame(commit1, repoHandler.getLatestCommitForBranch("master"));
        assertSame(commit1, repoHandler.getRepo().getBranchTips().get("master"));

        // Checkout the content:
        StringHashMapArea checkout1 = repoHandler.checkout(commit1);

        // Make sure the content areas are different instances:
        assertNotSame(area, checkout1);

        // Make sure the content area is as expected:
        assertEquals("/ : 'Hello World'", checkout1.asListString());

        // Change the content:
        area.putString("/", "Hello Again");

        // Commit the changes:
        MemoryCommit commit2 = repoHandler.commitToBranch(area, "changed", "Made a change");

        // Make sure that the repo has the new branch:
        assertEquals(2, repoHandler.getRepo().getBranchTips().size());
        assertTrue(repoHandler.getRepo().getBranchTips().containsKey("changed"));
        assertTrue(repoHandler.getRepo().getBranchTips().containsKey("master"));
        assertTrue(repoHandler.getBranchNames().contains("changed"));
        assertTrue(repoHandler.getBranchNames().contains("master"));

        // Make sure that the branch points at the commit that we created:
        assertSame(commit2, repoHandler.getLatestCommitForBranch("changed"));
        assertSame(commit2, repoHandler.getRepo().getBranchTips().get("changed"));

        // Change the content:
        area.putString("/", "Hello Master");

        // Commit the changes:
        MemoryCommit commit3 = repoHandler.commitToBranch(area, "master", "Made a change to master");

        // Make sure that the repo has the branches we expect:
        assertEquals(2, repoHandler.getRepo().getBranchTips().size());
        assertTrue(repoHandler.getRepo().getBranchTips().containsKey("changed"));
        assertTrue(repoHandler.getRepo().getBranchTips().containsKey("master"));
        assertTrue(repoHandler.getBranchNames().contains("changed"));
        assertTrue(repoHandler.getBranchNames().contains("master"));

        // Make sure that the branch points at the commit that we created:
        assertSame(commit3, repoHandler.getLatestCommitForBranch("master"));
        assertSame(commit3, repoHandler.getRepo().getBranchTips().get("master"));

        // Make sure that the parentage of the commit is intact:
        assertSame(commit1, commit3.firstParent);
        assertNull(commit3.otherParents);
    }

    /**
     * This tests that we can create a new branch at a given commit.
     */
    @Test
    public void testCreatingBranchAtCommit()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Hello World");

        // Make sure that we don't have any branches before we start:
        assertEquals(0, repoHandler.getRepo().getBranchTips().size());
        assertFalse(repoHandler.getRepo().getBranchTips().containsKey("master"));
        assertFalse(repoHandler.getBranchNames().contains("master"));

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commitToBranch(area, "master", "Initial Structure");

        // Make sure that the repo has a branch:
        assertEquals(1, repoHandler.getRepo().getBranchTips().size());
        assertTrue(repoHandler.getRepo().getBranchTips().containsKey("master"));
        assertTrue(repoHandler.getBranchNames().contains("master"));

        // Make sure that the branch points at the commit that we created:
        assertSame(commit1, repoHandler.getLatestCommitForBranch("master"));
        assertSame(commit1, repoHandler.getRepo().getBranchTips().get("master"));

        // Create another named branch:
        repoHandler.createBranchAtCommit(commit1, "feature");
    }

    /**
     * This tests that we can tag commits and check them out again.
     */
    @Test
    public void testTagging()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Hello World");

        // Make sure that we don't have any tags before we start:
        assertEquals(0, repoHandler.getRepo().getTags().size());
        assertFalse(repoHandler.getRepo().getTags().containsKey("Tag 1"));
        assertFalse(repoHandler.getTagNames().contains("Tag 1"));

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commitToBranch(area, "master", "Initial Structure");

        // Make sure that we don't have any tags after we commit (because we haven't tagged yet):
        assertEquals(0, repoHandler.getRepo().getTags().size());
        assertFalse(repoHandler.getRepo().getTags().containsKey("Tag 1"));
        assertFalse(repoHandler.getTagNames().contains("Tag 1"));

        // Tag the commit in the repo:
        repoHandler.tagCommit(commit1, "Tag 1");

        // Make sure that we now have a tag in the repo:
        assertEquals(1, repoHandler.getRepo().getTags().size());
        assertTrue(repoHandler.getRepo().getTags().containsKey("Tag 1"));
        assertTrue(repoHandler.getTagNames().contains("Tag 1"));

        // Make sure that the tag points at the same commit instances that we created:
        assertSame(commit1, repoHandler.getRepo().getTags().get("Tag 1"));
        assertSame(commit1, repoHandler.getCommitForTag("Tag 1"));

        // Add another tag to the commit to confirm that we can have multiple tags per commit:
        repoHandler.tagCommit(commit1, "Tag 1.1");

        // Make sure that we now have both tags in the repo:
        assertEquals(2, repoHandler.getRepo().getTags().size());
        assertTrue(repoHandler.getRepo().getTags().containsKey("Tag 1"));
        assertTrue(repoHandler.getRepo().getTags().containsKey("Tag 1.1"));
        assertTrue(repoHandler.getTagNames().contains("Tag 1"));
        assertTrue(repoHandler.getTagNames().contains("Tag 1.1"));

        // Make sure that the tag points at the same commit instances that we created:
        assertSame(commit1, repoHandler.getRepo().getTags().get("Tag 1.1"));
        assertSame(commit1, repoHandler.getCommitForTag("Tag 1.1"));

        // Make sure we can remove tags from the repo:
        repoHandler.removeTag("Tag 1.1");

        // Make sure that we have only the one tag in the repo:
        assertEquals(1, repoHandler.getRepo().getTags().size());
        assertTrue(repoHandler.getRepo().getTags().containsKey("Tag 1"));
        assertTrue(repoHandler.getTagNames().contains("Tag 1"));

        // Create another commit so that we have more history in the repo:
        area.putString("/", "Hello World Again");
        MemoryCommit commit2 = repoHandler.commitToBranch(area, "master", "Great Changes");

        // Update an existing tag for the latest commit:
        repoHandler.tagCommit(commit2, "Tag 1");

        // Make sure that we still only have one tag in the repo:
        assertEquals(1, repoHandler.getRepo().getTags().size());
        assertTrue(repoHandler.getRepo().getTags().containsKey("Tag 1"));
        assertTrue(repoHandler.getTagNames().contains("Tag 1"));

        // Make sure that the updated tag points at the second commit:
        assertSame(commit2, repoHandler.getRepo().getTags().get("Tag 1"));
        assertSame(commit2, repoHandler.getCommitForTag("Tag 1"));
    }

    /**
     * Tests the performance of the string repo handler.
     */
    @Test
    public void performanceTest()
    {
        final int COUNT = 1_000_000;
        final int COMMITS = 10;

        long startNanos = System.nanoTime();

        for (int i = 0; i < COUNT; i++)
        {
            // Create the handler:
            StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

            // Create a new content area:
            StringHashMapArea area = repoHandler.createArea();

            MemoryCommit commit = null;
            for (int j = 0; j < COMMITS; j++)
            {
                // Add content to the area:
                area.putString("/", "Hello World " + i);

                // Commit the string:
                commit = repoHandler.commit(area, "Commit " + i);
            }

            // Checkout the last commit:
            StringHashMapArea checkout = repoHandler.checkout(commit);

            assertEquals(1, checkout.size());
        }

        long endNanos = System.nanoTime();
        long deltaNanos = endNanos - startNanos;

        System.out.printf("Duration = %,dms for %,d repos and %,d commits/repo = %,d repos/s = %,d commits/s", deltaNanos / 1_000_000, COUNT, COMMITS, COUNT * 1_000_000_000L / deltaNanos, COUNT * COMMITS * 1_000_000_000L / deltaNanos);

    }

    /**
     * Tests that the comparison API works as expected.
     */
    @Test
    public void testComparisonAPI()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Root");
        area.putString("/a", "A1");
        area.putString("/b", "B1");
        area.putString("/c", "c1");

        // Commit the area:
        MemoryCommit commit1 = repoHandler.commitToBranch(area, "master", "First commit");

        // Make some changes:
        area.putString(RepoPath.atRoot(), "New Root");
        area.putString("/a", "A2");
        // Leave /b the same.
        area.removeString("/c");

        // Commit the changes:
        MemoryCommit commit2 = repoHandler.commitToBranch(area, "master", "Second commit");


        // Compare the differences:
        Difference difference = repoHandler.computeDifferenceBetweenCommits(commit1, commit2);

        // Make sure the comparison is as expected:
        assertNotNull(difference);
        assertEquals(DifferenceState.CHANGED, difference.getDifference("/"));
        assertEquals(DifferenceState.CHANGED, difference.getDifference("/a"));
        assertNull(difference.getDifference("/b"));
        assertEquals(DifferenceState.DELETED, difference.getDifference("/c"));


        // Compare the changes:
        Comparison comparison = repoHandler.computeComparisonBetweenCommits(commit1, commit2);

        // Make sure the comparison is as expected:
        assertNotNull(comparison);
        assertEquals(ComparisonState.CHANGED, comparison.getComparison("/"));
        assertEquals(ComparisonState.CHANGED, comparison.getComparison("/a"));
        assertEquals(ComparisonState.UNCHANGED, comparison.getComparison("/b"));
        assertEquals(ComparisonState.DELETED, comparison.getComparison("/c"));
    }

    /**
     * Tests that the commit search API works as expected.
     */
    @Test
    public void testCommitSearchAPI()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Root");
        area.putString("/a", "A1");
        area.putString("/b", "B1");
        area.putString("/c", "c1");

        // Commit the area:
        MemoryCommit commit1 = repoHandler.commitToBranch(area, "master", "First commit");

        // Make some changes:
        area.putString(RepoPath.atRoot(), "New Root");
        area.putString("/a", "A2");
        // Leave /b the same.
        area.removeString("/c");

        // Commit the changes:
        MemoryCommit commit2 = repoHandler.commitToBranch(area, "master", "Second commit");

        // Construct the query that we are interested in:
        SearchQueryDefinition definition = SimpleSearchQueryDefinition.forSingleCommit(AllRepoCommitsExpression.allRepoCommits().tip());

        // Search for the last commit:
        MemorySearchResults searchResults = repoHandler.search(definition);
        assertNotNull(searchResults);

        // Make sure that the results have the search query that could be reused:
        assertNotNull(searchResults.getQuery());

        // Make sure that the correct commit was found:
        List<MemoryCommit> searchResultsCommits = searchResults.getCommits();
        assertEquals(1, searchResultsCommits.size());
        assertSame(commit2, searchResultsCommits.get(0));
    }

    /**
     * Tests that the merge API works as expected with no common parent between commits.
     * The master branch has the last commit in this scenario so the root content will be the same as the master branch.
     */
    @Test
    public void testMergeAPI_NoCommonParent_MasterHasLastCommit()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Root");
        area.putString("/a", "A1");
        area.putString("/b", "B1");
        area.putString("/c", "c1");

        // Commit the area to the master branch:
        MemoryCommit commit1 = repoHandler.commitToBranch(area, "master", "First commit");

        // Make some changes:
        area.putString(RepoPath.atRoot(), "New Root");
        area.putString("/a", "A2");
        // Leave /b the same.
        area.removeString("/c");
        // Add content at a new path:
        area.putString("/d", "D2");

        // Commit the changes to a disconnected branch (no common ancestor):
        MemoryCommit commit2 = repoHandler.commitToBranch(area, "disconnected", "Second commit");

        // Make another change to master:
        StringHashMapArea masterArea = repoHandler.checkout(commit1);

        // Change the content at an existing path that we also changed in the disconnected branch:
        masterArea.putString("/a", "A3");

        // Add content at a new path:
        masterArea.putString("/e", "E3");

        // Commit the changes to master (after making the disconnected changes):
        MemoryCommit commit3 = repoHandler.commitToBranch(masterArea, "master", "Third Commit");

        // Merge the disconnected branch back into the master branch:
        MemoryCommit mergeCommit = repoHandler.mergeIntoBranchFromAnotherBranch("master", "disconnected", "Merging disconnected branch into master branch");

        // Make sure that merge commit is as expected:
        assertNotNull(mergeCommit);
        assertNotSame(commit1, mergeCommit);
        assertNotSame(commit2, mergeCommit);
        assertNotSame(commit3, mergeCommit);
        assertEquals("Merging disconnected branch into master branch", mergeCommit.message);
        assertSame(commit3, mergeCommit.firstParent);
        assertEquals(1, mergeCommit.otherParents.size());
        assertSame(commit2, mergeCommit.otherParents.get(0));

        // Check out the merged commit and make sure the content is as expected:
        StringHashMapArea mergedArea = repoHandler.checkout(mergeCommit);
        // NOTICE: The Root value is from the third commit because it made after the second commit.
        assertEquals("/ : 'Root'\n" +
                     "/a : 'A3'\n" +
                     "/b : 'B1'\n" +
                     "/c : 'c1'\n" +
                     "/d : 'D2'\n" +
                     "/e : 'E3'", mergedArea.asListString());
    }

    /**
     * Tests that the merge API works as expected with no common parent between commits.
     * The disconnected branch has the last commit in this scenario so the root content will be the same as the disconnected branch.
     */
    @Test
    public void testMergeAPI_NoCommonParent_DisconnectedHasLastCommit()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Root");
        area.putString("/a", "A1");
        area.putString("/b", "B1");
        area.putString("/c", "c1");

        // Commit the area to the master branch:
        MemoryCommit commit1 = repoHandler.commitToBranch(area, "master", "First commit");

        // Change the content at an existing path that we will also change in the disconnected branch:
        area.putString("/a", "A3");

        // Add content at a new path:
        area.putString("/e", "E3");

        // Commit the changes to master (before making the disconnected changes):
        MemoryCommit commit2 = repoHandler.commitToBranch(area, "master", "Second Commit");

        // Create the disconnected branch:
        area = repoHandler.checkout(commit1);

        // Make some changes:
        area.putString(RepoPath.atRoot(), "New Root");
        area.putString("/a", "A2");
        // Leave /b the same.
        area.removeString("/c");
        // Add content at a new path:
        area.putString("/d", "D2");

        // Commit the changes to a disconnected branch (no common ancestor):
        MemoryCommit commit3 = repoHandler.commitToBranch(area, "disconnected", "Third commit");

        // Merge the disconnected branch back into the master branch:
        MemoryCommit mergeCommit = repoHandler.mergeIntoBranchFromAnotherBranch("master", "disconnected", "Merging disconnected branch into master branch");

        // Make sure that merge commit is as expected:
        assertNotNull(mergeCommit);
        assertNotSame(commit1, mergeCommit);
        assertNotSame(commit2, mergeCommit);
        assertNotSame(commit3, mergeCommit);
        assertEquals("Merging disconnected branch into master branch", mergeCommit.message);
        assertSame(commit2, mergeCommit.firstParent);
        assertEquals(1, mergeCommit.otherParents.size());
        assertSame(commit3, mergeCommit.otherParents.get(0));

        // Check out the merged commit and make sure the content is as expected:
        StringHashMapArea mergedArea = repoHandler.checkout(mergeCommit);
        // NOTICE: The Root value is from the third commit because it made after the second commit.
        assertEquals("/ : 'New Root'\n" +
                     "/a : 'A2'\n" +
                     "/b : 'B1'\n" +
                     "/c : 'c1'\n" +
                     "/d : 'D2'\n" +
                     "/e : 'E3'", mergedArea.asListString());
    }

    /**
     * Tests that the merge API works as expected with a common parent between commits.
     */
    @Test
    public void testMergeAPI_WithCommonParent()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Root");
        area.putString("/a", "A1");
        area.putString("/b", "B1");
        area.putString("/c", "c1");

        // Commit the area to the master branch:
        MemoryCommit commit1 = repoHandler.commitToBranch(area, "master", "First commit");

        // Make some changes:
        area.putString(RepoPath.atRoot(), "New Root");
        area.putString("/a", "A2");
        // Leave /b the same.
        area.removeString("/c");

        // Create a branch from master:
        repoHandler.createBranchAtCommit(commit1, "feature");

        // Commit the changes to a feature branch:
        MemoryCommit commit2 = repoHandler.commitToBranch(area, "feature", "Second commit");

        // Make another change to master:
        StringHashMapArea masterArea = repoHandler.checkout(commit1);
        masterArea.putString("/a", "A3");

        // Commit the changes to master (after making the feature changes):
        MemoryCommit commit3 = repoHandler.commitToBranch(masterArea, "master", "Third Commit");

        // Merge the feature branch back into the master branch:
        MemoryCommit mergeCommit = repoHandler.mergeIntoBranchFromAnotherBranch("master", "feature", "Merging Feature into Master");

        // Make sure that merge commit is as expected:
        assertNotNull(mergeCommit);
        assertNotSame(commit1, mergeCommit);
        assertNotSame(commit2, mergeCommit);
        assertNotSame(commit3, mergeCommit);
        assertEquals("Merging Feature into Master", mergeCommit.message);
        assertSame(commit3, mergeCommit.firstParent);
        assertEquals(1, mergeCommit.otherParents.size());
        assertSame(commit2, mergeCommit.otherParents.get(0));

        // Check out the merged commit and make sure the content is as expected:
        StringHashMapArea mergedArea = repoHandler.checkout(mergeCommit);
        assertEquals("/ : 'New Root'\n" +
                     "/a : 'A3'\n" +
                     "/b : 'B1'", mergedArea.asListString());
    }

    /**
     * This checks that we can create commits with a parent commit without needing to create a branch.
     * NANO-27
     */
    @Test
    public void testCommitWithParentAndNoBranch()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Hello World");

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commit(area, "Commit 1");
        assertNotNull(commit1);

        // Make sure that the commit is tracked as a dangling tip:
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit1), "The first commit should be a dangling tip.");

        // Make a change:
        area.putString("/A", "A");

        // Commit the changes:
        MemoryCommit commit2 = repoHandler.commit(area, "Commit 2", commit1);
        assertNotNull(commit2);
        assertNotSame(commit1, commit2);

        // Make sure that the second commit has the first commit as a parent:
        assertSame(commit1, commit2.firstParent, "The second commit should have the first commit as a parent.");

        // Make sure that the first commit is no longer tracked as a dangling commit tip:
        assertFalse(repoHandler.getRepo().getDanglingCommits().contains(commit1), "The first commit shouldn't be a dangling tip anymore.");
    }

    /**
     * This checks that we can create commits with multiple parent commits without needing to create a branch.
     * NANO-27
     */
    @Test
    public void testCommitWithParentArgsAndNoBranch()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Hello World");

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commit(area, "Commit 1");
        assertNotNull(commit1);

        // Make sure that the commit is tracked as a dangling tip:
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit1), "The first commit should be a dangling tip.");

        // Add branch 1A:
        StringHashMapArea areaAt1A = repoHandler.checkout(commit1);
        areaAt1A.putString("/A", "A");
        MemoryCommit commit1A = repoHandler.commit(areaAt1A, "Commit 1A", commit1);

        // Add branch 1B:
        StringHashMapArea areaAt1B = repoHandler.checkout(commit1);
        areaAt1B.putString("/B", "B");
        MemoryCommit commit1B = repoHandler.commit(areaAt1B, "Commit 1B", commit1);

        // Add branch 1C:
        StringHashMapArea areaAt1C = repoHandler.checkout(commit1);
        areaAt1C.putString("/C", "C");
        MemoryCommit commit1C = repoHandler.commit(areaAt1C, "Commit 1C", commit1);

        // Confirm that all 3 commits are dangling:
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit1A), "The commit should be a dangling tip.");
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit1B), "The commit should be a dangling tip.");
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit1C), "The commit should be a dangling tip.");

        // Commit the changes:
        MemoryCommit commit2 = repoHandler.commit(area, "Commit 2", commit1A, commit1B, commit1C);
        assertNotNull(commit2);
        assertNotSame(commit1, commit2);

        // Make sure that the second commit has the first commit as a parent:
        assertSame(commit1A, commit2.firstParent, "The second commit should have the branch commits as parents.");
        assertFalse(commit2.otherParents.contains(commit1A), "The second commit should not be in the list of other parents.");
        assertTrue(commit2.otherParents.contains(commit1B), "The second commit should have the branch commits as parents.");
        assertTrue(commit2.otherParents.contains(commit1C), "The second commit should have the branch commits as parents.");

        // Make sure that the branch commits are no longer tracked as dangling commit tips:
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit2), "The second commit should be a dangling tip.");
        assertFalse(repoHandler.getRepo().getDanglingCommits().contains(commit1A), "The commit shouldn't be a dangling tip anymore.");
        assertFalse(repoHandler.getRepo().getDanglingCommits().contains(commit1B), "The commit shouldn't be a dangling tip anymore.");
        assertFalse(repoHandler.getRepo().getDanglingCommits().contains(commit1C), "The commit shouldn't be a dangling tip anymore.");
    }

    /**
     * This checks that we can create commits with multiple parent commits without needing to create a branch.
     * NANO-27
     */
    @Test
    public void testCommitWithParentListAndNoBranch()
    {
        // Create the handler:
        StringMemoryRepoHandler repoHandler = new StringMemoryRepoHandler();

        // Create a new content area:
        StringHashMapArea area = repoHandler.createArea();

        // Add content to the area:
        area.putString("/", "Hello World");

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commit(area, "Commit 1");
        assertNotNull(commit1);

        // Make sure that the commit is tracked as a dangling tip:
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit1), "The first commit should be a dangling tip.");

        // Add branch 1A:
        StringHashMapArea areaAt1A = repoHandler.checkout(commit1);
        areaAt1A.putString("/A", "A");
        MemoryCommit commit1A = repoHandler.commit(areaAt1A, "Commit 1A", commit1);

        // Add branch 1B:
        StringHashMapArea areaAt1B = repoHandler.checkout(commit1);
        areaAt1B.putString("/B", "B");
        MemoryCommit commit1B = repoHandler.commit(areaAt1B, "Commit 1B", commit1);

        // Add branch 1C:
        StringHashMapArea areaAt1C = repoHandler.checkout(commit1);
        areaAt1C.putString("/C", "C");
        MemoryCommit commit1C = repoHandler.commit(areaAt1C, "Commit 1C", commit1);

        // Confirm that all 3 commits are dangling:
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit1A), "The commit should be a dangling tip.");
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit1B), "The commit should be a dangling tip.");
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit1C), "The commit should be a dangling tip.");

        // Commit the changes:
        MemoryCommit commit2 = repoHandler.commit(area, "Commit 2", commit1A, Arrays.asList(commit1B, commit1C));
        assertNotNull(commit2);
        assertNotSame(commit1, commit2);

        // Make sure that the second commit has the first commit as a parent:
        assertSame(commit1A, commit2.firstParent, "The second commit should have the branch commits as parents.");
        assertFalse(commit2.otherParents.contains(commit1A), "The second commit should not be in the list of other parents.");
        assertTrue(commit2.otherParents.contains(commit1B), "The second commit should have the branch commits as parents.");
        assertTrue(commit2.otherParents.contains(commit1C), "The second commit should have the branch commits as parents.");

        // Make sure that the branch commits are no longer tracked as dangling commit tips:
        assertTrue(repoHandler.getRepo().getDanglingCommits().contains(commit2), "The second commit should be a dangling tip.");
        assertFalse(repoHandler.getRepo().getDanglingCommits().contains(commit1A), "The commit shouldn't be a dangling tip anymore.");
        assertFalse(repoHandler.getRepo().getDanglingCommits().contains(commit1B), "The commit shouldn't be a dangling tip anymore.");
        assertFalse(repoHandler.getRepo().getDanglingCommits().contains(commit1C), "The commit shouldn't be a dangling tip anymore.");
    }
}
