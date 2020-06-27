/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.epochs;

import io.nanovc.EpochBase;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * An epoch in Nano Version Control is used as a reference point for high precision relative time measurement.
 * This specific epoch has an effective "window of uncertainty" of the time measurement.
 * We don't know exactly where in the window the absolute global time was measured but this is useful to understand the fuzziness of the epoch measurement.
 * Epochs with smaller windows of uncertainty are more desirable because we are more confident that relative timestamps have a smaller window of uncertainty.
 * Sometimes it might be worth re-processing timestamp histories to be relative to a different {@link EpochWithVMNanos} so that we have less uncertainty in the timestamps.
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
 */
public class EpochWithNanoWindow extends EpochBase
{
    /**
     * This is the global time for this Epoch, which ties it to a UTC date and time.
     * The window of uncertainty of this instant in time is given in {@link #nanoUncertaintyWindow}.
     */
    public final Instant globalTime;

    /**
     * This is the window of uncertainty (in nano seconds) of the {@link #globalTime} measurement that was taken.
     * We don't know exactly where in the window the absolute global time was measured but this is useful to understand the fuzziness of the epoch measurement.
     * Epochs with smaller windows of uncertainty are more desirable because we are more confident that relative timestamps have a smaller window of uncertainty.
     * It's up to the algorithm interpreting this Epoch to decide how to distribute this window of uncertainty.
     * The global time could be considered as the start, middle, end or anything in-between for the window of uncertainty.
     * The uncertainty window would have been computed from the different between the {@link EpochWithVMNanos#nanoTimeAfter} - {@link EpochWithVMNanos#nanoTimeBefore}.
     * This idea is explained in more detail in {@link System#nanoTime}.
     */
    public final int nanoUncertaintyWindow;

    /**
     * Creates a new epoch with the given local nano time.
     *
     * @param globalTime            The global UTC time for the epoch being created. Use this to set the global time if it was captured outside of this call.
     * @param nanoUncertaintyWindow This is the window of uncertainty (in nano seconds) of the {@link #globalTime} measurement that was taken.
     */
    public EpochWithNanoWindow(Instant globalTime, int nanoUncertaintyWindow)
    {
        this.globalTime = globalTime;
        this.nanoUncertaintyWindow = nanoUncertaintyWindow;
    }

    /**
     * Creates a new epoch with the global UTC time being recorded during this constructor call.
     *
     * @param nanoUncertaintyWindow This is the window of uncertainty (in nano seconds) of the {@link #globalTime} measurement that was taken.
     */
    public EpochWithNanoWindow(int nanoUncertaintyWindow)
    {
        this.globalTime = Instant.now();
        this.nanoUncertaintyWindow = nanoUncertaintyWindow;
    }

    /**
     * Creates a new epoch with the global UTC time being recorded during this constructor call.
     * The global time is taken from the given clock, which is useful to override when unit testing.
     *
     * @param globalClock The clock to use to get the global time. This clock can be overridden for unit testing.
     * @param nanoUncertaintyWindow This is the window of uncertainty (in nano seconds) of the {@link #globalTime} measurement that was taken.
     */
    public EpochWithNanoWindow(Clock globalClock, int nanoUncertaintyWindow)
    {
        this.globalTime = Instant.now(globalClock);
        this.nanoUncertaintyWindow = nanoUncertaintyWindow;
    }

    /**
     * Gets the duration of the uncertainty window.
     * @return The uncertainty of the duration window.
     */
    @Override
    public Duration getUncertaintyDuration()
    {
        return Duration.ofNanos(this.nanoUncertaintyWindow);
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
}
