/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.CommitTags;
import io.nanovc.ComparisonAPI;
import io.nanovc.areas.HashMapArea;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.clocks.ClockWithVMNanos;
import io.nanovc.comparisons.HashMapComparisonHandler;
import io.nanovc.content.StringContent;
import io.nanovc.differences.HashMapDifferenceHandler;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import io.nanovc.merges.LastWinsMergeHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests dangling commit scenarios for the {@link MemoryRepoHandler}.
 */
public class MemoryRepoHandlerTests_DanglingCommits extends MemoryRepoHandlerTestBase<
    StringContent,
    StringHashMapArea,
    MemoryCommit,
    MemorySearchQuery,
    MemorySearchResults,
    MemoryRepo<StringContent, StringHashMapArea>,
    MemoryRepoEngine<StringContent, StringHashMapArea>,
    MemoryRepoHandler<StringContent, StringHashMapArea>
    >
{

    /**
     * Creates the specific type of handler under test.
     *
     * @return A new instance of the handler under test.
     */
    @Override protected MemoryRepoHandler<StringContent, StringHashMapArea> createNewRepoHandler()
    {
        return new MemoryRepoHandler<>(StringContent::new, StringHashMapArea::new);
    }

    /**
     * This tests that a dangling commit should be removed from the dangling commits list once a branch is created for it.
     */
    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingBranch()
    {
        // Create the handler:
        MemoryRepoHandler<StringContent, StringHashMapArea> repoHandler = createNewRepoHandler();

        // Create an area where we can commit content:
        StringHashMapArea contentArea = repoHandler.createArea();

        // Add content to the area:
        contentArea.putString("Hello", "World");

        // Get the repo so we can confirm the internal state:
        MemoryRepo<StringContent, StringHashMapArea> repo = repoHandler.getRepo();

        // Confirm that the
        assertEquals(0, repo.getDanglingCommits().size());

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commit(contentArea, "First commit!", CommitTags.none());

        // Confirm that we have a dangling commit:
        assertEquals(1, repo.getDanglingCommits().size());
        assertTrue(repo.getDanglingCommits().contains(commit1));

        // Create a branch for the commit:
        repoHandler.createBranchAtCommit(commit1, "master");

        // Confirm that we no longer have a dangling commit:
        assertEquals(0, repo.getDanglingCommits().size());

        // Confirm that the commit is a branch:
        assertEquals(1, repo.getBranchTips().size());
        assertSame(commit1, repo.getBranchTips().get("master"));
    }

    /**
     * This tests that a dangling commit should be removed from the dangling commits list once a tag is created for it.
     */
    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingTag()
    {
        // Create the handler:
        MemoryRepoHandler<StringContent, StringHashMapArea> repoHandler = createNewRepoHandler();

        // Create an area where we can commit content:
        StringHashMapArea contentArea = repoHandler.createArea();

        // Add content to the area:
        contentArea.putString("Hello", "World");

        // Get the repo so we can confirm the internal state:
        MemoryRepo<StringContent, StringHashMapArea> repo = repoHandler.getRepo();

        // Confirm that the
        assertEquals(0, repo.getDanglingCommits().size());

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commit(contentArea, "First commit!", CommitTags.none());

        // Confirm that we have a dangling commit:
        assertEquals(1, repo.getDanglingCommits().size());
        assertTrue(repo.getDanglingCommits().contains(commit1));

        // Create a tag for the commit:
        repoHandler.tagCommit(commit1, "tag");

        // Confirm that we no longer have a dangling commit:
        assertEquals(0, repo.getDanglingCommits().size());

        // Confirm that the commit is tagged:
        assertEquals(1, repo.getTags().size());
        assertSame(commit1, repo.getTags().get("tag"));
    }

    /**
     * This tests that a dangling commit is flagged after removing a branch.
     */
    @Test
    public void testDanglingCommitAfterRemovingBranch()
    {
        // Create the handler:
        MemoryRepoHandler<StringContent, StringHashMapArea> repoHandler = createNewRepoHandler();

        // Create an area where we can commit content:
        StringHashMapArea contentArea = repoHandler.createArea();

        // Add content to the area:
        contentArea.putString("Hello", "World");

        // Get the repo so we can confirm the internal state:
        MemoryRepo<StringContent, StringHashMapArea> repo = repoHandler.getRepo();

        // Confirm that the
        assertEquals(0, repo.getDanglingCommits().size());

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commitToBranch(contentArea, "master", "First commit!", CommitTags.none());

        // Confirm that we don't have a dangling commit:
        assertEquals(0, repo.getDanglingCommits().size());

        // Confirm that the commit is a branch:
        assertEquals(1, repo.getBranchTips().size());
        assertSame(commit1, repo.getBranchTips().get("master"));

        // Remove the branch:
        repoHandler.removeBranch("master");

        // Confirm that we have a dangling commit now:
        assertEquals(1, repo.getDanglingCommits().size());
        assertTrue(repo.getDanglingCommits().contains(commit1));

        // Confirm that there are no branches:
        assertEquals(0, repo.getBranchTips().size());
    }

    /**
     * This tests that a dangling commit is flagged after removing a tag.
     */
    @Test
    public void testDanglingCommitAfterRemovingTag()
    {
        // Create the handler:
        MemoryRepoHandler<StringContent, StringHashMapArea> repoHandler = createNewRepoHandler();

        // Create an area where we can commit content:
        StringHashMapArea contentArea = repoHandler.createArea();

        // Add content to the area:
        contentArea.putString("Hello", "World");

        // Get the repo so we can confirm the internal state:
        MemoryRepo<StringContent, StringHashMapArea> repo = repoHandler.getRepo();

        // Confirm that the
        assertEquals(0, repo.getDanglingCommits().size());

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commit(contentArea, "First commit!", CommitTags.none());

        // Confirm that we have a dangling commit:
        assertEquals(1, repo.getDanglingCommits().size());
        assertTrue(repo.getDanglingCommits().contains(commit1));

        // Confirm that there are no tags:
        assertEquals(0, repo.getTags().size());

        // Create a tag:
        repoHandler.tagCommit(commit1, "tag");

        // Confirm that we don't have a dangling commit:
        assertEquals(0, repo.getDanglingCommits().size());

        // Confirm that the commit is a tag:
        assertEquals(1, repo.getTags().size());
        assertSame(commit1, repo.getTags().get("tag"));

        // Remove the tag:
        repoHandler.removeTag("tag");

        // Confirm that we have a dangling commit now:
        assertEquals(1, repo.getDanglingCommits().size());
        assertTrue(repo.getDanglingCommits().contains(commit1));

        // Confirm that there are no tags:
        assertEquals(0, repo.getTags().size());
    }
}
