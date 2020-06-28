/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.strings;

import io.nanovc.ByteArrayIndex;
import io.nanovc.CommitTags;
import io.nanovc.ComparisonAPI;
import io.nanovc.RepoPath;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import io.nanovc.memory.MemoryCommit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the creation of {@link StringNanoRepo}'s.
 */
public class StringNanoRepoTests
{
    @Test
    public void testCreation()
    {
        StringNanoRepo repo = new StringNanoRepo();
        repo = new StringNanoRepo(new HashWrapperByteArrayIndex());
        repo = new StringNanoRepo(
            new HashWrapperByteArrayIndex(),
            StringNanoRepo.COMMON_ENGINE,
            StringNanoRepo.COMMON_CLOCK,
            StringNanoRepo.COMMON_DIFFERENCE_HANDLER,
            StringNanoRepo.COMMON_COMPARISON_HANDLER,
            StringNanoRepo.COMMON_MERGE_HANDLER
        );
    }

    @Test
    public void testHelloWorld()
    {
        // Create the repo:
        StringNanoRepo repo = new StringNanoRepo();

        // Create an area for us to put content:
        // NOTE: Think of this as a mini-filesystem.
        StringHashMapArea contentArea = repo.createArea();

        contentArea.putString("Hello", "World");
        contentArea.putString("Static", "Content");
        contentArea.putString("Mistake", "Honest");

        // Commit the content:
        MemoryCommit commit1 = repo.commit(contentArea, "First commit!", CommitTags.none());

        // Modify content:
        contentArea.putString("Hello", "Nano World");

        // Remove unwanted content:
        contentArea.removeContent("Mistake");

        // The content area supports paths:
        contentArea.putString(RepoPath.at("Hello").resolve("Info"), "Details");

        // And even emoji's:
        contentArea.putString(RepoPath.at("🔧").resolve("👍"), "I ❤ NanoVC‼");

        // Commit again, but this time to a branch:
        MemoryCommit commit2 = repo.commitToBranch(contentArea, "master", "Second commit.", CommitTags.withAuthor("Luke"));

        // Get the difference between the two commits:
        ComparisonAPI comparison = repo.computeComparisonBetweenCommits(commit1, commit2);
        assertEquals(
            "/Hello : Changed\n" +
            "/Hello/Info : Added\n" +
            "/Mistake : Deleted\n" +
            "/Static : Unchanged\n" +
            "/🔧/👍 : Added",
            comparison.asListString()
        );
    }

    @Test
    public void testUsage1()
    {
        // Create the repo:
        StringNanoRepo repo = new StringNanoRepo();

        // Create an area for us to put content:
        StringHashMapArea area = repo.createArea();
        area.putString("/", "Hello World!");

        // Commit the content:
        MemoryCommit first_commit = repo.commitToBranch(area, "master", "First commit", CommitTags.none());

        // Change the content:
        area.putString("/A", "A1");

        // Commit the changed content:
        MemoryCommit second_commit = repo.commitToBranch(area, "master", "Second Commit", CommitTags.none());

        // Create another branch:
        repo.createBranchAtCommit(first_commit, "alternate");

        // Make a change to the content:
        area.putString("/A", "A2");

        // Commit the change:
        MemoryCommit memoryCommit = repo.commitToBranch(area, "alternate", "Alternate commit", CommitTags.none());

        // Merge the changes:
        MemoryCommit merge_commit = repo.mergeIntoBranchFromAnotherBranch("master", "alternate", "Merging Alternate Branch into Master", CommitTags.none());

        // Get the merged content:
        StringHashMapArea mergedContent = repo.checkout(merge_commit);

        // Make sure the content is as expected:
        assertEquals(
            "/ : 'Hello World!'\n" +
            "/A : 'A2'",
            mergedContent.asListString());
    }

    @Test
    public void performanceTest1()
    {
        final int MAX = 1_000_000;

        // Create a shared byte array index so that we can reuse memory for common content.
        ByteArrayIndex sharedByteArrayIndex = new HashWrapperByteArrayIndex();

        // Keep track of the repo's:
        int count = 0;

        // Start timing:
        long startTime = System.nanoTime();

        for (int i = 0; i < MAX; i++)
        {
            // Create the repo:
            StringNanoRepo repo = new StringNanoRepo(sharedByteArrayIndex);

            // Create an area for us to put content:
            StringHashMapArea area = repo.createArea();
            area.putString("/", "Hello World!");

            // Commit the content:
            MemoryCommit first_commit = repo.commitToBranch(area, "master", "First commit", CommitTags.none());

            // Change the content:
            area.putString("/A", "A1" + (i % 100));

            // Commit the changed content:
            MemoryCommit second_commit = repo.commitToBranch(area, "master", "Second Commit", CommitTags.none());

            // Create another branch:
            repo.createBranchAtCommit(first_commit, "alternate");

            // Make a change to the content:
            area.putString("/A", "A2" + (i % 100));

            // Commit the change:
            MemoryCommit memoryCommit = repo.commitToBranch(area, "alternate", "Alternate commit", CommitTags.none());

            // Merge the changes:
            MemoryCommit merge_commit = repo.mergeIntoBranchFromAnotherBranch("master", "alternate", "Merging Alternate Branch into Master", CommitTags.none());

            // Get the merged content:
            StringHashMapArea mergedContent = repo.checkout(merge_commit);

            count += mergedContent.size();
        }

        // End timing:
        long endTime = System.nanoTime();
        long durationNanos = endTime - startTime;
        System.out.printf("Duration = %,d ns%n", durationNanos);
        System.out.printf("Duration = %,d ms%n", durationNanos / 1_000_000);
        System.out.printf("Count = %,d repos%n", MAX);
        System.out.printf("Rate = %,d repos/sec%n", MAX * 1_000_000_000L / durationNanos);

        // Make sure that the count has a value:
        assertTrue(count > 0);
    }
}
