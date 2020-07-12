/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.ByteArrayIndex;
import io.nanovc.CommitTags;
import io.nanovc.ComparisonAPI;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.clocks.SimulatedInstantClock;
import io.nanovc.content.ByteArrayContent;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static io.nanovc.memory.ByteHelper.bytes;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests common usage scenarios for the {@link MemoryRepoHandler}.
 */
public class MemoryNanoRepoTests extends MemoryNanoVersionControlTestsBase
{

    @Test
    public void testCreation()
    {
        // Create the repo handler with each of the constructors:
        MemoryNanoRepo repo;
        repo = new MemoryNanoRepo();
        repo = new MemoryNanoRepo(new HashWrapperByteArrayIndex());
        repo = new MemoryNanoRepo(
            new HashWrapperByteArrayIndex(),
            new MemoryRepoEngine<ByteArrayContent,ByteArrayHashMapArea>(),
            MemoryNanoRepo.COMMON_CLOCK,
            MemoryNanoRepo.COMMON_DIFFERENCE_HANDLER,
            MemoryNanoRepo.COMMON_COMPARISON_HANDLER,
            MemoryNanoRepo.COMMON_MERGE_HANDLER
            );
    }

    /**
     * This tests 'Hello World'.
     */
    @Test
    public void testHelloWorld()
    {
        // Create the repo:
        MemoryNanoRepo repo = new MemoryNanoRepo();

        // Create an area where we can commit content:
        ByteArrayHashMapArea contentArea = repo.createArea();

        // Add content to the area:
        contentArea.putBytes("Hello", bytes("World"));
        contentArea.putBytes("Static",  bytes("Content"));

        // Commit the content:
        MemoryCommit commit1 = repo.commit(contentArea, "First commit!", CommitTags.none());

        // Add and modify content:
        contentArea.putBytes("Hello", bytes("Nano World"));
        contentArea.putBytes("Info", bytes("Details"));

        // Commit again:
        MemoryCommit commit2 = repo.commit(contentArea, "Second commit.", CommitTags.none());

        // Get the difference between the two commits:
        ComparisonAPI comparison = repo.computeComparisonBetweenCommits(commit1, commit2);
        assertEquals(
            "/Hello : Changed\n" +
            "/Info : Added\n" +
            "/Static : Unchanged",
            comparison.asListString()
        );
    }

    @Test
    public void testUsage1()
    {
        // Create the repo:
        MemoryNanoRepo repo = new MemoryNanoRepo();

        // Create an area for us to put content:
        ByteArrayHashMapArea area = repo.createArea();
        area.putBytes("/", "Hello World!".getBytes());

        // Commit the content:
        MemoryCommit first_commit = repo.commitToBranch(area, "master", "First commit", CommitTags.none());

        // Change the content:
        area.putBytes("/A", "A1".getBytes());

        // Commit the changed content:
        MemoryCommit second_commit = repo.commitToBranch(area, "master", "Second Commit", CommitTags.none());

        // Create another branch:
        repo.createBranchAtCommit(first_commit, "alternate");

        // Make a change to the content:
        area.putBytes("/A", "A2".getBytes());

        // Commit the change:
        MemoryCommit memoryCommit = repo.commitToBranch(area, "alternate", "Alternate commit", CommitTags.none());

        // Merge the changes:
        MemoryCommit merge_commit = repo.mergeIntoBranchFromAnotherBranch("master", "alternate", "Merging Alternate Branch into Master", CommitTags.none());

        // Get the merged content:
        ByteArrayHashMapArea mergedContent = repo.checkout(merge_commit);

        // Make sure the content is as expected:
        assertEquals("/ : byte[12] ➡ 'Hello World!'\n" +
                     "/A : byte[2] ➡ 'A2'", mergedContent.asListString());
    }

    @Test
    public void testOverridingClock()
    {
        // Create the repo:
        MemoryNanoRepo repo = new MemoryNanoRepo();

        // Create an area for us to put content:
        ByteArrayHashMapArea area = repo.createArea();
        area.putBytes("/", "Hello World!".getBytes());

        // Commit the content:
        MemoryCommit first_commit = repo.commitToBranch(area, "master", "First commit", CommitTags.none());

        // Create a clock that we want to override with:
        SimulatedInstantClock clock = new SimulatedInstantClock(Instant.ofEpochSecond(123456789L));

        // Use the simulated clock:
        repo.setClock(clock);

        // Make sure the clock was set:
        assertSame(clock, repo.getClock());

        // Create another commit:
        MemoryCommit second_commit = repo.commitToBranch(area, "master", "Second Commit", CommitTags.none());

        // Make sure that the two timestamps are different:
        assertNotEquals(first_commit.getTimestamp().getInstant(), second_commit.getTimestamp().getInstant());

        // Make sure that the second commit has the same timestamp as the simulated clock:
        assertEquals(clock.getNowOverride(), second_commit.getTimestamp().getInstant());
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
            MemoryNanoRepo repo = new MemoryNanoRepo(sharedByteArrayIndex);

            // Create an area for us to put content:
            ByteArrayHashMapArea area = repo.createArea();
            area.putBytes("/", "Hello World!".getBytes());

            // Commit the content:
            MemoryCommit first_commit = repo.commitToBranch(area, "master", "First commit", CommitTags.none());

            // Change the content:
            area.putBytes("/A", ("A1" + (i % 100)).getBytes());

            // Commit the changed content:
            MemoryCommit second_commit = repo.commitToBranch(area, "master", "Second Commit", CommitTags.none());

            // Create another branch:
            repo.createBranchAtCommit(first_commit, "alternate");

            // Make a change to the content:
            area.putBytes("/A", ("A2" + (i % 100)).getBytes());

            // Commit the change:
            MemoryCommit memoryCommit = repo.commitToBranch(area, "alternate", "Alternate commit", CommitTags.none());

            // Merge the changes:
            MemoryCommit merge_commit = repo.mergeIntoBranchFromAnotherBranch("master", "alternate", "Merging Alternate Branch into Master", CommitTags.none());

            // Get the merged content:
            ByteArrayHashMapArea mergedContent = repo.checkout(merge_commit);

            count += mergedContent.size();
        }

        // End timing:
        long endTime = System.nanoTime();
        long durationNanos = endTime - startTime;
        System.out.printf("Duration = %,d ns%n", durationNanos);
        System.out.printf("Duration = %,d ms%n", durationNanos/1_000_000);
        System.out.printf("Count = %,d repos%n", MAX);
        System.out.printf("Rate = %,d repos/sec%n", MAX * 1_000_000_000L / durationNanos);


        // Make sure that the count has a value:
        assertTrue(count > 0);
    }

}
