/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.CommitTags;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.content.StringContent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests merging of branches and commits for the {@link MemoryRepoHandler}.
 */
public class MemoryRepoHandlerTests_Merging extends MemoryRepoHandlerTestBase<
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
    public void testMergingTwoBranches_RootCommits()
    {
        // Create the handler:
        MemoryRepoHandler<StringContent, StringHashMapArea> repoHandler = createNewRepoHandler();

        // Create the first area to commit:
        StringHashMapArea contentArea1 = repoHandler.createArea();
        contentArea1.putString("A1", "123");
        MemoryCommit commit1 = repoHandler.commitToBranch(contentArea1, "1", "First Branch", CommitTags.none());

        // Create the second area to commit:
        StringHashMapArea contentArea2 = repoHandler.createArea();
        contentArea2.putString("B2", "abc");
        MemoryCommit commit2 = repoHandler.commitToBranch(contentArea2, "2", "Second Branch", CommitTags.none());

        // Merge the two branches:
        MemoryCommit mergeCommit = repoHandler.mergeIntoBranchFromAnotherBranch("1", "2", "Merge 2 into 1", CommitTags.none());

        // Make sure that the merge looks as expected:
        StringHashMapArea mergeArea = repoHandler.checkout(mergeCommit);
        String expected =
            "/A1 : '123'\n" +
            "/B2 : 'abc'";
        assertEquals(expected, mergeArea.asListString());
    }

    @Test
    public void testMergingTwoRootBranches_CommonAncestor()
    {
        // Create the handler:
        MemoryRepoHandler<StringContent, StringHashMapArea> repoHandler = createNewRepoHandler();

        // Create the common ancestor for both commits:
        StringHashMapArea contentArea0 = repoHandler.createArea();
        contentArea0.putString("0", "0");
        MemoryCommit commit0 = repoHandler.commit(contentArea0, "Common Ancestor", CommitTags.none());

        // Create the first area to commit:
        StringHashMapArea contentArea1 = repoHandler.checkout(commit0);
        contentArea1.putString("A1", "123");
        repoHandler.createBranchAtCommit(commit0, "1");
        MemoryCommit commit1 = repoHandler.commitToBranch(contentArea1, "1", "First Branch", CommitTags.none());

        // Create the second area to commit:
        StringHashMapArea contentArea2 = repoHandler.checkout(commit0);
        contentArea2.putString("B2", "abc");
        repoHandler.createBranchAtCommit(commit0, "2");
        MemoryCommit commit2 = repoHandler.commitToBranch(contentArea2, "2", "Second Branch", CommitTags.none());

        // Merge the two branches:
        MemoryCommit mergeCommit = repoHandler.mergeIntoBranchFromAnotherBranch("1", "2", "Merge 2 into 1", CommitTags.none());

        // Make sure that the merge looks as expected:
        StringHashMapArea mergeArea = repoHandler.checkout(mergeCommit);
        String expected =
            "/0 : '0'\n" +
            "/A1 : '123'\n" +
            "/B2 : 'abc'";
        assertEquals(expected, mergeArea.asListString());
    }

}
