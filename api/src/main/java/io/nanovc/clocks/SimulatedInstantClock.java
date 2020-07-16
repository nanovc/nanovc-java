package io.nanovc.clocks;

import io.nanovc.ClockBase;
import io.nanovc.timestamps.InstantTimestamp;

import java.time.Instant;

/**
 * This is a simulated clock that allows us to override timestamps.
 */
public class SimulatedInstantClock extends ClockBase<InstantTimestamp>
{
    /**
     * The override value to use for the current instant in time.
     */
    public Instant nowOverride;

    /**
     * Creates a simulated clock with the given override for the time.
     * @param nowOverride The instant in time to use as the simulated time for the clock.
     */
    public SimulatedInstantClock(Instant nowOverride)
    {
        this.nowOverride = nowOverride;
    }

    /**
     * Creates a simulated clock where the current time is used as the simulated time.
     */
    public SimulatedInstantClock()
    {
        // Use the current instant in time for the clock:
        this.nowOverride = Instant.now();
    }

    /**
     * Creates a timestamp for the current instant in time.
     *
     * @return A new timestamp for the current instant in time.
     */
    @Override public InstantTimestamp now()
    {
        return new InstantTimestamp(this.nowOverride);
    }

    /**
     * Gets the override value to use for the current instant in time.
     * @return The override value to use for the current instant in time.
     */
    public Instant getNowOverride()
    {
        return nowOverride;
    }

    /**
     * Sets the override value to use for the current instant in time.
     * @param nowOverride The override value to use for the current instant in time.
     */
    public void setNowOverride(Instant nowOverride)
    {
        this.nowOverride = nowOverride;
    }
}
