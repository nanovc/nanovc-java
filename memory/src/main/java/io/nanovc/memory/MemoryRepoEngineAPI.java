/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory;

import io.nanovc.*;
import io.nanovc.areas.ByteArrayArea;
import io.nanovc.indexes.ByteArrayIndex;

import java.util.List;
import java.util.Set;

/**
 * The interface for the engine for working with a nano version control repository in memory.
 * A Repo Engine does not contain any state. Just the logic of how to manipulate a repo.
 * Therefore you need to pass the repo into all the calls.
 * This is good where one Repo Engine is going to be reused across many Repos.
 * A repo engine is thread safe because it is stateless.
 *
 * @param <TContent>     The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>        The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>      The specific type of commit that is created in the repo.
 * @param <TSearchQuery> The specific type of search query that this engine returns.
 * @param <TRepo>        The specific type of repo that this engine is for.
 */
public interface MemoryRepoEngineAPI<
    TContent extends Content,
    TArea extends Area<TContent>,
    TCommit extends MemoryCommitBase<TCommit>,
    TSearchQuery extends SearchQuery<TCommit>,
    TSearchResults extends SearchResults<TCommit, TSearchQuery>,
    TRepo extends MemoryRepoAPI<TContent, TArea, TCommit>
    >
    extends RepoEngine<
    TContent,
    TArea,
    TCommit,
    TSearchQuery,
    TSearchResults,
    TRepo
    >
{
    /**
     * Creates a repo that is associated with this repo engine.
     *
     * @return The Repo that has been created and is now associated with this engine.
     */
    TRepo createRepo();

    /**
     * Creates a new commit.
     *
     * @return A new commit.
     */
    TCommit createCommit();

    /**
     * Creates a new content area.
     *
     * @param areaSupplier The user specified factory method for the specific type of content area to create.
     * @return A new content area.
     */
    default TArea createArea(AreaFactory<TContent, TArea> areaSupplier)
    {
        return areaSupplier.createArea();
    }

    /**
     * A factory method for an area to use for snapshots.
     * This is used when we are committing content and the snapshot is used for the {@link MemoryCommit#snapshot}.
     *
     * @return A new empty area for snapshots.
     */
    ByteArrayArea createSnapshotArea();

    /**
     * Creates a new instance of the content with the given bytes.
     *
     * @param bytes          The bytes to use for the content.
     * @param contentFactory The user supplied content factory to use.
     * @return A new instance of the content for the given bytes.
     */
    TContent createContent(byte[] bytes, ContentFactory<TContent> contentFactory);

    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     *
     * @param contentAreaToCommit The content area to commit to the repo.
     * @param message             The commit message.
     * @param repo                The repo to commit the content area to.
     * @param byteArrayIndex      The byte array index to use when creating snap-shots for the content.
     * @param clock               The clock to use for generating the timestamp for the commit.
     * @return The commit for this content area.
     */
    TCommit commit(TArea contentAreaToCommit, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock);

    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     * It tracks the given commit as the parent.
     *
     * @param contentAreaToCommit The content area to commit to the repo.
     * @param message             The commit message.
     * @param repo                The repo to commit the content area to.
     * @param byteArrayIndex      The byte array index to use when creating snap-shots for the content.
     * @param clock               The clock to use for generating the timestamp for the commit.
     * @param parentCommit        The parent commit that we want to make this commit from.
     * @return The commit for this content area.
     */
    TCommit commit(TArea contentAreaToCommit, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock, TCommit parentCommit);

    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     * It tracks the given commit as the parent.
     *
     * @param contentAreaToCommit The content area to commit to the repo.
     * @param message             The commit message.
     * @param repo                The repo to commit the content area to.
     * @param byteArrayIndex      The byte array index to use when creating snap-shots for the content.
     * @param clock               The clock to use for generating the timestamp for the commit.
     * @param firstParentCommit   The parent commit that we want to make this commit from.
     * @param otherParentCommits  The other parents to have in addition to the first parent commit.
     * @return The commit for this content area.
     */
    TCommit commit(TArea contentAreaToCommit, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock, TCommit firstParentCommit, List<TCommit> otherParentCommits);

    /**
     * Commit the given content to the repo.
     *
     * @param contentAreaToCommit The content area to commit to the repo.
     * @param branchName          The name of the branch to commit to. The branch is created if it doesn't already exist.
     * @param message             The commit message.
     * @param repo                The repo to commit the content area to.
     * @param byteArrayIndex      The byte array index to use when creating snap-shots for the content.
     * @param clock               The clock to use for generating the timestamp for the commit.
     * @return The commit for this content area.
     */
    TCommit commitToBranch(TArea contentAreaToCommit, String branchName, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock);

    /**
     * Constructs a new commit for the given content.
     * This does not put it into the repo.
     * The calling method should decide what to do with the commit. Usually putting it into the repo.
     *
     * @param contentAreaToCommit The content area to commit to the repo.
     * @param message             The commit message.
     * @param byteArrayIndex      The byte array index to use when creating snap-shots for the content.
     * @param clock               The clock to use for generating the timestamp for the commit.
     * @return The commit for this content area.
     */
    TCommit constructCommit(TArea contentAreaToCommit, String message, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock);

    /**
     * Checks out the content for the given commit into the given content area.
     *
     * @param commit         The commit to check out.
     * @param repo           The repo to check out from.
     * @param areaToUpdate   The area to update with the content for the commit.
     * @param contentFactory The content factory to use when populating the content area.
     */
    void checkoutIntoArea(TCommit commit, TRepo repo, TArea areaToUpdate, ContentFactory<TContent> contentFactory);

    /**
     * Checks out the content for the given commit into a new content area.
     *
     * @param commit         The commit to check out.
     * @param repo           The repo to check out from.
     * @param areaFactory    The user specified factory method for the specific type of content area to create.
     * @param contentFactory The content factory to use when populating the content area.
     * @return A new content area with the content from the checkout.
     */
    TArea checkout(TCommit commit, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory);

    /**
     * Creates a new byte array index that can be used for providing Value-Equality semantics for the byte arrays we have in the repo.
     *
     * @return A new byte array index to use.
     */
    ByteArrayIndex createByteArrayIndex();

    /**
     * Creates a new clock for creating timestamps.
     *
     * @return A new clock for creating timestamps.
     */
    Clock<? extends Timestamp> createClock();

    /**
     * Gets the latest commit for the branch with the given name.
     *
     * @param branchName The name of the branch to get the latest commit for.
     * @param repo       The repo to get the commit from.
     * @return The latest commit for the given branch. Null if there is no branch with the given name.
     */
    TCommit getLatestCommitForBranch(String branchName, TRepo repo);

    /**
     * Gets the set of branch names in the repo.
     * The set is a snapshot of the names that currently exist for the repo.
     *
     * @param repo The repo to get the branch names from.
     * @return The set of branch names in the repo. If there are no branches in the repo then an empty set is returned. The set returned is unmodifiable so that you have a snapshot of the branches at this point in time.
     */
    Set<String> getBranchNames(TRepo repo);

    /**
     * Gets the set of tag names in the repo.
     * The set is a snapshot of the names that currently exist for the repo.
     *
     * @param repo The repo to get the tag names from.
     * @return The set of tag names in the repo. If there are no tags in the repo then an empty set is returned. The set returned is unmodifiable so that you have a snapshot of the tags at this point in time.
     */
    Set<String> getTagNames(TRepo repo);

    /**
     * Tags the commit with the given name.
     * This tag name can be used to reference a specific commit in the history, independently of the branches.
     *
     * @param repo    The repo to tag the commit in.
     * @param commit  The commit to tag with a name.
     * @param tagName The name of the tag to give to this commit.
     */
    void tagCommit(TRepo repo, TCommit commit, String tagName);

    /**
     * Gets the commit with the given tag name.
     *
     * @param repo    The repo to get the tagged commit from.
     * @param tagName The name of the tagged commit.
     * @return The commit with the given tag name. Null if there is no tag with this name.
     */
    TCommit getCommitForTag(TRepo repo, String tagName);

    /**
     * Removes the tag with the given name from the repo.
     *
     * @param repo    The repo to remove the tag from.
     * @param tagName The name of the tag to remove. If this tag doesn't exist then nothing happens.
     */
    void removeTag(TRepo repo, String tagName);

    /**
     * Optimizes the timestamps for the repo.
     * This process might re-base epochs and timestamps to better reuse epochs.
     *
     * @param repo The repo to optimize the timestamps in.
     */
    void optimizeTimestamps(TRepo repo);

    /**
     * Computes a difference between the given areas.
     * The areas could have come from anywhere.
     *
     * @param fromArea          The first area to find differences from.
     * @param toArea            The second area to find differences to.
     * @param differenceHandler The handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     * @return The differences between the given areas.
     */
    Difference computeDifferenceBetweenAreas(Area<? extends TContent> fromArea, Area<? extends TContent> toArea, DifferenceHandler<? extends DifferenceEngine> differenceHandler);

    /**
     * Computes a difference between the given commits.
     * It is assumed that the commits come from this repo.
     *
     * @param fromCommit        The first commit to find differences from.
     * @param toCommit          The second commit to find differences to.
     * @param differenceHandler The handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     * @param repo              The repo to check out from.
     * @param areaFactory       The user specified factory method for the specific type of content area to create.
     * @param contentFactory    The content factory to use when populating the content area.
     * @return The differences between the given commits.
     */
    Difference computeDifferenceBetweenCommits(TCommit fromCommit, TCommit toCommit, DifferenceHandler<? extends DifferenceEngine> differenceHandler, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory);

    /**
     * Computes a difference between the given branches.
     *
     * @param fromBranchName    The first branch to find differences from.
     * @param toBranchName      The second branch to find differences to.
     * @param differenceHandler The handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     * @param repo              The repo to check out from.
     * @param areaFactory       The user specified factory method for the specific type of content area to create.
     * @param contentFactory    The content factory to use when populating the content area.
     * @return The differences between the given branches.
     */
    Difference computeDifferenceBetweenBranches(String fromBranchName, String toBranchName, DifferenceHandler<? extends DifferenceEngine> differenceHandler, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory);

    /**
     * Computes a comparison between the given areas.
     * The areas could have come from anywhere.
     *
     * @param fromArea          The first area to find comparisons from.
     * @param toArea            The second area to find comparisons to.
     * @param comparisonHandler The handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     * @return The comparisons between the given areas.
     */
    Comparison computeComparisonBetweenAreas(Area<? extends TContent> fromArea, Area<? extends TContent> toArea, ComparisonHandler<? extends ComparisonEngine> comparisonHandler);

    /**
     * Computes a comparison between the given commits.
     * It is assumed that the commits come from this repo.
     *
     * @param fromCommit        The first commit to find comparisons from.
     * @param toCommit          The second commit to find comparisons to.
     * @param comparisonHandler The handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     * @param repo              The repo to check out from.
     * @param areaFactory       The user specified factory method for the specific type of content area to create.
     * @param contentFactory    The content factory to use when populating the content area.
     * @return The comparisons between the given commits.
     */
    Comparison computeComparisonBetweenCommits(TCommit fromCommit, TCommit toCommit, ComparisonHandler<? extends ComparisonEngine> comparisonHandler, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory);

    /**
     * Computes a comparison between the given branches.
     *
     * @param fromBranchName    The first branch to find comparisons from.
     * @param toBranchName      The second branch to find comparisons to.
     * @param comparisonHandler The handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     * @param repo              The repo to check out from.
     * @param areaFactory       The user specified factory method for the specific type of content area to create.
     * @param contentFactory    The content factory to use when populating the content area.
     * @return The comparisons between the given branches.
     */
    Comparison computeComparisonBetweenBranches(String fromBranchName, String toBranchName, ComparisonHandler<? extends ComparisonEngine> comparisonHandler, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory);

    /**
     * A factory method to create the specific search query to use.
     *
     * @param searchQueryDefinition The definition of the search query that is being created.
     * @return A new search query.
     */
    TSearchQuery createSearchQuery(SearchQueryDefinition searchQueryDefinition);

    /**
     * Prepares a reusable search query from the given search definition.
     * This search query can be thought of as the compiled/prepared search query.
     * The same search query can be run for multiple repo's without needing to recompute the search query each time.
     *
     * @param searchQueryDefinition The definition of the search to perform.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    TSearchQuery prepareSearchQuery(SearchQueryDefinition searchQueryDefinition);

    /**
     * A factory method to create the specific search results that are needed.
     *
     * @param searchQuery The search query to reuse for this search.
     * @return New search results.
     */
    TSearchResults createSearchResults(TSearchQuery searchQuery);

    /**
     * Searches for commits that match the given search query.
     * Use this when you want to reuse the search query
     *
     * @param searchQuery        The search query to reuse for this search.
     * @param overrideParameters Parameters to override the defaults of the search query with. Pass null to use the parameters in the search query.
     * @param repo               The repo to search in.
     * @param areaFactory        The user specified factory method for the specific type of content area to create.
     * @param contentFactory     The content factory to use when populating the content area.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    TSearchResults searchWithQuery(TSearchQuery searchQuery, SearchParameters overrideParameters, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory);

    /**
     * Merges one branch into another.
     * The merge handler is used to resolve any merge conflicts if there are any.
     *
     * @param destinationBranchName The branch that we should merge into.
     * @param sourceBranchName      The branch that we should merge from.
     * @param message               The commit message to use for the merge.
     * @param mergeHandler          The handler to use for dealing with the merge logic.
     * @param comparisonHandler     The handler to use for comparing content between content areas.
     * @param differenceHandler     The handler to use for finding differences between content areas.
     * @param repo                  The repo that we are working on.
     * @param areaFactory           The factory to use for creating content areas for the repo.
     * @param contentFactory        The factory to use for extracting content from the areas.
     * @param byteArrayIndex        The byte array index to use when creating snap-shots for the content.
     * @param clock                 The clock to use for generating the timestamp for the commit.
     * @return The commit that was performed for the merge.
     */
    TCommit mergeIntoBranchFromAnotherBranch(String destinationBranchName, String sourceBranchName, String message, MergeHandler<? extends MergeEngine> mergeHandler, ComparisonHandler<? extends ComparisonEngine> comparisonHandler, DifferenceHandler<? extends DifferenceEngine> differenceHandler, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock);

    /**
     * Creates a new branch with the given name and makes it point at the given commit.
     * If the repo already has a branch with this name then it is updated to point at this commit.
     * @param commit The commit where the new branch should be created.
     * @param branchName The name of the branch to create at the commit.
     * @param repo       The repo to update with the new branch.
     */
    void createBranchAtCommit(TCommit commit, String branchName, TRepo repo);

    /**
     * Casts or clones the given area to the specific type required by this repo handler.
     *
     * @param areaToCastOrClone The area to cast if it is already the required type or to clone if it is a different area type.
     * @param areaFactory       The factory method to use for creating the specific area that we want.
     * @param contentFactory    The factory method to use for creating the specific content that we want.
     * @param byteArrayIndex    The byte array index to use that allows us to re-use byte arrays that we have seen before in the repo.
     * @return A compatible area for the repo handler which is either a cast of the same instance or a completely new clone of it if it is an incompatible type.
     */
    TArea castOrCloneArea(Area<? extends Content> areaToCastOrClone, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory, ByteArrayIndex byteArrayIndex);
}
