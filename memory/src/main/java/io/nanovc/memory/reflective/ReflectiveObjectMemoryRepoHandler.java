/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.reflective;

import io.nanovc.*;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.areas.HashMapArea;
import io.nanovc.content.ByteArrayContent;
import io.nanovc.content.StringContent;
import io.nanovc.indexes.ByteArrayIndex;
import io.nanovc.memory.*;

/**
 * The repo handler for working with {@link MemoryRepoBase}'s which uses a {@link HashMapArea} and {@link StringContent}.
 * This represents the public API when working with {@link MemoryRepo}'s.
 * It holds common state including the {@link MemoryRepo} being worked on and the {@link MemoryRepoEngine} that contains the specific algorithm that we are interested in when working with the repo.
 * You can swap out the repo that is being worked on in cases where a correctly configured repo handler must work on multiple repo's.
 * The core functionality is delegated to the {@link MemoryRepoEngine} which is stateless and can be reused for multiple {@link MemoryRepo}'s and {@link za.co.synthesis.nanovc.memory.reflective.ReflectiveObjectMemoryRepoHandler}'s.
 */
public class ReflectiveObjectMemoryRepoHandler
    extends ReflectiveObjectMemoryRepoHandlerBase<
    ByteArrayContent,
    ByteArrayHashMapArea,
    MemoryCommit,
    MemorySearchQuery,
    MemorySearchResults,
    ReflectiveObjectMemoryRepo,
    ReflectiveObjectMemoryRepoEngine
    >
{

    /**
     * A constructor that initialises the MemoryRepoHandler with the specified repo and repoEngine.
     *
     * @param repo              The repo to manage.
     * @param byteArrayIndex    The index to use for managing byte arrays in the in-memory repo. Pass null to create a new default index.
     * @param clock             The clock to use for creating timestamps.
     * @param repoEngine        The repo engine to use internally.
     * @param differenceHandler The handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     * @param comparisonHandler The handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     * @param mergeHandler      The handler to use for merging commits.
     */
    public ReflectiveObjectMemoryRepoHandler(
        ReflectiveObjectMemoryRepo repo,
        ByteArrayIndex byteArrayIndex,
        Clock<? extends Timestamp> clock,
        ReflectiveObjectMemoryRepoEngine repoEngine,
        DifferenceHandler<? extends DifferenceEngine> differenceHandler,
        ComparisonHandler<? extends ComparisonEngine> comparisonHandler,
        MergeHandler<? extends MergeEngine> mergeHandler
    )
    {
        super(ByteArrayContent::new, ByteArrayHashMapArea::new, repo, byteArrayIndex, clock, repoEngine, differenceHandler, comparisonHandler, mergeHandler);
    }

    /**
     * A constructor that initialises the MemoryRepoHandler.
     * The repo is set to the one provided and a default repo engine is created.
     *
     * @param repo The repo to manage.
     */
    public ReflectiveObjectMemoryRepoHandler(ReflectiveObjectMemoryRepo repo)
    {
        super(ByteArrayContent::new, ByteArrayHashMapArea::new, repo, null, null, null, null, null, null);
    }

    /**
     * A constructor that initialises the MemoryRepoHandler.
     * The repo is set to the one provided and a default repo engine is created.
     */
    public ReflectiveObjectMemoryRepoHandler()
    {
        super(ByteArrayContent::new, ByteArrayHashMapArea::new, null, null, null, null, null, null, null);
    }

    /**
     * A factory method to create a default engine if one was not provided.
     *
     * @return The engine to use if one was not provided.
     */
    @Override
    protected ReflectiveObjectMemoryRepoEngine createDefaultEngine()
    {
        return new ReflectiveObjectMemoryRepoEngine();
    }
}
