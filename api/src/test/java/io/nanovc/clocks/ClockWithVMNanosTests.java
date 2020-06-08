package io.nanovc.clocks;

import io.nanovc.NanoVersionControlTestsBase;
import io.nanovc.epochs.EpochWithVMNanos;
import io.nanovc.timestamps.TimestampWithVMNanos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link ClockWithVMNanos}.
 */
public class ClockWithVMNanosTests extends NanoVersionControlTestsBase
{
    @Test
    public void testCreation()
    {
        new ClockWithVMNanos();
        new ClockWithVMNanos(0L, 1_000L);
        new ClockWithVMNanos(new EpochWithVMNanos());
        new ClockWithVMNanos(new EpochWithVMNanos(), 0L, 2_000L);
    }

    /**
     * This confirms that timestamps with durations that are within 2s of each other all reuse the same epoch.
     */
    @Test
    public void testEpochReuse_Sub1s()
    {
        // Create a clock with a 1s window on either side:
        ClockWithVMNanos clock = new ClockWithVMNanos(-1_000_000_000L, +1_000_000_000L);

        // Create three high resolution timestamps:
        TimestampWithVMNanos timestamp1 = clock.now();
        TimestampWithVMNanos timestamp2 = clock.now();
        TimestampWithVMNanos timestamp3 = clock.now();

        // Make sure the epoch is the same instance because we want the clock to reuse epochs if they are within range of the clock:
        assertSame(timestamp1.epoch, timestamp2.epoch, "We expected the same epoch to be reused because the duration is within 1s");
        assertSame(timestamp2.epoch, timestamp3.epoch, "We expected the same epoch to be reused because the duration is within 1s");
    }

    /**
     * This confirms that timestamps with durations that are outside of the clock range will create different epochs.
     */
    @Test
    public void testEpochCreation_Over5ms() throws InterruptedException
    {
        // Create a clock with a 5ms window on either side:
        ClockWithVMNanos clock = new ClockWithVMNanos(-5_000_000L, +5_000_000L);

        // Create three high resolution timestamps:
        TimestampWithVMNanos timestamp1 = clock.now();
        Thread.sleep(10L);
        TimestampWithVMNanos timestamp2 = clock.now();
        Thread.sleep(10L);
        TimestampWithVMNanos timestamp3 = clock.now();

        // Make sure the epoch is a different instance because we want the clock to create new epochs if they are outside of the range of the clock:
        assertNotSame(timestamp1.epoch, timestamp2.epoch, "We expected a new epoch to be created because the duration is greater than the range of the clock");
        assertNotSame(timestamp2.epoch, timestamp3.epoch, "We expected a new epoch to be created because the duration is greater than the range of the clock");
        assertNotSame(timestamp1.epoch, timestamp3.epoch, "We expected a new epoch to be created because the duration is greater than the range of the clock");
    }
}
