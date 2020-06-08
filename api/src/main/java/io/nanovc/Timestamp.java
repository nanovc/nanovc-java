/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import java.time.Instant;

/**
 * A timestamp for high precision time measurement.
 * An {@link io.nanovc.Epoch} in Nano Version Control is used as a reference point for high precision relative time measurement.
 * <p>
 * The reason why we are reluctant to rely on the high precision measurement of the time instant is because of following reasons:
 * 1) Not all Java Virtual Machines are required to implement high precision sub second resolution.
 * 2) JDK uses milli second precision, but JDK 9 gives us nano precision. We still want the design to work irrespective of JDK version.
 * 3) With recent vulnerabilities like Sceptre, we see that specifications for EcmaScript are very reluctant to provide high precision timing. This means that we want our design to still work whether the runtime actually supports high precision timing or not.
 */
public abstract class Timestamp
{
    /**
     * Gets the UTC instant in time for this timestamp.
     * @return The UTC instant in time for this timestamp.
     */
    public abstract Instant getInstant();

}
