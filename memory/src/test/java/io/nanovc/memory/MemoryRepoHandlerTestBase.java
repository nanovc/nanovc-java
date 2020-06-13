/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.Area;
import io.nanovc.Content;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The base class for common tests for {@link MemoryRepoHandler}'s.
 * Sub classes will inherit this test.
 *
 * @param <TContent> The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>    The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>  The specific type of commit that is created in the repo.
 * @param <TRepo>    The specific type of repo to test.
 * @param <TEngine>  The specific type of engine to test.
 * @param <THandler> The specific type of handler to test.
 */
public abstract class MemoryRepoHandlerTestBase<
    TContent extends Content,
    TArea extends Area<TContent>,
    TCommit extends MemoryCommitBase<TCommit>,
    TSearchQuery extends MemorySearchQueryBase<TCommit>,
    TSearchResults extends MemorySearchResultsBase<TCommit, TSearchQuery>,
    TRepo extends MemoryRepoBase<TContent, TArea, TCommit>,
    TEngine extends MemoryRepoEngineBase<TContent, TArea, TCommit, TSearchQuery, TSearchResults, TRepo>,
    THandler extends MemoryRepoHandlerBase<TContent, TArea, TCommit, TSearchQuery, TSearchResults, TRepo, TEngine>
    >
    extends MemoryNanoVersionControlTestsBase
{

    /**
     * Creates the specific type of handler under test.
     *
     * @return A new instance of the handler under test.
     */
    protected abstract THandler createNewRepoHandler();

    /**
     * Tests that the repo handler can be created.
     */
    @Test
    public void testBasicCreation()
    {
        THandler repoHandler = createNewRepoHandler();
        assertNotNull(repoHandler);
    }

}
