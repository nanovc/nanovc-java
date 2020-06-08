/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.clocks;

import io.nanovc.Clock;
import io.nanovc.epochs.EpochWithVMNanos;
import io.nanovc.timestamps.TimestampWithVMNanos;

/**
 * An abstract clock which creates {@link TimestampWithVMNanos} relative to an {@link EpochWithVMNanos}.
 * This clock keeps the state around the last epoch and creates new epochs once the duration between newly created timestamps is out of range of a defined limit.
 * We want this behaviour because we can then control the data structures required to store each timestamp (eg: so they can fit in an int).
 * The sub class must decide how it gets the Virtual Machine nano seconds for the clock.
 */
public abstract class AbstractClockWithVMNanos extends Clock<TimestampWithVMNanos>
{
    /**
     * This is the last epoch that was created by this clock.
     */
    private EpochWithVMNanos lastEpoch;

    /**
     * The minimum window range for this clock to reuse the last epoch.
     * If the timestamp is earlier than this range then a new epoch is created and reused.
     */
    private final long minRange;


    /**
     * The maximum window range for this clock to reuse the last epoch.
     * If the timestamp is later than this range then a new epoch is created and reused.
     */
    private final long maxRange;

    /**
     * Creates a new clock.
     * The epoch will be created when the first timestamp is created.
     * The minimum and maximum duration is set to the range of an int (-2s to +2s).
     */
    public AbstractClockWithVMNanos()
    {
        this(null, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Creates a new clock.
     * The epoch will be created when the first timestamp is created.
     * @param minRange The minimum window range for this clock to reuse the last epoch.
     * @param maxRange The maximum window range for this clock to reuse the last epoch.
     */
    public AbstractClockWithVMNanos(long minRange, long maxRange)
    {
        this(null, minRange, maxRange);
    }

    /**
     * Creates a new clock for the given epoch.
     * The minimum and maximum duration is set to the range of an int (-2s to +2s).
     * @param startingEpoch The starting epoch to reuse for timestamps.
     */
    public AbstractClockWithVMNanos(EpochWithVMNanos startingEpoch)
    {
        this(startingEpoch, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Creates a new clock for the given epoch.
     * @param startingEpoch The starting epoch to reuse for timestamps.
     * @param minRange The minimum window range for this clock to reuse the last epoch.
     * @param maxRange The maximum window range for this clock to reuse the last epoch.
     */
    public AbstractClockWithVMNanos(EpochWithVMNanos startingEpoch, long minRange, long maxRange)
    {
        this.lastEpoch = startingEpoch;
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    /**
     * Gets the Virtual Machine nano seconds for this instant in time.
     * @return The nano seconds for the current instant in time.
     */
    protected abstract long nowNanos();

    /**
     * Creates a new epoch for this instant in time.
     * @return A new epoch for this instant in time.
     */
    protected abstract EpochWithVMNanos createNewEpoch();

    /**
     * Creates a timestamp for the current instant in time.
     * @return A new timestamp for the current instant in time.
     */
    @Override
    public TimestampWithVMNanos now()
    {
        // Get the current relative nano's from the Java Virtual Machine:
        long nowNanos = nowNanos();

        // Check whether we have an epoch:
        if (this.lastEpoch == null)
        {
            // We haven't created an epoch yet for this clock.
            // Capture the current time as the epoch that the timestamps are relative to:
            this.lastEpoch = createNewEpoch();
        }
        // Now we have the last epoch.

        // Confirm that the duration from the epoch is within the range of the clock:
        long deltaNanos = nowNanos - this.lastEpoch.nanoTimeBefore;
        if (deltaNanos < this.minRange || deltaNanos > this.maxRange)
        {
            // The window from the epoch to the timestamp is out of range of the clock.

            // Create a new epoch so that we are in range of the clock:
            this.lastEpoch = createNewEpoch();
        }
        // Now we know that the duration from the epoch to the timestamp is within range of the clock.

        // Create a new timestamp:
        TimestampWithVMNanos timestamp = new TimestampWithVMNanos(this.lastEpoch, nowNanos);

        return timestamp;
    }
}
