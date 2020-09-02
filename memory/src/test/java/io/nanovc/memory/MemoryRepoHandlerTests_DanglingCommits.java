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
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.content.StringContent;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;

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
     * Asserts that dangling commits are removed after creating a branch.
     *
     * @param commitCount        The number of linear commits to create before testing.
     * @param commitCreator      The logic to create a commit in the history for this type of test. The first argument is the repo handler to use for the commit. The second argument is the default content area for the commit that is already populated. The third argument is the previous commit if there was one (null if there wasn't). The function must return the commit that was created.
     * @param repoAsserterBefore The logic to assert the state of the repo before all changes have been made.
     * @param repoModifier       The logic to modify the repo after the commits have been created. Provide the code that is specific to the type of test being performed. The first parameter is the repo handler under test. The second parameter is the last commit that was performed.
     * @param repoAsserterAfter  The logic to assert the state of the repo after all changes have been made.
     */
    public MemoryRepoHandler<StringContent, StringHashMapArea> assertDanglingCommits(
        int commitCount,
        TriFunction<MemoryRepoHandler<StringContent, StringHashMapArea>, StringHashMapArea, MemoryCommit, MemoryCommit> commitCreator,
        BiConsumer<MemoryRepo<StringContent, StringHashMapArea>, MemoryCommit> repoAsserterBefore,
        BiConsumer<MemoryRepoHandler<StringContent, StringHashMapArea>, MemoryCommit> repoModifier,
        BiConsumer<MemoryRepo<StringContent, StringHashMapArea>, MemoryCommit> repoAsserterAfter
    )
    {
        // Create the handler:
        MemoryRepoHandler<StringContent, StringHashMapArea> repoHandler = createNewRepoHandler();

        // Create an area where we can commit content:
        StringHashMapArea contentArea = repoHandler.createArea();

        // Add content to the area:
        contentArea.putString("Hello", "World");

        // Get the repo so we can confirm the internal state:
        MemoryRepo<StringContent, StringHashMapArea> repo = repoHandler.getRepo();

        // Confirm that there are no dangling commits to begin with:
        assertEquals(0, repo.getDanglingCommits().size(), "There should be no dangling commits with a new repo.");

        // Loop the required number of times to create a linear history:
        MemoryCommit lastCommit = null;
        Set<MemoryCommit> commits = new LinkedHashSet<>();
        for (int i = 1; i <= commitCount; i++)
        {
            // Commit the content:
            lastCommit = commitCreator.apply(repoHandler, contentArea, lastCommit);

            // Keep track of the commits:
            commits.add(lastCommit);
        }

        // Make sure that the repo handler is as expected before the changes:
        repoAsserterBefore.accept(repo, lastCommit);

        // Make sure that the intermediate commits are not part of the dangling commit list
        // (because they are in the parentage of the last commit):
        for (MemoryCommit commit : commits)
        {
            // Skip the last commit:
            if (commit == lastCommit) continue;

            // Make sure that the intermediate commit is not part of the list of dangling commits:
            assertFalse(repo.getDanglingCommits().contains(commit), "The intermediate commits shouldn't be in the list of dangling references.");
        }

        // Modify the repo for the specific test being run:
        repoModifier.accept(repoHandler, lastCommit);

        // Make sure that the repo handler is as expected after the changes:
        repoAsserterAfter.accept(repo, lastCommit);

        return repoHandler;
    }

    //#region Dangling Commit Should Be Removed After Creating Branch

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingBranch_1_Commit()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingBranch(1);
    }

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingBranch_2_Commits()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingBranch(2);
    }

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingBranch_3_Commits()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingBranch(3);
    }

    /**
     * Asserts that dangling commits are removed after creating a branch.
     *
     * @param commitCount The number of linear commits to create before testing.
     */
    public void assertDanglingCommitShouldBeRemovedAfterCreatingBranch(int commitCount)
    {
        assertDanglingCommits(
            commitCount,
            (repoHandler, contentArea, previousCommit) ->
            {
                // Create the specific type of commit for this test:
                return previousCommit == null
                    ? repoHandler.commit(contentArea, "Commit", CommitTags.none())
                    : repoHandler.commit(contentArea, "Commit", CommitTags.none(), previousCommit);
            },
            (repo, lastCommit) -> {
                // Confirm that we have a dangling commit:
                // NOTE: We only expect the tip of the dangling commits to be present.
                assertEquals(1, repo.getDanglingCommits().size());
                assertTrue(repo.getDanglingCommits().contains(lastCommit));
            },
            (repoHandler, lastCommit) -> {
                // Create a branch for the commit:
                repoHandler.createBranchAtCommit(lastCommit, "master");
            },
            (repo, lastCommit) -> {
                // Confirm that we no longer have a dangling commit:
                assertEquals(0, repo.getDanglingCommits().size());

                // Confirm that the commit is a branch:
                assertEquals(1, repo.getBranchTips().size());
                assertSame(lastCommit, repo.getBranchTips().get("master"));
            }
        );
    }

    //#endregion

    //#region Dangling Commit Should Be Removed After Creating Multiple Branches

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingMultipleBranches_1_Commit()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingMultipleBranches(1);
    }

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingMultipleBranches_2_Commits()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingMultipleBranches(2);
    }

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingMultipleBranches_3_Commits()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingMultipleBranches(3);
    }

    /**
     * Asserts that dangling commits are removed after creating multiple branches.
     *
     * @param commitCount The number of linear commits to create before testing.
     */
    public void assertDanglingCommitShouldBeRemovedAfterCreatingMultipleBranches(int commitCount)
    {
        assertDanglingCommits(
            commitCount,
            (repoHandler, contentArea, previousCommit) ->
            {
                // Create the specific type of commit for this test:
                return previousCommit == null
                    ? repoHandler.commit(contentArea, "Commit", CommitTags.none())
                    : repoHandler.commit(contentArea, "Commit", CommitTags.none(), previousCommit);
            },
            (repo, lastCommit) -> {
                // Confirm that we have a dangling commit:
                // NOTE: We only expect the tip of the dangling commits to be present.
                assertEquals(1, repo.getDanglingCommits().size());
                assertTrue(repo.getDanglingCommits().contains(lastCommit));
            },
            (repoHandler, lastCommit) -> {
                // Create multiple branches for the commit:
                repoHandler.createBranchAtCommit(lastCommit, "master");
                repoHandler.createBranchAtCommit(lastCommit, "another");
            },
            (repo, lastCommit) -> {
                // Confirm that we no longer have a dangling commit:
                assertEquals(0, repo.getDanglingCommits().size());

                // Confirm that the commit is a branch:
                assertEquals(2, repo.getBranchTips().size());
                assertSame(lastCommit, repo.getBranchTips().get("master"));
                assertSame(lastCommit, repo.getBranchTips().get("another"));
            }
        );
    }

    //#endregion

    //#region Dangling Commit Should Be Removed After Creating Tag

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingTag_1_Commit()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingTag(1);
    }

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingTag_2_Commits()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingTag(2);
    }

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingTag_3_Commits()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingTag(3);
    }

    /**
     * This tests that a dangling commit should be removed from the dangling commits list once a tag is created for it.
     *
     * @param commitCount The number of linear commits to create before testing.
     */
    public void assertDanglingCommitShouldBeRemovedAfterCreatingTag(int commitCount)
    {
        assertDanglingCommits(
            commitCount,
            (repoHandler, contentArea, previousCommit) ->
            {
                // Create the specific type of commit for this test:
                return previousCommit == null
                    ? repoHandler.commit(contentArea, "Commit", CommitTags.none())
                    : repoHandler.commit(contentArea, "Commit", CommitTags.none(), previousCommit);
            },
            (repo, lastCommit) -> {
                // Confirm that we have a dangling commit:
                // NOTE: We only expect the tip of the dangling commits to be present.
                assertEquals(1, repo.getDanglingCommits().size());
                assertTrue(repo.getDanglingCommits().contains(lastCommit));
            },
            (repoHandler, lastCommit) -> {
                // Create a tag for the commit:
                repoHandler.tagCommit(lastCommit, "tag");
            },
            (repo, lastCommit) -> {
                // Confirm that we no longer have a dangling commit:
                assertEquals(0, repo.getDanglingCommits().size());

                // Confirm that the commit is tagged:
                assertEquals(1, repo.getTags().size());
                assertSame(lastCommit, repo.getTags().get("tag"));
            }
        );
    }

    //#endregion

    //#region Dangling Commit Should Be Removed After Creating Multiple Tags

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingMultipleTags_1_Commit()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingMultipleTags(1);
    }

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingMultipleTags_2_Commits()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingMultipleTags(2);
    }

    @Test
    public void testDanglingCommitShouldBeRemovedAfterCreatingMultipleTags_3_Commits()
    {
        assertDanglingCommitShouldBeRemovedAfterCreatingMultipleTags(3);
    }

    /**
     * This tests that a dangling commit should be removed from the dangling commits list once multiple tags are created for it.
     *
     * @param commitCount The number of linear commits to create before testing.
     */
    public void assertDanglingCommitShouldBeRemovedAfterCreatingMultipleTags(int commitCount)
    {
        assertDanglingCommits(
            commitCount,
            (repoHandler, contentArea, previousCommit) ->
            {
                // Create the specific type of commit for this test:
                return previousCommit == null
                    ? repoHandler.commit(contentArea, "Commit", CommitTags.none())
                    : repoHandler.commit(contentArea, "Commit", CommitTags.none(), previousCommit);
            },
            (repo, lastCommit) -> {
                // Confirm that we have a dangling commit:
                // NOTE: We only expect the tip of the dangling commits to be present.
                assertEquals(1, repo.getDanglingCommits().size());
                assertTrue(repo.getDanglingCommits().contains(lastCommit));
            },
            (repoHandler, lastCommit) -> {
                // Create a multiple tags for the commit:
                repoHandler.tagCommit(lastCommit, "tag");
                repoHandler.tagCommit(lastCommit, "another");
            },
            (repo, lastCommit) -> {
                // Confirm that we no longer have a dangling commit:
                assertEquals(0, repo.getDanglingCommits().size());

                // Confirm that the commit is tagged:
                assertEquals(2, repo.getTags().size());
                assertSame(lastCommit, repo.getTags().get("tag"));
                assertSame(lastCommit, repo.getTags().get("another"));
            }
        );
    }

    //#endregion

    //#region Dangling Commit After Removing Branch

    @Test
    public void testDanglingCommitAfterRemovingBranch_1_Commit()
    {
        assertDanglingCommitAfterRemovingBranch(1);
    }

    @Test
    public void testDanglingCommitAfterRemovingBranch_2_Commits()
    {
        assertDanglingCommitAfterRemovingBranch(2);
    }

    @Test
    public void testDanglingCommitAfterRemovingBranch_3_Commits()
    {
        assertDanglingCommitAfterRemovingBranch(3);
    }

    /**
     * This tests that a dangling commit is flagged after removing a branch.
     *
     * @param commitCount The number of linear commits to create before testing.
     */
    public void assertDanglingCommitAfterRemovingBranch(int commitCount)
    {
        assertDanglingCommits(
            commitCount,
            (repoHandler, contentArea, previousCommit) ->
            {
                // Create the specific type of commit for this test:
                return repoHandler.commitToBranch(contentArea, "master", "Commit", CommitTags.none());
            },
            (repo, lastCommit) -> {
                // Confirm that we don't have a dangling commit because we have a branch:
                assertEquals(0, repo.getDanglingCommits().size());
                assertFalse(repo.getDanglingCommits().contains(lastCommit));
            },
            (repoHandler, lastCommit) -> {
                // Remove the branch:
                repoHandler.removeBranch("master");
            },
            (repo, lastCommit) -> {
                // Confirm that we have a dangling commit:
                assertEquals(1, repo.getDanglingCommits().size());

                // Confirm that the commit is not a branch:
                assertEquals(0, repo.getBranchTips().size());
            }
        );
    }

    //#endregion

    //#region Dangling Commit After Removing Branch With Other Branches

    @Test
    public void testDanglingCommitAfterRemovingBranchWithOtherBranches_1_Commit()
    {
        assertDanglingCommitAfterRemovingBranchWithOtherBranches(1);
    }

    @Test
    public void testDanglingCommitAfterRemovingBranchWithOtherBranches_2_Commits()
    {
        assertDanglingCommitAfterRemovingBranchWithOtherBranches(2);
    }

    @Test
    public void testDanglingCommitAfterRemovingBranchWithOtherBranches_3_Commits()
    {
        assertDanglingCommitAfterRemovingBranchWithOtherBranches(3);
    }

    /**
     * This tests that a dangling commit is flagged after removing a branch while the commit is also in another branch.
     *
     * @param commitCount The number of linear commits to create before testing.
     */
    public void assertDanglingCommitAfterRemovingBranchWithOtherBranches(int commitCount)
    {
        assertDanglingCommits(
            commitCount,
            (repoHandler, contentArea, previousCommit) ->
            {
                // Create the specific type of commit for this test:
                return repoHandler.commitToBranch(contentArea, "master", "Commit", CommitTags.none());
            },
            (repo, lastCommit) -> {
                // Confirm that we don't have a dangling commit because we have a branch:
                assertEquals(0, repo.getDanglingCommits().size());
                assertFalse(repo.getDanglingCommits().contains(lastCommit));
            },
            (repoHandler, lastCommit) -> {
                // Make another branch so that there are multiple for this test:
                repoHandler.createBranchAtCommit(lastCommit, "another");

                // Remove the branch:
                repoHandler.removeBranch("master");
            },
            (repo, lastCommit) -> {
                // Confirm that we don't have a dangling commit because it is still referenced from the other branch:
                assertEquals(0, repo.getDanglingCommits().size());

                // Confirm that the commit is in the other branch:
                assertEquals(1, repo.getBranchTips().size());
                assertSame(lastCommit, repo.getBranchTips().get("another"));
            }
        );
    }

    //#endregion

    //#region Dangling Commit After Removing Branch With Unrelated Branches

    @Test
    public void testDanglingCommitAfterRemovingBranchWithUnrelatedBranches_1_Commit()
    {
        assertDanglingCommitAfterRemovingBranchWithUnrelatedBranches(1);
    }

    @Test
    public void testDanglingCommitAfterRemovingBranchWithUnrelatedBranches_2_Commits()
    {
        assertDanglingCommitAfterRemovingBranchWithUnrelatedBranches(2);
    }

    @Test
    public void testDanglingCommitAfterRemovingBranchWithUnrelatedBranches_3_Commits()
    {
        assertDanglingCommitAfterRemovingBranchWithUnrelatedBranches(3);
    }

    /**
     * This tests that a dangling commit is flagged after removing a branch while there are other unrelated commits in other branches.
     *
     * @param commitCount The number of linear commits to create before testing.
     */
    public void assertDanglingCommitAfterRemovingBranchWithUnrelatedBranches(int commitCount)
    {
        assertDanglingCommits(
            commitCount,
            (repoHandler, contentArea, previousCommit) ->
            {
                // Create an unrelated commit to a different branch for this test:
                repoHandler.commitToBranch(contentArea, "another", "Another Commit", CommitTags.none());

                // Create the specific type of commit for this test:
                return repoHandler.commitToBranch(contentArea, "master", "Commit", CommitTags.none());
            },
            (repo, lastCommit) -> {
                // Confirm that we don't have a dangling commit because we have branches for each commit:
                assertEquals(0, repo.getDanglingCommits().size());
                assertFalse(repo.getDanglingCommits().contains(lastCommit));

                // Confirm that we have the two expected branches:
                assertEquals(2, repo.branchTips.size());
                assertTrue(repo.branchTips.containsKey("master"));
                assertTrue(repo.branchTips.containsKey("another"));
            },
            (repoHandler, lastCommit) -> {
                // Remove the branch (while leaving the other unrelated branches):
                repoHandler.removeBranch("master");
            },
            (repo, lastCommit) -> {
                // Confirm that we do have a dangling commit because it is not referenced from the unrelated branches:
                assertEquals(1, repo.getDanglingCommits().size());
                assertTrue(repo.getDanglingCommits().contains(lastCommit));

                // Confirm that the commit is in the other branch:
                assertEquals(1, repo.getBranchTips().size());
            }
        );
    }

    //#endregion

    //#region Dangling Commit After Removing Tag

    @Test
    public void testDanglingCommitAfterRemovingTag_1_Commit()
    {
        assertDanglingCommitAfterRemovingTag(1);
    }

    @Test
    public void testDanglingCommitAfterRemovingTag_2_Commits()
    {
        assertDanglingCommitAfterRemovingTag(2);
    }

    @Test
    public void testDanglingCommitAfterRemovingTag_3_Commits()
    {
        assertDanglingCommitAfterRemovingTag(3);
    }

    /**
     * This tests that a dangling commit is flagged after removing a tag.
     *
     * @param commitCount The number of linear commits to create before testing.
     */
    public void assertDanglingCommitAfterRemovingTag(int commitCount)
    {
        assertDanglingCommits(
            commitCount,
            (repoHandler, contentArea, previousCommit) ->
            {
                // Create the specific type of commit for this test:
                MemoryCommit commit = previousCommit == null
                    ? repoHandler.commit(contentArea, "Commit", CommitTags.none())
                    : repoHandler.commit(contentArea, "Commit", CommitTags.none(), previousCommit);

                // Tag the commit:
                repoHandler.tagCommit(commit, "tag");

                return commit;
            },
            (repo, lastCommit) -> {
                // Confirm that we don't have a dangling commit because we have a tag:
                assertEquals(0, repo.getDanglingCommits().size());
                assertFalse(repo.getDanglingCommits().contains(lastCommit));
            },
            (repoHandler, lastCommit) -> {
                // Remove the tag:
                repoHandler.removeTag("tag");
            },
            (repo, lastCommit) -> {
                // Confirm that we have a dangling commit now:
                assertEquals(1, repo.getDanglingCommits().size());
                assertTrue(repo.getDanglingCommits().contains(lastCommit));

                // Confirm that there are no tags:
                assertEquals(0, repo.getTags().size());
            }
        );
    }

    //#endregion

    //#region Dangling Commit After Removing Tag With Other Tags

    @Test
    public void testDanglingCommitAfterRemovingTagWithOtherTags_1_Commit()
    {
        assertDanglingCommitAfterRemovingTagWithOtherTags(1);
    }

    @Test
    public void testDanglingCommitAfterRemovingTagWithOtherTags_2_Commits()
    {
        assertDanglingCommitAfterRemovingTagWithOtherTags(2);
    }

    @Test
    public void testDanglingCommitAfterRemovingTagWithOtherTags_3_Commits()
    {
        assertDanglingCommitAfterRemovingTagWithOtherTags(3);
    }

    /**
     * This tests that a dangling commit is flagged after removing a tag while there are other tags that reference the same commit.
     *
     * @param commitCount The number of linear commits to create before testing.
     */
    public void assertDanglingCommitAfterRemovingTagWithOtherTags(int commitCount)
    {
        assertDanglingCommits(
            commitCount,
            (repoHandler, contentArea, previousCommit) ->
            {
                // Create the specific type of commit for this test:
                MemoryCommit commit = previousCommit == null
                    ? repoHandler.commit(contentArea, "Commit", CommitTags.none())
                    : repoHandler.commit(contentArea, "Commit", CommitTags.none(), previousCommit);

                // Tag the commit:
                repoHandler.tagCommit(commit, "tag");

                return commit;
            },
            (repo, lastCommit) -> {
                // Confirm that we don't have a dangling commit because we have a tag:
                assertEquals(0, repo.getDanglingCommits().size());
                assertFalse(repo.getDanglingCommits().contains(lastCommit));
            },
            (repoHandler, lastCommit) -> {
                // Tag the commit so that we have another tag referencing it for this test:
                repoHandler.tagCommit(lastCommit, "another");

                // Remove the tag:
                repoHandler.removeTag("tag");
            },
            (repo, lastCommit) -> {
                // Confirm that we don't have a dangling commit because it is still referencd by another tag:
                assertEquals(0, repo.getDanglingCommits().size());

                // Confirm that the commit is still tagged:
                assertEquals(1, repo.getTags().size());
                assertSame(lastCommit, repo.getTags().get("another"));
            }
        );
    }

    //#endregion

    //#region Dangling Commit After Removing Tag With Unrelated Tags

    @Test
    public void testDanglingCommitAfterRemovingTagWithUnrelatedTags_1_Commit()
    {
        assertDanglingCommitAfterRemovingTagWithUnrelatedTags(1);
    }

    @Test
    public void testDanglingCommitAfterRemovingTagWithUnrelatedTags_2_Commits()
    {
        assertDanglingCommitAfterRemovingTagWithUnrelatedTags(2);
    }

    @Test
    public void testDanglingCommitAfterRemovingTagWithUnrelatedTags_3_Commits()
    {
        assertDanglingCommitAfterRemovingTagWithUnrelatedTags(3);
    }

    /**
     * This tests that a dangling commit is flagged after removing a tag while there are other unrelated tags too.
     *
     * @param commitCount The number of linear commits to create before testing.
     */
    public void assertDanglingCommitAfterRemovingTagWithUnrelatedTags(int commitCount)
    {
        assertDanglingCommits(
            commitCount,
            (repoHandler, contentArea, previousCommit) ->
            {
                // Create the unrelated commit for this test:
                MemoryCommit unrelatedCommit = repoHandler.commit(contentArea, "Another", CommitTags.none());

                // Tag the unrelated commit:
                repoHandler.tagCommit(unrelatedCommit, "Another");

                // Create the specific type of commit:
                MemoryCommit commit = previousCommit == null
                    ? repoHandler.commit(contentArea, "Commit", CommitTags.none())
                    : repoHandler.commit(contentArea, "Commit", CommitTags.none(), previousCommit);

                // Tag the commit:
                repoHandler.tagCommit(commit, "tag");

                return commit;
            },
            (repo, lastCommit) -> {
                // Confirm that we don't have a dangling commit because all commits are tagged:
                assertEquals(0, repo.getDanglingCommits().size());
                assertFalse(repo.getDanglingCommits().contains(lastCommit));
            },
            (repoHandler, lastCommit) -> {
                // Remove the tag:
                repoHandler.removeTag("tag");
            },
            (repo, lastCommit) -> {
                // Confirm that we have a dangling commit now because the unrelated tags don't point at the commit:
                assertEquals(1, repo.getDanglingCommits().size());
                assertTrue(repo.getDanglingCommits().contains(lastCommit));

                // Confirm that there are other tags:
                assertEquals(1, repo.getTags().size());
            }
        );
    }

    //#endregion

    @FunctionalInterface
    public interface TriFunction<T, U, V, R>
    {
        /**
         * Applies this function to the given arguments.
         *
         * @param t the first function argument
         * @param u the second function argument
         * @param v the third function argument
         * @return the function result
         */
        R apply(T t, U u, V v);
    }
}
