package io.nanovc.timestamps;

import org.junit.jupiter.api.Test;
import io.nanovc.NanoVersionControlTestsBase;

import java.time.Instant;

/**
 * Tests for {@link InstantTimestamp}.
 */
public class InstantTimestampTests extends NanoVersionControlTestsBase
{
    @Test
    public void testCreation()
    {
        InstantTimestamp timestamp = new InstantTimestamp(Instant.EPOCH);
    }
}
