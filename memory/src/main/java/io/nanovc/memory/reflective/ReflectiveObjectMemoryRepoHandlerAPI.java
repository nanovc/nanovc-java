/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.reflective;

import io.nanovc.AreaAPI;
import io.nanovc.CommitTags;
import io.nanovc.ContentAPI;
import io.nanovc.areas.HashMapArea;
import io.nanovc.areas.StringAreaAPI;
import io.nanovc.content.StringContent;
import io.nanovc.memory.*;

import java.util.List;

/**
 * The repo handler for working with {@link MemoryRepoBase}'s which uses a {@link HashMapArea} and {@link StringContent}.
 * This represents the public API when working with {@link MemoryRepo}'s.
 * It holds common state including the {@link MemoryRepo} being worked on and the {@link MemoryRepoEngine} that contains the specific algorithm that we are interested in when working with the repo.
 * You can swap out the repo that is being worked on in cases where a correctly configured repo handler must work on multiple repo's.
 * The core functionality is delegated to the {@link MemoryRepoEngine} which is stateless and can be reused for multiple {@link MemoryRepo}'s and {@link ReflectiveObjectMemoryRepoHandlerAPI}'s.
 *
 * @param <TContent> The specific type of content that is stored in area for each commit in the repo.
 */
public interface ReflectiveObjectMemoryRepoHandlerAPI<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends MemoryCommitAPI<TCommit>,
    TSearchQuery extends MemorySearchQueryAPI<TCommit>,
    TSearchResults extends MemorySearchResultsAPI<TCommit, TSearchQuery>,
    TRepo extends ReflectiveObjectMemoryRepoAPI<TContent, TArea, TCommit>,
    TEngine extends ReflectiveObjectMemoryRepoEngineAPI<TContent, TArea, TCommit, TSearchQuery, TSearchResults, TRepo>
    >
    extends MemoryRepoHandlerAPI<
    TContent,
    TArea,
    TCommit,
    TSearchQuery,
    TSearchResults,
    TRepo,
    TEngine
    >
{
    /**
     * Commit the given object to the repo.
     *
     * @param objectToCommit The object to commit to the repo.
     * @param message        The commit message.
     * @param commitTags     The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @return The commit for this content.
     */
    TCommit commitObject(Object objectToCommit, String message, StringAreaAPI commitTags);

    /**
     * Commit the given object to the repo.
     * It tracks the given commit as the parent.
     *
     * @param objectToCommit The object to commit to the repo.
     * @param message        The commit message.
     * @param commitTags     The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @param parentCommit   The parent commit that we want to make this commit from.
     * @return The commit for this content.
     */
    TCommit commitObject(Object objectToCommit, String message, StringAreaAPI commitTags, TCommit parentCommit);

    /**
     * Commit the given object to the repo.
     * It tracks the given commits as the parents.
     *
     * @param objectToCommit     The object to commit to the repo.
     * @param message            The commit message.
     * @param commitTags         The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @param firstParentCommit  The parent commit that we want to make this commit from.
     * @param otherParentCommits The other parents to have in addition to the first parent commit.
     * @return The commit for this content area.
     */
    TCommit commitObject(Object objectToCommit, String message, StringAreaAPI commitTags, TCommit firstParentCommit, TCommit... otherParentCommits);

    /**
     * Commit the given object to the repo.
     * It tracks the given commits as the parents.
     *
     * @param objectToCommit     The object to commit to the repo.
     * @param message            The commit message.
     * @param commitTags         The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @param firstParentCommit  The parent commit that we want to make this commit from.
     * @param otherParentCommits The other parents to have in addition to the first parent commit.
     * @return The commit for this content area.
     */
    TCommit commitObject(Object objectToCommit, String message, StringAreaAPI commitTags, TCommit firstParentCommit, List<TCommit> otherParentCommits);

    /**
     * Commit the given object to the given branch in the the repo.
     *
     * @param objectToCommit The object to commit to the repo.
     * @param branch         The branch to commit to. If the branch doesn't exist, it is created.
     * @param message        The commit message.
     * @param commitTags     The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @return The commit for this content.
     */
    TCommit commitObjectToBranch(Object objectToCommit, String branch, String message, StringAreaAPI commitTags);

    /**
     * Checks out the object for the given commit.
     *
     * @param commit The commit to check out.
     * @return A new object of the expected type from the checkout.
     */
    Object checkoutObject(TCommit commit);
}
