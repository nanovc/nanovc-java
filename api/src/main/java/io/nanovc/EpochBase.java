/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import java.time.Duration;
import java.time.Instant;

/**
 * An epoch in Nano Version Control is used as a reference point for high precision relative time measurement.
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
public abstract class EpochBase
    implements EpochAPI
{
    /**
     * Gets the global time for this epoch.
     * @return The global time for this epoch.
     */
    public abstract Instant getGlobalTime();

    /**
     * Gets the duration of the uncertainty window.
     * @return The uncertainty of the duration window.
     */
    public abstract Duration getUncertaintyDuration();
}
