/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.areas;

import io.nanovc.CommitTags;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the creation of {@link CommitTags}.
 */
public class CommitTagsTests
{
    /**
     * Tests the creation of {@link CommitTags}.
     */
    @Test
    public void testCommitTags()
    {
        assertEquals("", CommitTags.with().asListString());
        assertEquals("/author : 'Luke'", CommitTags.withAuthor("Luke").asListString());
        assertEquals("/committer : 'Luke'", CommitTags.withCommitter("Luke").asListString());
        assertEquals("/custom : 'value'", CommitTags.with("custom", "value").asListString());
        assertEquals("/description : 'Commit Tags are Cool!'", CommitTags.withDescription("Commit Tags are Cool!").asListString());
        assertEquals("/description : 'Commit Tags can handle emoji's 👍‼'", CommitTags.withDescription("Commit Tags can handle emoji's 👍‼").asListString());

        assertEquals(
            "/author : 'Luke'\n" +
            "/author/timestamp : '1982-06-19T00:00:00Z'",
            CommitTags
                .withAuthorAndTimestamp(
                    "Luke",
                    Instant.from(ZonedDateTime.of(1982, 6, 19, 0, 0, 0, 0, ZoneId.of("Z")))
                )
                .asListString()
        );

        assertEquals(
            "/author : 'Luke'\n" +
            "/author/timestamp : '1982-06-19T00:00:00Z'\n" +
            "/committer : 'Luke'\n" +
            "/committer/timestamp : '1982-06-19T00:00:00Z'",
            CommitTags
                .withAuthorCommitterAndTimestamp(
                    "Luke",
                    Instant.from(ZonedDateTime.of(1982, 6, 19, 0, 0, 0, 0, ZoneId.of("Z")))
                )
                .asListString()
        );

        assertEquals(
            "/description : 'Using other tags'\n" +
            "/other : 'tag'\n" +
            "/other/meta : 'meta-tag'",
            CommitTags
                .withDescription("Using other tags")
                .and(CommitTags.with("other", "tag"))
                .and(CommitTags.with("other/meta", "meta-tag"))
                .asListString()
        );
    }
}
