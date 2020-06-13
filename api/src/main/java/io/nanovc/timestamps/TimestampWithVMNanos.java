/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.timestamps;

import io.nanovc.Timestamp;
import io.nanovc.epochs.EpochWithVMNanos;

import java.time.Instant;

/**
 * A timestamp for high precision relative time measurement.
 * This timestamp measures the high precision nano seconds of the virtual machine running.
 * This timestamp is relative to an {@link EpochWithVMNanos}.
 * An epoch in Nano Version Control is used as a reference point for high precision relative time measurement.
 * This specific timestamp has a snapshot of the local virtual machine nano time so that it can be used as a reference for other relative epochs and timestamps.
 * The local nanos are described in {@link System#nanoTime}
 * A snapshot of the nano time is taken when the timestamp is created.
 * Sometimes it might be worth re-processing timestamp histories to be relative to a different {@link EpochWithVMNanos} so that we have less uncertainty in the timestamps.
 * The value of having timestamps with relative nanos is that you can meaningfully re-epoch other relative timestamps because they actually store the absolute nano time of the same virtual machine.
 * <p>
 * The reason why we are reluctant to rely on the high precision measurement of the time instant is because of following reasons:
 * 1) Not all Java Virtual Machines are required to implement high precision sub second resolution.
 * 2) JDK uses milli second precision, but JDK 9 gives us nano precision. We still want the design to work irrespective of JDK version.
 * 3) With recent vulnerabilities like Sceptre, we see that specifications for EcmaScript are very reluctant to provide high precision timing. This means that we want our design to still work whether the runtime actually supports high precision timing or not.
 */
public class TimestampWithVMNanos extends Timestamp
{
    /**
     * The epoch that this timestamp is relative to.
     */
    public final EpochWithVMNanos epoch;

    /**
     * The local relative nano time when this timestamp was taken.
     * If this is positive then the timestamp happened after the epoch.
     * If this is negative then the timestamp happened before the epoch.
     */
    public final long nanoTime;

    /**
     * Creates a new timestamp relative to the given epoch and with the given relative local nano time.
     * @param epoch The epoch that this timestamp is relative to.
     * @param nanoTime The local relative nano time of the Java Virtual Machine when this timestamp was captured.
     */
    public TimestampWithVMNanos(EpochWithVMNanos epoch, long nanoTime)
    {
        this.epoch = epoch;
        this.nanoTime = nanoTime;
    }

    /**
     * Gets the UTC instant in time for this timestamp.
     */
    @Override
    public Instant getInstant()
    {
        return this.epoch.getGlobalTime().plusNanos(this.nanoTime);
    }

    /**
     * Create a new instant timestamp from this timestamp.
     * @return A new instant time stamp.
     */
    public InstantTimestamp toInstantTimestamp()
    {
        return new InstantTimestamp(getInstant());
    }
}
