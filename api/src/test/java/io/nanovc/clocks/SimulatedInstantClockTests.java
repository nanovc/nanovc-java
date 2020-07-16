package io.nanovc.clocks;

import io.nanovc.NanoVersionControlTestsBase;
import io.nanovc.timestamps.InstantTimestamp;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Tests for {@link SimulatedClockWithVMNanos}.
 */
public class SimulatedInstantClockTests extends NanoVersionControlTestsBase
{

    @Test
    public void testCreation()
    {
        new SimulatedInstantClock();
        new SimulatedInstantClock(Instant.ofEpochSecond(123456789L));
    }

    @Test
    public void testInstant()
    {
        // Define the instant in time we are interested in:
        final long EPOCH_SECOND = 123456789L;

        // Create the simulated clock:
        SimulatedInstantClock clock = new SimulatedInstantClock(Instant.ofEpochSecond(EPOCH_SECOND));

        // Get the instant:
        InstantTimestamp instant1 = clock.now();

        // Make sure that it's the expected time:
        assertEquals(EPOCH_SECOND, instant1.getInstant().getEpochSecond());

        // Get another instant and make sure that it's a separate instance:
        InstantTimestamp instant2 = clock.now();

        // Make sure that it's the expected time:
        assertEquals(EPOCH_SECOND, instant2.getInstant().getEpochSecond());

        // Make that they are separate instances:
        assertNotSame(instant1, instant2);
    }

}
