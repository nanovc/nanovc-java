/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.bytes;

import io.nanovc.*;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.areas.HashMapArea;
import io.nanovc.content.ByteArrayContent;
import io.nanovc.content.StringContent;
import io.nanovc.memory.*;

/**
 * The repo handler for working with {@link MemoryRepoBase}'s which uses a {@link HashMapArea} and {@link StringContent}.
 * This represents the public API when working with {@link MemoryRepo}'s.
 * It holds common state including the {@link MemoryRepo} being worked on and the {@link MemoryRepoEngine} that contains the specific algorithm that we are interested in when working with the repo.
 * You can swap out the repo that is being worked on in cases where a correctly configured repo handler must work on multiple repo's.
 * The core functionality is delegated to the {@link MemoryRepoEngine} which is stateless and can be reused for multiple {@link MemoryRepo}'s and {@link ByteArrayMemoryRepoHandler}'s.
 */
public class ByteArrayMemoryRepoHandler
    extends ByteArrayMemoryRepoHandlerBase<
    ByteArrayContent,
    ByteArrayHashMapArea,
    MemoryCommit,
    MemorySearchQuery,
    MemorySearchResults,
    ByteArrayMemoryRepo,
    ByteArrayMemoryRepoEngine
    >
{

    /**
     * A constructor that initialises the MemoryRepoHandler with the specified repo and repoEngine.
     *
     * @param stringMemoryRepo  The repo to manage.
     * @param byteArrayIndex    The index to use for managing byte arrays in the in-memory repo. Pass null to create a new default index.
     * @param clock             The clock to use for creating timestamps.
     * @param repoEngine        The repo engine to use internally.
     * @param differenceHandler The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @param comparisonHandler The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @param mergeHandler      The handler to use for merging commits.
     */
    public ByteArrayMemoryRepoHandler(
        ByteArrayMemoryRepo stringMemoryRepo,
        ByteArrayIndex byteArrayIndex,
        ClockBase<? extends TimestampBase> clock,
        ByteArrayMemoryRepoEngine repoEngine,
        DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler,
        ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler,
        MergeHandlerAPI<? extends MergeEngineAPI> mergeHandler
    )
    {
        super(ByteArrayContent::new, ByteArrayHashMapArea::new, stringMemoryRepo, byteArrayIndex, clock, repoEngine, differenceHandler, comparisonHandler, mergeHandler);
    }

    /**
     * A constructor that initialises the MemoryRepoHandler.
     * The repo is set to the one provided and a default repo engine is created.
     *
     * @param stringMemoryRepo The repo to manage.
     */
    public ByteArrayMemoryRepoHandler(ByteArrayMemoryRepo stringMemoryRepo)
    {
        super(ByteArrayContent::new, ByteArrayHashMapArea::new, stringMemoryRepo, null, null, null, null, null, null);
    }

    /**
     * A constructor that initialises the MemoryRepoHandler.
     * The repo is set to the one provided and a default repo engine is created.
     */
    public ByteArrayMemoryRepoHandler()
    {
        super(ByteArrayContent::new, ByteArrayHashMapArea::new, null, null, null, null, null, null, null);
    }

    /**
     * A factory method to create a default engine if one was not provided.
     *
     * @return The engine to use if one was not provided.
     */
    @Override
    protected ByteArrayMemoryRepoEngine createDefaultEngine()
    {
        return new ByteArrayMemoryRepoEngine();
    }
}
