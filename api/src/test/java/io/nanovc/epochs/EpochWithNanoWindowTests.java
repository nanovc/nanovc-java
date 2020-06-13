package io.nanovc.epochs;

import org.junit.jupiter.api.Test;
import io.nanovc.NanoVersionControlTestsBase;

import java.util.Map;
import java.util.TreeMap;

/**
 * Tests for {@link EpochWithNanoWindow}.
 */
public class EpochWithNanoWindowTests extends NanoVersionControlTestsBase
{

    @Test
    public void testCreation()
    {
        EpochWithVMNanos epoch = new EpochWithVMNanos();
    }

    /**
     * Tests what the "windows of uncertainty" are for measurements of global time.
     */
    @Test
    public void verifyNanoWindows()
    {
        final int COUNT = 1_000_000;

        // Keep a histogram of the window sizes:
        Map<Integer, Integer> histogram = new TreeMap<>();

        for (int i = 0; i < COUNT; i++)
        {
            // Create the epoch:
            EpochWithVMNanos epoch = new EpochWithVMNanos();

            // Convert the epoch from relative nanos to an uncertainty window:
            EpochWithNanoWindow epochWithNanoWindow = epoch.toEpochWithNanoWindow();

            // Get the size of the nano time window for the global measurement:
            int window = epochWithNanoWindow.nanoUncertaintyWindow;

            // Update the histogram:
            histogram.compute(window, (k, v) -> v == null ? 1 : v + 1);
        }

        // Print out the histogram:
        histogram.forEach((nanoDuration, count) -> System.out.printf("Duration: %,d ns Count: %,d%n", nanoDuration, count));
    }

}
