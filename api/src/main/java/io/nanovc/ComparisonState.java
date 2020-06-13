/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

/**
 * The comparison state between two pieces of {@link Content} in an {@link Area}.
 * This is similar to {@link DifferenceState} except it contains {@link ComparisonState#UNCHANGED}.
 */
public enum ComparisonState
{
    /**
     * Indicates that the content is unchanged between the two areas.
     */
    UNCHANGED ("Unchanged", "Indicates that the content is unchanged between the two areas."),

    /**
     * Indicates that the content has changed between the two areas.
     */
    CHANGED("Changed", "Indicates that the content has changed between the two areas."),

    /**
     * The content has been added when going from the first area to the second area.
     */
    ADDED ("Added", "The content has been added when going from the first area to the second area."),

    /**
     * The content has been deleted when going from the first area to the second area.
     */
    DELETED ("Deleted", "The content has been deleted when going from the first area to the second area.");

    /**
     * The pretty (user friendly) name for the comparison state.
     */
    public final String prettyName;

    /**
     * The description for the comparison state.
     */
    public final String description;

    ComparisonState(String prettyName, String description)
    {
        this.prettyName = prettyName;
        this.description = description;
    }

}
