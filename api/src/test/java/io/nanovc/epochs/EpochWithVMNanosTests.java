package io.nanovc.epochs;

import org.junit.jupiter.api.Test;
import io.nanovc.NanoVersionControlTestsBase;

import java.util.Map;
import java.util.TreeMap;

/**
 * Tests for {@link EpochWithVMNanos}.
 */
public class EpochWithVMNanosTests extends NanoVersionControlTestsBase
{

    @Test
    public void testCreation()
    {
        EpochWithVMNanos epoch = new EpochWithVMNanos();
    }

    /**
     * Tests what the nano durations are for measurements of global time.
     */
    @Test
    public void verifyNanoDurations()
    {
        final int COUNT = 1_000_000;

        // Keep a histogram of the window sizes:
        Map<Long, Integer> histogram = new TreeMap<>();

        for (int i = 0; i < COUNT; i++)
        {
            // Create the epoch:
            EpochWithVMNanos epoch = new EpochWithVMNanos();

            // Get the size of the nano time window for the global measurement:
            long nanoDuration = epoch.getNanoTimeDurationLong();

            // Update the histogram:
            histogram.compute(nanoDuration, (k, v) -> v == null ? 1 : v + 1);
        }

        // Print out the histogram:
        histogram.forEach((nanoDuration, count) -> System.out.printf("Duration: %,d ns Count: %,d%n", nanoDuration, count));
    }

}
