/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.ComparisonAPI;
import io.nanovc.areas.HashMapArea;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.clocks.ClockWithVMNanos;
import io.nanovc.comparisons.HashMapComparisonHandler;
import io.nanovc.content.StringContent;
import io.nanovc.differences.HashMapDifferenceHandler;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import io.nanovc.merges.LastWinsMergeHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests common usage scenarios for the {@link MemoryRepoHandler}.
 */
public class MemoryRepoHandlerTests extends MemoryRepoHandlerTestBase<
    StringContent,
    StringHashMapArea,
    MemoryCommit,
    MemorySearchQuery,
    MemorySearchResults,
    MemoryRepo<StringContent, StringHashMapArea>,
    MemoryRepoEngine<StringContent, StringHashMapArea>,
    MemoryRepoHandler<StringContent, StringHashMapArea>
    >
{


    /**
     * Creates the specific type of handler under test.
     *
     * @return A new instance of the handler under test.
     */
    @Override protected MemoryRepoHandler<StringContent, StringHashMapArea> createNewRepoHandler()
    {
        return new MemoryRepoHandler<>(StringContent::new, StringHashMapArea::new);
    }

    @Test
    public void testCreation()
    {
        // Create the repo handler with each of the constructors:
        MemoryRepoHandler<StringContent, HashMapArea<StringContent>> repoHandler;
        repoHandler = new MemoryRepoHandler<>(StringContent::new, HashMapArea::new);
        repoHandler = new MemoryRepoHandler<>(StringContent::new, HashMapArea::new, new MemoryRepo<>());
        repoHandler = new MemoryRepoHandler<>(
            StringContent::new,
            HashMapArea::new,
            new MemoryRepo<>(),
            new HashWrapperByteArrayIndex(),
            new ClockWithVMNanos(),
            new MemoryRepoEngine<>(),
            new HashMapDifferenceHandler(),
            new HashMapComparisonHandler(),
            new LastWinsMergeHandler()
        );
    }

    /**
     * This tests that we can commit a string.
     */
    @Test
    public void testCommitString()
    {
        // Create the handler:
        MemoryRepoHandler<StringContent, StringHashMapArea> repoHandler = createNewRepoHandler();

        // Create an area where we can commit content:
        StringHashMapArea contentArea = repoHandler.createArea();

        // Add content to the area:
        contentArea.putString("Hello", "World");
        contentArea.putString("Static", "Content");

        // Commit the content:
        MemoryCommit commit1 = repoHandler.commit(contentArea, "First commit!");

        // Add and modify content:
        contentArea.putString("Hello", "Nano World");
        contentArea.putString("Info", "Details");

        // Commit again:
        MemoryCommit commit2 = repoHandler.commit(contentArea, "Second commit.");

        // Get the difference between the two commits:
        ComparisonAPI comparison = repoHandler.computeComparisonBetweenCommits(commit1, commit2);
        assertEquals(
            "/Hello : Changed\n" +
            "/Info : Added\n" +
            "/Static : Unchanged",
            comparison.asListString()
        );
    }

}
