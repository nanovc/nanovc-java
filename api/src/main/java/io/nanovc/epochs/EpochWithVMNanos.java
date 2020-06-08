/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.epochs;

import io.nanovc.Epoch;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * An epoch in Nano Version Control is used as a reference point for high precision relative time measurement.
 * This specific epoch has a snapshot of the local virtual machine nano time so that it can be used as a reference for other relative timestamps.
 * The local nanos are described in {@link System#nanoTime}
 * A snapshot of the nano time is taken just before and just after the global time is measured.
 * This gives us an effective "window of uncertainty" of the time measurement, but it does tie it to the relative high precision timer of the running Java Virtual Machine.
 * Epochs with smaller windows of uncertainty are more desirable because we are more confident that relative timestamps have a smaller window of uncertainty.
 * Sometimes it might be worth re-processing timestamp histories to be relative to a different {@link EpochWithVMNanos} so that we have less uncertainty in the timestamps.
 * <p>
 * The reason why we are reluctant to rely on the high precision measurement of the time instant is because of following reasons:
 * 1) Not all Java Virtual Machines are required to implement high precision sub second resolution.
 * 2) JDK uses milli second precision, but JDK 9 gives us nano precision. We still want the design to work irrespective of JDK version.
 * 3) With recent vulnerabilities like Sceptre, we see that specifications for EcmaScript are very reluctant to provide high precision timing. This means that we want our design to still work whether the runtime actually supports high precision timing or not.
 * <p>
 * An epoch is an instant in time chosen as the origin of a particular era.
 * The "epoch" then serves as a reference point from which time is measured.
 * Time measurement units are counted from the epoch so that the date and time of events can be specified unambiguously.
 * Events taking place before the epoch can be dated by counting negatively from the epoch,
 * though in pragmatic periodization practice, epochs are defined for the past,
 * and another epoch is used to start the next era,
 * therefore serving as the ending of the older preceding era.
 * The whole purpose and criteria of such definitions are to clarify and co-ordinate scholarship about a period, at times, across disciplines.
 * <p>
 * https://en.wikipedia.org/wiki/Epoch_(reference_date)
 * <p>
 * The reason why we don't go with an interface for the time framework is because we don't really intend it to be extended.
 * We imagine that engines and handlers are not that interested in specific sub types.
 * The specific sub types are needed to be dealt with specifically for key algorithms around creating time stamps etc.
 * Instead the high precision timing requirements for nano version control are met with the simple time framework fleshed out here.
 * Time will tell whether this call is right :)
 */
public class EpochWithVMNanos extends Epoch
{
    /**
     * This is a snapshot of the high precision nano time from the Java Virtual Machine just before the {@link #globalTime} was measured.
     * The absolute value of this value is irrelevant.
     * Instead, the difference between this time and another local Nano Time measurement is meaningful, either for another Epoch or for a timestamp.
     * This idea is explained in more detail in {@link System#nanoTime}.
     */
    public final long nanoTimeBefore;

    /**
     * This is the global time for this Epoch, which ties it to a UTC date and time.
     * To tie this time to the high precision nano time of the running Java Virtual Machine, see {@link #nanoTimeBefore} and {@link #nanoTimeAfter}.
     */
    public final Instant globalTime;

    /**
     * This is a snapshot of the high precision nano time from the Java Virtual Machine just after the {@link #globalTime} was measured.
     * The absolute value of this value is irrelevant.
     * Instead, the difference between this time and another local Nano Time measurement is meaningful, either for another Epoch or for a timestamp.
     * This idea is explained in more detail in {@link System#nanoTime}.
     */
    public final long nanoTimeAfter;

    /**
     * Creates a new epoch with the given local nano time.
     *
     * @param nanoTimeBefore The local relative nano time for the epoch being created just before the global time was measured. Use this to set the local nano time if it was captured outside of this call.
     * @param globalTime     The global UTC time for the epoch being created. Use this to set the global time if it was captured outside of this call.
     * @param nanoTimeAfter  The local relative nano time for the epoch being created just after the global time was measured. Use this to set the local nano time if it was captured outside of this call.
     */
    public EpochWithVMNanos(long nanoTimeBefore, Instant globalTime, long nanoTimeAfter)
    {
        this.nanoTimeBefore = nanoTimeBefore;
        this.globalTime = globalTime;
        this.nanoTimeAfter = nanoTimeAfter;
    }

    /**
     * Creates a new epoch with all time measurements (local nanos and global UTC) being recorded during this constructor call.
     */
    public EpochWithVMNanos()
    {
        this.nanoTimeBefore = System.nanoTime();
        this.globalTime = Instant.now();
        this.nanoTimeAfter = System.nanoTime();
    }

    /**
     * Creates a new epoch with all time measurements (local nanos and global UTC) being recorded during this constructor call.
     * The global time is taken from the given clock, which is useful to override when unit testing.
     * The nano time is taken from {@link System#nanoTime()}.
     * @param globalClock The clock to use to get the global time. This clock can be overridden for unit testing.
     */
    public EpochWithVMNanos(Clock globalClock)
    {
        this.nanoTimeBefore = System.nanoTime();
        this.globalTime = Instant.now(globalClock);
        this.nanoTimeAfter = System.nanoTime();
    }

    /**
     * Gets the duration of the measurement window for the global time.
     * This is the number of nano seconds between the before and after nano time values.
     * @return The duration of the global time measurement window as calculated from the difference of the {@link #nanoTimeAfter} - {@link #nanoTimeBefore}, truncated to fit into an int.
     */
    public int getNanoTimeDurationInt()
    {
        return (int)(this.nanoTimeAfter - this.nanoTimeBefore);
    }

    /**
     * Gets the duration of the measurement window for the global time.
     * This is the number of nano seconds between the before and after nano time values.
     * @return The duration of the global time measurement window as calculated from the difference of the {@link #nanoTimeAfter} - {@link #nanoTimeBefore}.
     */
    public long getNanoTimeDurationLong()
    {
        return this.nanoTimeAfter - this.nanoTimeBefore;
    }

    /**
     * Gets a representation of this epoch without the local relative nano information.
     * It will contain the information as a "Window of Uncertainty".
     * NOTE: Once you convert to an epoch with a window of uncertainty, we lose the ability to tie local relative timestamps together.
     * @return The epoch with a window of uncertainty.
     */
    public EpochWithNanoWindow toEpochWithNanoWindow()
    {
        return new EpochWithNanoWindow(this.globalTime, getNanoTimeDurationInt());
    }

    /**
     * Gets the global time for this epoch.
     *
     * @return The global time for this epoch.
     */
    @Override
    public Instant getGlobalTime()
    {
        return this.globalTime;
    }

    /**
     * Gets the duration of the uncertainty window.
     *
     * @return The uncertainty of the duration window.
     */
    @Override
    public Duration getUncertaintyDuration()
    {
        return Duration.ofNanos(this.getNanoTimeDurationLong());
    }
}
