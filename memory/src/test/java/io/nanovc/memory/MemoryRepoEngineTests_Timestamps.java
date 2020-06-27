/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.ClockBase;
import io.nanovc.TimestampBase;
import io.nanovc.areas.StringAreaAPI;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.clocks.ClockWithVMNanos;
import io.nanovc.clocks.SimulatedClockWithVMNanos;
import io.nanovc.content.StringContent;
import io.nanovc.epochs.EpochWithVMNanos;
import io.nanovc.indexes.PassThroughByteArrayIndex;
import io.nanovc.timestamps.TimestampWithVMNanos;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link MemoryRepoEngine} for {@link TimestampBase} functionality.
 */
public class MemoryRepoEngineTests_Timestamps extends MemoryNanoVersionControlTestsBase
{

    @Test
    public void testCreation()
    {
        new MemoryRepoEngine<StringContent, StringAreaAPI>();
    }

    /**
     * Tests that we can optimize timestamps for a repo so that {@link TimestampWithVMNanos} are re-based to more appropriate epochs.
     */
    @Test
    public void testCommitsHaveTimestamps()
    {
        // Create the engine under test:
        MemoryRepoEngine<StringContent, StringAreaAPI> engine = new MemoryRepoEngine<>();

        // Create a repo that we are testing with:
        MemoryRepo<StringContent, StringAreaAPI> repo = engine.createRepo();

        // Create a clock that will give us timestamps:
        ClockWithVMNanos clock = new ClockWithVMNanos();

        // Create a commit in the repo:
        MemoryCommit commit = createCommit(engine, repo, clock);

        // Make sure that the commit has a timestamp:
        assertNotNull(commit.timestamp, "We expect the commit to have a timestamp when we commit");
    }

    /**
     * Tests that we can optimize timestamps for a repo so that {@link TimestampWithVMNanos} are re-based to more appropriate epochs.
     */
    @Test
    public void testOptimizingTimestamps_1_Commit()
    {
        // Create the engine under test:
        MemoryRepoEngine<StringContent, StringAreaAPI> engine = new MemoryRepoEngine<>();

        // Create a repo that we are testing with:
        MemoryRepo<StringContent, StringAreaAPI> repo = engine.createRepo();

        // Create a clock that will give us timestamps:
        ClockWithVMNanos clock = new ClockWithVMNanos();

        // Create some known structure in the repo:
        MemoryCommit commit;
        commit = createCommit(engine, repo, clock);

        // Make sure that the commit has a VM timestamp:
        assertTrue(commit.timestamp instanceof TimestampWithVMNanos, "We expect the timestamp to be a VM timestamp when it comes from a VM clock");

        // Save a reference to the timestamp and epoch that was created so we can check later whether it was modified:
        TimestampWithVMNanos timestampBefore = (TimestampWithVMNanos) commit.timestamp;
        EpochWithVMNanos epochBefore = timestampBefore.epoch;

        // Optimize the timestamp:
        engine.optimizeTimestamps(repo);

        // Make sure that the timestamp is the same instance and references the same epoch because there are no other timestamps:
        assertSame(timestampBefore, commit.timestamp, "The timestamp should have stayed the same after optimization");
        assertSame(epochBefore, timestampBefore.epoch, "The epoch should have stayed the same after optimization");
    }

    /**
     * Tests that we can optimize timestamps for a repo so that {@link TimestampWithVMNanos} are re-based to more appropriate epochs.
     * The first commit has a high Epoch window.
     * The second commit which is outside of the Epoch window has a low Epoch window.
     * Since we prefer Epochs with smaller windows, we re-base the timestamps relative to the second Epoch.
     */
    @Test
    public void testOptimizingTimestamps_2_Commits()
    {
        // Create the engine under test:
        MemoryRepoEngine<StringContent, StringAreaAPI> engine = new MemoryRepoEngine<>();

        // Create a repo that we are testing with:
        MemoryRepo<StringContent, StringAreaAPI> repo = engine.createRepo();

        // Create a clock that will give us timestamps:
        SimulatedClockWithVMNanos clock = new SimulatedClockWithVMNanos(0L, 500_000L, 0L, Instant.EPOCH, 0L);

        // Create some known structure in the repo:
        clock.setSimulatedNanos(1_000_000L);
        clock.setSimulatedEpochWindow(10_000L); // Large window.
        MemoryCommit commit1 = createCommit(engine, repo, clock);

        clock.setSimulatedNanos(2_000_000L);
        clock.setSimulatedEpochWindow(1_000L); // Small window.
        MemoryCommit commit2 = createCommit(engine, repo, clock);

        // Make sure that the commit has a VM timestamp:
        assertTrue(commit1.timestamp instanceof TimestampWithVMNanos, "We expect the timestamp to be a VM timestamp when it comes from a VM clock");
        assertTrue(commit2.timestamp instanceof TimestampWithVMNanos, "We expect the timestamp to be a VM timestamp when it comes from a VM clock");

        // Save a reference to the timestamp and epoch that was created so we can check later whether it was modified:
        TimestampWithVMNanos timestampBefore1 = (TimestampWithVMNanos) commit1.timestamp;
        TimestampWithVMNanos timestampBefore2 = (TimestampWithVMNanos) commit2.timestamp;
        EpochWithVMNanos epochBefore1 = timestampBefore1.epoch;
        EpochWithVMNanos epochBefore2 = timestampBefore2.epoch;

        // Optimize the timestamp:
        engine.optimizeTimestamps(repo);

        // Save a reference to the timestamps and epochs after the optimization:
        TimestampWithVMNanos timestampAfter1 = (TimestampWithVMNanos) commit1.timestamp;
        TimestampWithVMNanos timestampAfter2 = (TimestampWithVMNanos) commit2.timestamp;

        // Make sure that the first commits timestamp is re-based around the second Epoch because it has a smaller window:
        assertNotSame(epochBefore1, timestampAfter1.epoch, "The epoch for the first timestamp should have been re-based to the second epoch after optimization");
        assertSame(epochBefore2, timestampAfter1.epoch, "The epoch for the first timestamp should have been re-based to the second epoch after optimization");
        assertSame(epochBefore2, timestampAfter2.epoch, "The epoch for the second timestamp should have stayed the same after optimization");
    }

    /**
     * Creates a commit in the given repo.
     * @param engine The engine to use to create the commit.
     * @param repo The repo to commit to.
     * @param clock
     * @return The commit that was created.
     */
    protected MemoryCommit createCommit(MemoryRepoEngine<StringContent, StringAreaAPI> engine, MemoryRepo<StringContent, StringAreaAPI> repo, ClockBase<? extends TimestampBase> clock)
    {
        // Create a new commit:
        MemoryCommit commit = engine.commit(new StringHashMapArea(), "Commit", repo, PassThroughByteArrayIndex.instance, clock);

        return commit;
    }

}
