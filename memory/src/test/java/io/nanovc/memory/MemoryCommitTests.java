package io.nanovc.memory;

import io.nanovc.CommitAPI;
import io.nanovc.CommitTags;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.timestamps.InstantTimestamp;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for usage of the {@link MemoryCommit}.
 * This is useful for pinning down the intended operations that we expect to be able to perform on generic commits.
 */
public class MemoryCommitTests
{
    @Test
    public void testCommitUsage()
    {
        // Create the mock commit to test the usage on:
        MemoryCommit commit = new MemoryCommit();
        commit.setMessage("Memory Commit");
        commit.setTimestamp(new InstantTimestamp(Instant.ofEpochSecond(1234567890L)));
        commit.setCommitTags(CommitTags.withAuthor("Lukasz Machowski"));
        commit.setSnapshot(new ByteArrayHashMapArea());
        commit.setFirstParent(commit);
        commit.setOtherParents(Collections.singletonList(commit));

        // Make sure the commit has what we expect:
        assertEquals("Memory Commit", commit.getMessage());
        assertEquals("2009-02-13T23:31:30Z", commit.getTimestamp().getInstant().toString());
        assertEquals("/author : 'Lukasz Machowski'", commit.getCommitTags().asListString());
        assertEquals("", commit.getSnapshot().asListString());

        // Explore the parents of the commit:
        CommitAPI parent1 = commit.getFirstParentCommit();
        CommitAPI parent2 = commit.getOtherParentCommits().get(0);
        List<CommitAPI> allParentCommits = commit.getAllParentCommits();
        assertSame(parent1, allParentCommits.get(0));
        assertSame(parent2, allParentCommits.get(1));
    }
}
