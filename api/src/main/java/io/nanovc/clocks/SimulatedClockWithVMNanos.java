/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.clocks;

import io.nanovc.epochs.EpochWithVMNanos;
import io.nanovc.timestamps.TimestampWithVMNanos;

import java.time.Instant;

/**
 * A simulated clock which creates {@link TimestampWithVMNanos} relative to an {@link EpochWithVMNanos}.
 * This clock is useful for testing.
 * This clock allows us to simulate the VM nano clock by setting it precisely.
 * Make sure to set the simulated nano's during the test to simulate time passing.
 * This clock keeps the state around the last epoch and creates new epochs once the duration between newly created timestamps is out of range of a defined limit.
 * We want this behaviour because we can then control the data structures required to store each timestamp (eg: so they can fit in an int).
 */
public class SimulatedClockWithVMNanos extends AbstractClockWithVMNanos
{
    /**
     * The simulated value for the nano seconds for this clock.
     */
    private long simulatedNanos;

    /**
     * The simulated value for the global time for this clock.
     */
    private Instant simulatedInstant;

    /**
     * The simulated window of variance for the epoch for this clock.
     */
    private long simulatedEpochWindow;

    /**
     * Creates a new clock.
     * The epoch will be created when the first timestamp is created.
     * The minimum and maximum duration is set to the range of an int (-2s to +2s).
     * @param simulatedNanos The simulated value for the nano seconds for this clock.
     * @param simulatedInstant The simulated value for the global time for this clock.
     * @param simulatedEpochWindow The simulated window of variance for the epoch for this clock.
     */
    public SimulatedClockWithVMNanos(long simulatedNanos, Instant simulatedInstant, long simulatedEpochWindow)
    {
        this.simulatedNanos = simulatedNanos;
        this.simulatedInstant = simulatedInstant;
        this.simulatedEpochWindow = simulatedEpochWindow;
    }

    /**
     * Creates a new clock.
     * The epoch will be created when the first timestamp is created.
     *
     * @param minRange The minimum window range for this clock to reuse the last epoch.
     * @param maxRange The maximum window range for this clock to reuse the last epoch.
     * @param simulatedNanos The simulated value for the nano seconds for this clock.
     * @param simulatedInstant The simulated value for the global time for this clock.
     * @param simulatedEpochWindow The simulated window of variance for the epoch for this clock.
     */
    public SimulatedClockWithVMNanos(long minRange, long maxRange, long simulatedNanos, Instant simulatedInstant, long simulatedEpochWindow)
    {
        super(minRange, maxRange);
        this.simulatedNanos = simulatedNanos;
        this.simulatedInstant = simulatedInstant;
        this.simulatedEpochWindow = simulatedEpochWindow;
    }

    /**
     * Creates a new clock for the given epoch.
     * The minimum and maximum duration is set to the range of an int (-2s to +2s).
     *
     * @param startingEpoch The starting epoch to reuse for timestamps.
     * @param simulatedNanos The simulated value for the nano seconds for this clock.
     * @param simulatedInstant The simulated value for the global time for this clock.
     * @param simulatedEpochWindow The simulated window of variance for the epoch for this clock.
     */
    public SimulatedClockWithVMNanos(EpochWithVMNanos startingEpoch, long simulatedNanos, Instant simulatedInstant, long simulatedEpochWindow)
    {
        super(startingEpoch);
        this.simulatedNanos = simulatedNanos;
        this.simulatedInstant = simulatedInstant;
        this.simulatedEpochWindow = simulatedEpochWindow;
    }

    /**
     * Creates a new clock for the given epoch.
     *
     * @param startingEpoch The starting epoch to reuse for timestamps.
     * @param minRange      The minimum window range for this clock to reuse the last epoch.
     * @param maxRange      The maximum window range for this clock to reuse the last epoch.
     * @param simulatedNanos The simulated value for the nano seconds for this clock.
     * @param simulatedInstant The simulated value for the global time for this clock.
     * @param simulatedEpochWindow The simulated window of variance for the epoch for this clock.
     */
    public SimulatedClockWithVMNanos(EpochWithVMNanos startingEpoch, long minRange, long maxRange, long simulatedNanos, Instant simulatedInstant, long simulatedEpochWindow)
    {
        super(startingEpoch, minRange, maxRange);
        this.simulatedNanos = simulatedNanos;
        this.simulatedInstant = simulatedInstant;
        this.simulatedEpochWindow = simulatedEpochWindow;
    }

    /**
     * Gets the Virtual Machine nano seconds for this instant in time.
     *
     * @return The nano seconds for the current instant in time.
     */
    @Override
    protected long nowNanos()
    {
        return getSimulatedNanos();
    }

    /**
     * Creates a new epoch for this instant in time.
     *
     * @return A new epoch for this instant in time.
     */
    @Override
    protected EpochWithVMNanos createNewEpoch()
    {
        long nowNanos = nowNanos();
        return new EpochWithVMNanos(nowNanos, getSimulatedInstant(), nowNanos + getSimulatedEpochWindow());
    }

    /**
     * Gets the simulated value for the nano seconds for this clock.
     *
     * @return The simulated value for the nano seconds for this clock.
     */
    public long getSimulatedNanos()
    {
        return simulatedNanos;
    }

    /**
     * Sets the simulated value for the nano seconds for this clock.
     *
     * @param simulatedNanos The simulated value for the nano seconds for this clock.
     */
    public void setSimulatedNanos(long simulatedNanos)
    {
        this.simulatedNanos = simulatedNanos;
    }

    /**
     * Gets the simulated value for the global time for this clock.
     * @return The simulated value for the global time for this clock.
     */
    public Instant getSimulatedInstant()
    {
        return simulatedInstant;
    }

    /**
     * Sets the simulated value for the global time for this clock.
     * @param simulatedInstant The simulated value for the global time for this clock.
     */
    public void setSimulatedInstant(Instant simulatedInstant)
    {
        this.simulatedInstant = simulatedInstant;
    }

    /**
     * Gets the simulated window of variance for the epoch for this clock.
     * @return The simulated window of variance for the epoch for this clock.
     */
    public long getSimulatedEpochWindow()
    {
        return simulatedEpochWindow;
    }

    /**
     * Sets the simulated window of variance for the epoch for this clock.
     * @param simulatedEpochWindow The simulated window of variance for the epoch for this clock.
     */
    public void setSimulatedEpochWindow(long simulatedEpochWindow)
    {
        this.simulatedEpochWindow = simulatedEpochWindow;
    }
}
