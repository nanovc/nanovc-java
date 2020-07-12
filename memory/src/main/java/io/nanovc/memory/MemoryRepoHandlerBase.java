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
import io.nanovc.areas.StringAreaAPI;
import io.nanovc.comparisons.HashMapComparisonHandler;
import io.nanovc.differences.HashMapDifferenceHandler;
import io.nanovc.merges.LastWinsMergeHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * The repo handler for working with {@link MemoryRepoBase}'s.
 * This represents the public API when working with {@link RepoAPI}'s.
 * It holds common state including the {@link RepoAPI} being worked on and the {@link RepoEngineAPI} that contains the specific algorithm that we are interested in when working with the repo.
 * You can swap out the repo that is being worked on in cases where a correctly configured repo handler must work on multiple repo's.
 * The core functionality is delegated to the {@link RepoEngineAPI} which is stateless and can be reused for multiple {@link RepoAPI}'s and {@link RepoHandlerAPI}'s.
 *
 * @param <TContent>       The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>          The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>        The specific type of commit that is created in the repo.
 * @param <TSearchQuery>   The specific type of search query that this engine returns.
 * @param <TSearchResults> The specific type of search results that we expect to get.
 * @param <TRepo>          The specific type of repo that this handler manages.
 * @param <TEngine>        The specific type of engine that manipulates the repo.
 */
