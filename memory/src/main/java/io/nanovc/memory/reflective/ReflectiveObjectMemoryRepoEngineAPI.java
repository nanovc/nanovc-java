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
import io.nanovc.indexes.ByteArrayIndex;
import io.nanovc.memory.MemoryCommitBase;
import io.nanovc.memory.MemoryRepoEngineAPI;
import io.nanovc.memory.MemorySearchQueryBase;
import io.nanovc.memory.MemorySearchResultsBase;

import java.util.List;

/**
 * The interface for the engine for working with a nano version control repository of strings in memory.
 * A Repo Engine does not contain any state. Just the logic of how to manipulate a repo.
 * Therefore you need to pass the repo into all the calls.
 * This is good where one Repo Engine is going to be reused across many Repos.
 * A repo engine is thread safe because it is stateless.
 *
 * @param <TContent> The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>    The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>  The specific type of commit that is created in the repo.
 * @param <TRepo>    The specific type of repo that this engine is for.
 */
public interface ReflectiveObjectMemoryRepoEngineAPI<
    TContent extends Content,
    TArea extends Area<TContent>,
    TCommit extends MemoryCommitBase<TCommit>,
    TSearchQuery extends MemorySearchQueryBase<TCommit>,
    TSearchResults extends MemorySearchResultsBase<TCommit, TSearchQuery>,
    TRepo extends ReflectiveObjectMemoryRepoAPI<TContent, TArea, TCommit>
    >
    extends MemoryRepoEngineAPI<
    TContent,
    TArea,
    TCommit,
    TSearchQuery,
    TSearchResults,
    TRepo
    >
{
    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     *
     * @param object         The object to commit to the repo.
     * @param message        The commit message.
     * @param repo           The repo to commit the content area to.
     * @param byteArrayIndex The byte array index to use when creating snap-shots for the content.
     * @param clock          The clock to use for generating the timestamp for the commit.
     * @param areaFactory    The user specified factory method for the specific type of content area to create.
     * @param contentFactory The content factory to use when populating the content area.
     * @return The commit for this content area.
     */
    TCommit commitObject(Object object, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory);

    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     * It tracks the given commit as the parent.
     *
     * @param object         The object to commit to the repo.
     * @param message        The commit message.
     * @param repo           The repo to commit the content area to.
     * @param byteArrayIndex The byte array index to use when creating snap-shots for the content.
     * @param clock          The clock to use for generating the timestamp for the commit.
     * @param areaFactory    The user specified factory method for the specific type of content area to create.
     * @param contentFactory The content factory to use when populating the content area.
     * @param parentCommit   The parent commit that we want to make this commit from.
     * @return The commit for this content area.
     */
    TCommit commitObject(Object object, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory, TCommit parentCommit);

    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     * It tracks the given commit as the parent.
     *
     * @param object             The object to commit to the repo.
     * @param message            The commit message.
     * @param repo               The repo to commit the content area to.
     * @param byteArrayIndex     The byte array index to use when creating snap-shots for the content.
     * @param clock              The clock to use for generating the timestamp for the commit.
     * @param areaFactory        The user specified factory method for the specific type of content area to create.
     * @param contentFactory     The content factory to use when populating the content area.
     * @param firstParentCommit  The parent commit that we want to make this commit from.
     * @param otherParentCommits The other parents to have in addition to the first parent commit.
     * @return The commit for this content area.
     */
    TCommit commitObject(Object object, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory, TCommit firstParentCommit, List<TCommit> otherParentCommits);

    /**
     * Commit the given content to the repo.
     *
     * @param object         The object to commit to the repo.
     * @param branchName     The name of the branch to commit to. The branch is created if it doesn't already exist.
     * @param message        The commit message.
     * @param repo           The repo to commit the content area to.
     * @param byteArrayIndex The byte array index to use when creating snap-shots for the content.
     * @param clock          The clock to use for generating the timestamp for the commit.
     * @param areaFactory    The user specified factory method for the specific type of content area to create.
     * @param contentFactory The content factory to use when populating the content area.
     * @return The commit for this content area.
     */
    TCommit commitObjectToBranch(Object object, String branchName, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory);

    /**
     * Checks out the object for the given commit.
     *
     * @param commit         The commit to check out.
     * @param repo           The repo to check out from.
     * @param areaFactory    The user specified factory method for the specific type of content area to create.
     * @param contentFactory The content factory to use when populating the content area.
     * @return A new object with the content from the checkout.
     */
    Object checkoutObject(TCommit commit, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory);
}
