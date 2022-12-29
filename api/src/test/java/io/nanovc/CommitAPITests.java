package io.nanovc;

import io.nanovc.areas.ByteArrayAreaAPI;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.areas.StringAreaAPI;
import io.nanovc.timestamps.InstantTimestamp;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for usage of the {@link io.nanovc.CommitAPI}.
 * This is useful for pinning down the intended operations that we expect to be able to perform on generic commits.
 */
public class CommitAPITests
{
    @Test
    public void testCommitUsage()
    {
        // Create the mock commit to test the usage on:
        CommitAPI commit = new MockCommit();

        // Make sure the commit has what we expect:
        assertEquals("Mock Commit", commit.getMessage());
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

    /**
     * An implementation of the {@link CommitAPI} for us to confirm intended usage.
     */
    private static class MockCommit implements CommitAPI
    {

        @Override
        public String getMessage()
        {
            return "Mock Commit";
        }

        @Override
        public TimestampAPI getTimestamp()
        {
            return new InstantTimestamp(Instant.ofEpochSecond(1234567890L));
        }

        @Override
        public ByteArrayAreaAPI getSnapshot()
        {
            return new ByteArrayHashMapArea();
        }

        @Override
        public StringAreaAPI getCommitTags()
        {
            return CommitTags.withAuthor("Lukasz Machowski");
        }

        @Override
        public CommitAPI getFirstParentCommit()
        {
            // Cyclic commit. A bit strange but useful for unit testing. We normally would point at other commits.
            return this;
        }

        @Override
        public List<CommitAPI> getOtherParentCommits()
        {
            // Cyclic commit. A bit strange but useful for unit testing. We normally would point at other commits.
            return Collections.singletonList(this);
        }

        @Override
        public List<CommitAPI> getAllParentCommits()
        {
            // Cyclic commit. A bit strange but useful for unit testing. We normally would point at other commits.
            //                We add ourselves twice to be consistent with the getFirstParentCommit() and getOtherParentCommits() results.
            return Arrays.asList(this, this);
        }
    }
}