public abstract class MemoryRepoHandlerBase<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends MemoryCommitAPI<TCommit>,
    TSearchQuery extends MemorySearchQueryAPI<TCommit>,
    TSearchResults extends MemorySearchResultsAPI<TCommit, TSearchQuery>,
    TRepo extends MemoryRepoAPI<TContent, TArea, TCommit>,
    TEngine extends MemoryRepoEngineAPI<TContent, TArea, TCommit, TSearchQuery, TSearchResults, TRepo>
    >
    extends RepoHandlerBase<
    TContent,
    TArea,
    TCommit,
    TSearchQuery,
    TSearchResults,
    TRepo,
    TEngine
    >
    implements MemoryRepoHandlerAPI<
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
     * The user specified factory method for the specific type of content to create.
     */
    public ContentFactory<TContent> contentFactory;

    /**
     * The user specified factory method for the specific type of content area to create.
     */
    public AreaFactory<TContent, TArea> areaFactory;

    /**
     * The byte array index to use for managing the in-memory byte arrays that get created in a repo.
     * This index allows us to re-use arrays in memory.
     * The index gives us Value-Equality semantics for byte[] lookups.
     */
    public ByteArrayIndex byteArrayIndex;

    /**
     * A constructor that initialises the MemoryRepoHandler with the specified repo and repoEngine.
     *
     * @param contentFactory    The user specified factory method for the specific type of content to create.
     * @param areaFactory       The user specified factory method for the specific type of content area to create.
     * @param repo              The repo to manage. Pass null to create a new repo.
     * @param byteArrayIndex    The index to use for managing byte arrays in the in-memory repo. Pass null to create a new default index.
     * @param clock             The clock to use for creating timestamps.
     * @param repoEngine        The repo engine to use internally. Pass null to create a new default engine.
     * @param differenceHandler The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @param comparisonHandler The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @param mergeHandler      The handler to use for merging commits.
     */
    public MemoryRepoHandlerBase(
        ContentFactory<TContent> contentFactory,
        AreaFactory<TContent, TArea> areaFactory,
        TRepo repo,
        ByteArrayIndex byteArrayIndex,
        ClockAPI<? extends TimestampAPI> clock,
        TEngine repoEngine,
        DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler,
        ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler,
        MergeHandlerAPI<? extends MergeEngineAPI> mergeHandler
    )
    {
        super(repo, repoEngine, clock, differenceHandler, comparisonHandler, mergeHandler);
        this.contentFactory = contentFactory;
        this.areaFactory = areaFactory;
        this.byteArrayIndex = byteArrayIndex;

        // Make sure the dependencies for this handler exist:
        ensureDependenciesExist();
    }

    /**
     * This makes sure that all the dependencies for this handler exist.
     * By default it will ensure that the following are created if they were not specified:
     * * {@link #engine}
     * * {@link #repo}
     * * {@link #byteArrayIndex}
     * * {@link #clock}
     */
    private void ensureDependenciesExist()
    {
        // Make sure we have a repo engine:
        if (this.engine == null)
        {
            // Create the default engine:
            this.engine = this.createDefaultEngine();
        }

        // Make sure we have a repo:
        if (this.repo == null)
        {
            // Create a new repo:
            this.repo = engine.createRepo();
        }

        // Make sure we have a byte array index:
        if (this.byteArrayIndex == null)
        {
            // Create a new byte array index:
            this.byteArrayIndex = engine.createByteArrayIndex();
        }

        // Make sure we have a clock:
        if (this.clock == null)
        {
            // Create a new clock:
            this.clock = engine.createClock();
        }

        // Make sure that we have the difference handler:
        if (this.differenceHandler == null)
        {
            // Create the new difference handler:
            this.differenceHandler = new HashMapDifferenceHandler();
        }

        // Make sure that we have the comparison handler:
        if (this.comparisonHandler == null)
        {
            // Create the new comparison handler:
            this.comparisonHandler = new HashMapComparisonHandler();
        }

        // Make sure that we have a merge handler:
        if (this.mergeHandler == null)
        {
            // Create a new merge handler:
            this.mergeHandler = new LastWinsMergeHandler();
        }

        // Initialise the repo as well:
        this.initRepo(this.repo);
    }

    /**
     * Gets the commit with the given tag name.
     *
     * @param tagName The name of the tagged commit.
     * @return The commit with the given tag name. Null if there is no tag with this name.
     */
    @Override
    public TCommit getCommitForTag(String tagName)
    {
        return this.engine.getCommitForTag(this.repo, tagName);
    }

    /**
     * Removes the tag with the given name from the repo.
     *
     * @param tagName The name of the tag to remove. If this tag doesn't exist then nothing happens.
     */
    @Override
    public void removeTag(String tagName)
    {
        this.engine.removeTag(this.repo, tagName);
    }

    /**
     * Tags the commit with the given name.
     * This tag name can be used to reference a specific commit in the history, independently of the branches.
     *
     * @param commit  The commit to tag with a name.
     * @param tagName The name of the tag to give to this commit.
     */
    @Override
    public void tagCommit(TCommit commit, String tagName)
    {
        this.engine.tagCommit(this.repo, commit, tagName);
    }

    /**
     * Initialise the repository before it gets used for other functionality.
     * Sub classes should provide specific initialisation logic.
     *
     * @param repo The repository to initialise.
     */
    protected void initRepo(TRepo repo)
    {
        // Do nothing.
    }

    /**
     * A factory method to create a default engine if one was not provided.
     *
     * @return The engine to use if one was not provided.
     */
    protected abstract TEngine createDefaultEngine();

    /**
     * Creates a new area where content can be placed.
     *
     * @return A new content area that can be used for committing.
     */
    @Override
    public TArea createArea()
    {
        return this.engine.createArea(this.areaFactory);
    }

    /**
     * Commit the given content to the repo.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @param commitTags          The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @return The commit for this content.
     */
    @Override
    public TCommit commit(TArea contentAreaToCommit, String message, StringAreaAPI commitTags)
    {
        return this.engine.commit(contentAreaToCommit, message, commitTags, this.repo, this.byteArrayIndex, this.clock);
    }

    /**
     * Commit the given content to the repo.
     * It tracks the given commit as the parent.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @param commitTags          The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @param parentCommit        The parent commit that we want to make this commit from.
     * @return The commit for this content.
     */
    @Override
    public TCommit commit(TArea contentAreaToCommit, String message, StringAreaAPI commitTags, TCommit parentCommit)
    {
        return this.engine.commit(contentAreaToCommit, message, commitTags, this.repo, this.byteArrayIndex, this.clock, parentCommit);
    }

    /**
     * Commit the given content to the repo.
     * It tracks the given commits as the parents.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @param commitTags          The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @param firstParentCommit   The parent commit that we want to make this commit from.
     * @param otherParentCommits  The other parents to have in addition to the first parent commit.
     * @return The commit for this content area.
     */
    @Override
    public TCommit commit(TArea contentAreaToCommit, String message, StringAreaAPI commitTags, TCommit firstParentCommit, TCommit... otherParentCommits)
    {
        return this.engine.commit(contentAreaToCommit, message, commitTags, this.repo, this.byteArrayIndex, this.clock, firstParentCommit, Arrays.asList(otherParentCommits));
    }

    /**
     * Commit the given content to the repo.
     * It tracks the given commits as the parents.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @param commitTags          The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @param firstParentCommit   The parent commit that we want to make this commit from.
     * @param otherParentCommits  The other parents to have in addition to the first parent commit.
     * @return The commit for this content area.
     */
    @Override
    public TCommit commit(TArea contentAreaToCommit, String message, StringAreaAPI commitTags, TCommit firstParentCommit, List<TCommit> otherParentCommits)
    {
        return this.engine.commit(contentAreaToCommit, message, commitTags, this.repo, this.byteArrayIndex, this.clock, firstParentCommit, otherParentCommits);
    }

    /**
     * Commit the given content to the repo.
     * It tracks the given commits as the parents.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @param commitTags          The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @param parentCommits       The parents of this commit. Consider using the other overloads when there is are one or a few parent commits.
     * @return The commit for this content area.
     */
    @Override public TCommit commit(TArea contentAreaToCommit, String message, StringAreaAPI commitTags, List<TCommit> parentCommits)
    {
        // Determine how many parent commits there are to decide how to route this to the engine:
        if (parentCommits == null)
        {
            // There is no list of parent commits.
            return this.engine.commit(contentAreaToCommit, message, commitTags, this.repo, this.byteArrayIndex, this.clock);
        }
        else
        {
            // There is a list of parent commits.
            // Determine how to pass the list to the engine as efficiently as possible:
            switch (parentCommits.size())
            {
                case 0:
                    return this.engine.commit(contentAreaToCommit, message, commitTags, this.repo, this.byteArrayIndex, this.clock);
                case 1:
                    return this.engine.commit(contentAreaToCommit, message, commitTags, this.repo, this.byteArrayIndex, this.clock, parentCommits.get(0));
                default:
                    return this.engine.commit(contentAreaToCommit, message, commitTags, this.repo, this.byteArrayIndex, this.clock, parentCommits.get(0), parentCommits.subList(1, parentCommits.size()));
            }
        }
    }

    /**
     * Commit the given content to the given branch in the the repo.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param branch              The branch to commit to. If the branch doesn't exist, it is created.
     * @param message             The commit message.
     * @param commitTags          The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @return The commit for this content.
     */
    @Override
    public TCommit commitToBranch(TArea contentAreaToCommit, String branch, String message, StringAreaAPI commitTags)
    {
        return this.engine.commitToBranch(contentAreaToCommit, branch, message, commitTags, this.repo, this.byteArrayIndex, this.clock);
    }

    /**
     * Checks out the content for the given commit into the given content area.
     *
     * @param commit       The commit to check out.
     * @param areaToUpdate The area to update with the content for the commit.
     */
    @Override
    public void checkoutIntoArea(TCommit commit, TArea areaToUpdate)
    {
        this.engine.checkoutIntoArea(commit, this.repo, areaToUpdate, contentFactory);
    }

    /**
     * Checks out the content for the given commit into a new content area.
     *
     * @param commit The commit to check out.
     * @return A new content area with the content from the checkout.
     */
    @Override
    public TArea checkout(TCommit commit)
    {
        return this.engine.checkout(commit, this.repo, this.areaFactory, contentFactory);
    }

    /**
     * Gets the latest commit for the branch with the given name.
     *
     * @param branchName The name of the branch to get the latest commit for.
     * @return The latest commit for the given branch. Null if there is no branch with the given name.
     */
    @Override
    public TCommit getLatestCommitForBranch(String branchName)
    {
        return this.engine.getLatestCommitForBranch(branchName, this.repo);
    }

    /**
     * Computes a difference between the given areas.
     * The areas could have come from anywhere.
     *
     * @param fromArea The first area to find differences from.
     * @param toArea   The second area to find differences to.
     * @return The differences between the given areas.
     */
    @Override
    public DifferenceAPI computeDifferenceBetweenAreas(AreaAPI<? extends TContent> fromArea, AreaAPI<? extends TContent> toArea)
    {
        return this.engine.computeDifferenceBetweenAreas(fromArea, toArea, differenceHandler);
    }

    /**
     * Computes a difference between the given commits.
     * It is assumed that the commits come from this repo.
     *
     * @param fromCommit The first commit to find differences from.
     * @param toCommit   The second commit to find differences to.
     * @return The differences between the given commits.
     */
    @Override
    public DifferenceAPI computeDifferenceBetweenCommits(TCommit fromCommit, TCommit toCommit)
    {
        return this.engine.computeDifferenceBetweenCommits(fromCommit, toCommit, this.differenceHandler, this.repo, this.areaFactory, this.contentFactory);
    }

    /**
     * Computes a difference between the given branches.
     *
     * @param fromBranchName The first branch to find differences from.
     * @param toBranchName   The second branch to find differences to.
     * @return The differences between the given branches.
     */
    @Override
    public DifferenceAPI computeDifferenceBetweenBranches(String fromBranchName, String toBranchName)
    {
        return this.engine.computeDifferenceBetweenBranches(fromBranchName, toBranchName, this.differenceHandler, this.repo, this.areaFactory, this.contentFactory);
    }

    /**
     * Computes a comparison between the given areas.
     * The areas could have come from anywhere.
     *
     * @param fromArea The first area to find comparisons from.
     * @param toArea   The second area to find comparisons to.
     * @return The comparisons between the given areas.
     */
    @Override
    public ComparisonAPI computeComparisonBetweenAreas(AreaAPI<? extends TContent> fromArea, AreaAPI<? extends TContent> toArea)
    {
        return this.engine.computeComparisonBetweenAreas(fromArea, toArea, this.comparisonHandler);
    }

    /**
     * Computes a comparison between the given commits.
     * It is assumed that the commits come from this repo.
     *
     * @param fromCommit The first commit to find comparisons from.
     * @param toCommit   The second commit to find comparisons to.
     * @return The comparisons between the given commits.
     */
    @Override
    public ComparisonAPI computeComparisonBetweenCommits(TCommit fromCommit, TCommit toCommit)
    {
        return this.engine.computeComparisonBetweenCommits(fromCommit, toCommit, this.comparisonHandler, this.repo, this.areaFactory, this.contentFactory);
    }

    /**
     * Computes a comparison between the given branches.
     *
     * @param fromBranchName The first branch to find comparisons from.
     * @param toBranchName   The second branch to find comparisons to.
     * @return The comparisons between the given branches.
     */
    @Override
    public ComparisonAPI computeComparisonBetweenBranches(String fromBranchName, String toBranchName)
    {
        return this.engine.computeComparisonBetweenBranches(fromBranchName, toBranchName, this.comparisonHandler, this.repo, this.areaFactory, this.contentFactory);
    }

    /**
     * Gets the set of branch names in the repo.
     *
     * @return The set of branch names in the repo. If there are no branches in the repo then an empty set is returned.
     */
    @Override
    public Set<String> getBranchNames()
    {
        return this.engine.getBranchNames(this.repo);
    }

    /**
     * Gets the set of tag names in the repo.
     *
     * @return The set of tag names in the repo. If there are no tags in the repo then an empty set is returned.
     */
    @Override
    public Set<String> getTagNames()
    {
        return this.engine.getTagNames(this.repo);
    }

    /**
     * Prepares a reusable search query from the given search definition.
     * This search query can be thought of as the compiled/prepared search query.
     * The same search query can be run for multiple repo's without needing to recompute the search query each time.
     *
     * @param searchQueryDefinition The definition of the search to perform.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    @Override
    public TSearchQuery prepareSearchQuery(SearchQueryDefinitionAPI searchQueryDefinition)
    {
        return this.engine.prepareSearchQuery(searchQueryDefinition);
    }

    /**
     * Searches for commits that match the given search query.
     * Use this when you want to reuse the search query
     *
     * @param searchQuery The search query to reuse for this search.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    @Override
    public TSearchResults searchWithQuery(TSearchQuery searchQuery)
    {
        return this.engine.searchWithQuery(searchQuery, null, this.repo, this.areaFactory, this.contentFactory);
    }

    /**
     * Searches for commits that match the given search query.
     * Use this when you want to reuse the search query
     *
     * @param searchQuery        The search query to reuse for this search.
     * @param overrideParameters Parameters to override the defaults of the search query with. Pass null to use the parameters in the search query.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    @Override
    public TSearchResults searchWithQuery(TSearchQuery searchQuery, SearchParametersAPI overrideParameters)
    {
        return this.engine.searchWithQuery(searchQuery, overrideParameters, this.repo, this.areaFactory, this.contentFactory);
    }

    /**
     * Searches for commits that match the given search definition.
     *
     * @param searchQueryDefinition The definition of the search to perform.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    @Override
    public TSearchResults search(SearchQueryDefinitionAPI searchQueryDefinition)
    {
        TSearchQuery searchQuery = this.engine.prepareSearchQuery(searchQueryDefinition);
        return this.engine.searchWithQuery(searchQuery, null, this.repo, this.areaFactory, this.contentFactory);
    }

    /**
     * Searches for commits that match the given search definition.
     *
     * @param searchQueryDefinition The definition of the search to perform.
     * @param overrideParameters    Parameters to override the defaults of the search definition with. Pass null to use the parameters in the search definition.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    @Override
    public TSearchResults search(SearchQueryDefinitionAPI searchQueryDefinition, SearchParametersAPI overrideParameters)
    {
        TSearchQuery searchQuery = this.engine.prepareSearchQuery(searchQueryDefinition);
        return this.engine.searchWithQuery(searchQuery, overrideParameters, this.repo, this.areaFactory, this.contentFactory);
    }


    /**
     * Merges one branch into another.
     * The merge handler is used to resolve any merge conflicts if there are any.
     *
     * @param destinationBranchName The branch that we should merge into.
     * @param sourceBranchName      The branch that we should merge from.
     * @param message               The commit message to use for the merge.
     * @param commitTags            The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @return The commit that was performed for the merge.
     */
    @Override
    public TCommit mergeIntoBranchFromAnotherBranch(String destinationBranchName, String sourceBranchName, String message, StringAreaAPI commitTags)
    {
        return this.engine.mergeIntoBranchFromAnotherBranch(destinationBranchName, sourceBranchName, message, commitTags, this.mergeHandler, this.comparisonHandler, this.differenceHandler, this.repo, this.areaFactory, this.contentFactory, this.byteArrayIndex, this.clock);
    }

    /**
     * Creates a new branch with the given name and makes it point at the given commit.
     * If the repo already has a branch with this name then it is updated to point at this commit.
     *
     * @param commit     The commit where the new branch should be created.
     * @param branchName The name of the branch to create at the commit.
     */
    @Override
    public void createBranchAtCommit(TCommit commit, String branchName)
    {
        this.engine.createBranchAtCommit(commit, branchName, this.repo);
    }

    /**
     * Removes the branch with the given name from the repo.
     *
     * @param branchName The name of the branch to remove.
     */
    @Override public void removeBranch(String branchName)
    {
        this.engine.removeBranch(repo, branchName);
    }

    /**
     * Casts or clones the given area to the specific type required by this repo handler.
     *
     * @param areaToCastOrClone The area to cast if it is already the required type or to clone if it is a different area type.
     * @return A compatible area for the repo handler which is either a cast of the same instance or a completely new clone of it if it is an incompatible type.
     */
    @Override
    public TArea castOrCloneArea(AreaAPI<? extends ContentAPI> areaToCastOrClone)
    {
        return this.engine.castOrCloneArea(areaToCastOrClone, this.areaFactory, this.contentFactory, this.byteArrayIndex);
    }
}
