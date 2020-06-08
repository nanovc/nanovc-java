package io.nanovc.timestamps;

import org.junit.jupiter.api.Test;
import io.nanovc.NanoVersionControlTestsBase;
import io.nanovc.epochs.EpochWithVMNanos;

import java.time.Instant;

/**
 * Tests for {@link TimestampWithVMNanos}.
 */
public class TimestampWithVMNanosTests extends NanoVersionControlTestsBase
{
    @Test
    public void testCreation()
    {
        EpochWithVMNanos epoch = new EpochWithVMNanos(0, Instant.EPOCH, 100);
        TimestampWithVMNanos timestamp = new TimestampWithVMNanos(epoch, 200);
    }
}
