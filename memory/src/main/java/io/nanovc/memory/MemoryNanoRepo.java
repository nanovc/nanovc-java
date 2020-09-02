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
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.areas.StringAreaAPI;
import io.nanovc.clocks.ClockWithVMNanos;
import io.nanovc.comparisons.HashMapComparisonHandler;
import io.nanovc.content.ByteArrayContent;
import io.nanovc.differences.HashMapDifferenceHandler;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import io.nanovc.merges.DiffFromCommonAncestorMergeHandler;
import io.nanovc.merges.LastWinsMergeHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A fully self contained nano repository for version control.
 * Use this class for general purpose storage of history.
 * If you want more control, see the {@link RepoHandlerAPI} instead.
 */
public class MemoryNanoRepo extends MemoryRepo<ByteArrayContent, ByteArrayHashMapArea>
    implements MemoryRepoHandlerAPI<
    ByteArrayContent,
    ByteArrayHashMapArea,
    MemoryCommit,
    MemorySearchQuery,
    MemorySearchResults,
    MemoryRepo<ByteArrayContent, ByteArrayHashMapArea>,
    MemoryRepoEngine<ByteArrayContent, ByteArrayHashMapArea>
    >
{

    /**
     * The common engine to use for {@link MemoryNanoRepo}'s.
     */
    public static final ClockAPI<? extends TimestampAPI> COMMON_CLOCK = ClockWithVMNanos.COMMON_CLOCK;

    /**
     * The clock to use for creating timestamps.
     */
    protected ClockAPI<? extends TimestampAPI> clock = COMMON_CLOCK;

    /**
     * The common engine to use for {@link MemoryNanoRepo}'s.
     */
    public static final MemoryRepoEngine<ByteArrayContent, ByteArrayHashMapArea> COMMON_ENGINE = new MemoryRepoEngine<>();

    /**
     * The engine to use for this {@link MemoryNanoRepo}.
     */
    protected MemoryRepoEngine<ByteArrayContent, ByteArrayHashMapArea> engine = COMMON_ENGINE;

    /**
     * A common difference handler that is used as the default for Nano Repos.
     */
    public static final HashMapDifferenceHandler COMMON_DIFFERENCE_HANDLER = HashMapDifferenceHandler.COMMON_DIFFERENCE_HANDLER;

    /**
     * The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    protected DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler = COMMON_DIFFERENCE_HANDLER;

    /**
     * A common comparison handler that is used as the default for Nano Repos.
     */
    public static final HashMapComparisonHandler COMMON_COMPARISON_HANDLER = HashMapComparisonHandler.COMMON_COMPARISON_HANDLER;

    /**
     * The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    protected ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler = COMMON_COMPARISON_HANDLER;

    /**
     * A common merge handler that is used as the default for Nano Repos.
     */
    public static final MergeHandlerAPI<? extends MergeEngineAPI> COMMON_MERGE_HANDLER = DiffFromCommonAncestorMergeHandler.COMMON_MERGE_HANDLER;

    /**
     * The handler to use for merging commits.
     */
    protected MergeHandlerAPI<? extends MergeEngineAPI> mergeHandler = COMMON_MERGE_HANDLER;

    /**
     * The byte array index to use for managing the in-memory byte arrays that get created in a repo.
     * This index allows us to re-use arrays in memory.
     * The index gives us Value-Equality semantics for byte[] lookups.
     */
    protected ByteArrayIndex byteArrayIndex;

    /**
     * Creates a new Memory Nano Repo.
     */
    public MemoryNanoRepo()
    {
        ensureDependenciesExist();
    }

    /**
     * Creates a new Memory Nano Repo.
     *
     * @param byteArrayIndex The index to use when committing content.
     */
    public MemoryNanoRepo(ByteArrayIndex byteArrayIndex)
    {
        this.byteArrayIndex = byteArrayIndex;
        ensureDependenciesExist();
    }

    /**
     * Creates a new Memory Nano Repo.
     *
     * @param byteArrayIndex    The byte array index to reuse. This allows us to keep a shared pool of byte arrays for the content that is created. This index could be shared across multiple repos to save memory. Plug in an alternative handler or use {@link HashWrapperByteArrayIndex}.
     * @param engine            The engine to use for the version control functionality. All of the version control logic is delegated to this engine. You can plug in an alternative engine to modify the behaviour for this repo. Plug in an alternative handler or use {@link #COMMON_ENGINE}.
     * @param clock             The clock to use when creating commits for this repo. Plug in an alternative handler or use {@link #COMMON_CLOCK}.
     * @param differenceHandler The handler to use when computing differences between commits. Plug in an alternative handler or use {@link #COMMON_DIFFERENCE_HANDLER}.
     * @param comparisonHandler The handler to use when computing comparisons between commits. Plug in an alternative handler or use {@link #COMMON_COMPARISON_HANDLER}.
     * @param mergeHandler      The handler to use when merging commits. Plug in an alternative handler or use {@link #COMMON_MERGE_HANDLER}.
     */
    public MemoryNanoRepo(ByteArrayIndex byteArrayIndex, MemoryRepoEngine<ByteArrayContent, ByteArrayHashMapArea> engine, ClockAPI<? extends TimestampAPI> clock, DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler, ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler, MergeHandlerAPI<? extends MergeEngineAPI> mergeHandler)
    {
        this.byteArrayIndex = byteArrayIndex;
        this.engine = engine;
        this.clock = clock;
        this.differenceHandler = differenceHandler;
        this.comparisonHandler = comparisonHandler;
        this.mergeHandler = mergeHandler;
        ensureDependenciesExist();
    }

    private void ensureDependenciesExist()
    {
        // Make sure we have a repo engine:
        if (this.engine == null)
        {
            // Create the default engine:
            this.engine = COMMON_ENGINE;
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
    }


    /**
     * Creates a new area where content can be placed.
     *
     * @return A new content area that can be used for committing.
     */
    @Override
    public ByteArrayHashMapArea createArea()
    {
        return this.engine.createArea(ByteArrayHashMapArea::new);
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
    public MemoryCommit commit(ByteArrayHashMapArea contentAreaToCommit, String message, StringAreaAPI commitTags)
    {
        return this.engine.commit(contentAreaToCommit, message, commitTags, this, this.byteArrayIndex, this.clock);
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
    public MemoryCommit commit(ByteArrayHashMapArea contentAreaToCommit, String message, StringAreaAPI commitTags, MemoryCommit parentCommit)
    {
        return this.engine.commit(contentAreaToCommit, message, commitTags, this, this.byteArrayIndex, this.clock, parentCommit);
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
    public MemoryCommit commit(ByteArrayHashMapArea contentAreaToCommit, String message, StringAreaAPI commitTags, MemoryCommit firstParentCommit, MemoryCommit... otherParentCommits)
    {
        return this.engine.commit(contentAreaToCommit, message, commitTags, this, this.byteArrayIndex, this.clock, firstParentCommit, Arrays.asList(otherParentCommits));
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
    @Override public MemoryCommit commit(ByteArrayHashMapArea contentAreaToCommit, String message, StringAreaAPI commitTags, List<MemoryCommit> parentCommits)
    {
        // Determine how many parent commits there are to decide how to route this to the engine:
        if (parentCommits == null)
        {
            // There is no list of parent commits.
            return this.engine.commit(contentAreaToCommit, message, commitTags, this, this.byteArrayIndex, this.clock);
        }
        else
        {
            // There is a list of parent commits.
            // Determine how to pass the list to the engine as efficiently as possible:
            switch (parentCommits.size())
            {
                case 0:
                    return this.engine.commit(contentAreaToCommit, message, commitTags, this, this.byteArrayIndex, this.clock);
                case 1:
                    return this.engine.commit(contentAreaToCommit, message, commitTags, this, this.byteArrayIndex, this.clock, parentCommits.get(0));
                default:
                    return this.engine.commit(contentAreaToCommit, message, commitTags, this, this.byteArrayIndex, this.clock, parentCommits.get(0), parentCommits.subList(1, parentCommits.size()));
            }
        }
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
    public MemoryCommit commit(ByteArrayHashMapArea contentAreaToCommit, String message, StringAreaAPI commitTags, MemoryCommit firstParentCommit, List<MemoryCommit> otherParentCommits)
    {
        return this.engine.commit(contentAreaToCommit, message, commitTags, this, this.byteArrayIndex, this.clock, firstParentCommit, otherParentCommits);
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
    public MemoryCommit commitToBranch(ByteArrayHashMapArea contentAreaToCommit, String branch, String message, StringAreaAPI commitTags)
    {
        return this.engine.commitToBranch(contentAreaToCommit, branch, message, commitTags, this, this.byteArrayIndex, this.clock);
    }

    /**
     * Creates a new branch with the given name and makes it point at the given commit.
     * If the repo already has a branch with this name then it is updated to point at this commit.
     *
     * @param commit     The commit where the new branch should be created.
     * @param branchName The name of the branch to create at the commit.
     */
    @Override
    public void createBranchAtCommit(MemoryCommit commit, String branchName)
    {
        this.engine.createBranchAtCommit(commit, branchName, this);
    }

    /**
     * Removes the branch with the given name from the repo.
     *
     * @param branchName The name of the branch to remove.
     */
    @Override public void removeBranch(String branchName)
    {
        this.engine.removeBranch(this, branchName);
    }

    /**
     * Gets the latest commit for the branch with the given name.
     *
     * @param branchName The name of the branch to get the latest commit for.
     * @return The latest commit for the given branch. Null if there is no branch with the given name.
     */
    @Override
    public MemoryCommit getLatestCommitForBranch(String branchName)
    {
        return this.engine.getLatestCommitForBranch(branchName, this);
    }

    /**
     * Checks out the content for the given commit into the given content area.
     *
     * @param commit       The commit to check out.
     * @param areaToUpdate The area to update with the content for the commit.
     */
    @Override
    public void checkoutIntoArea(MemoryCommit commit, ByteArrayHashMapArea areaToUpdate)
    {
        this.engine.checkoutIntoArea(commit, this, areaToUpdate, ByteArrayContent::new);
    }

    /**
     * Checks out the content for the given commit into a new content area.
     *
     * @param commit The commit to check out.
     * @return A new content area with the content from the checkout.
     */
    @Override
    public ByteArrayHashMapArea checkout(MemoryCommit commit)
    {
        return this.engine.checkout(commit, this, ByteArrayHashMapArea::new, ByteArrayContent::new);
    }

    /**
     * Tags the commit with the given name.
     * This tag name can be used to reference a specific commit in the history, independently of the branches.
     *
     * @param commit  The commit to tag with a name.
     * @param tagName The name of the tag to give to this commit.
     */
    @Override
    public void tagCommit(MemoryCommit commit, String tagName)
    {
        this.engine.tagCommit(this, commit, tagName);
    }

    /**
     * Gets the commit with the given tag name.
     *
     * @param tagName The name of the tagged commit.
     * @return The commit with the given tag name. Null if there is no tag with this name.
     */
    @Override
    public MemoryCommit getCommitForTag(String tagName)
    {
        return this.engine.getCommitForTag(this, tagName);
    }

    /**
     * Removes the tag with the given name from the repo.
     *
     * @param tagName The name of the tag to remove. If this tag doesn't exist then nothing happens.
     */
    @Override
    public void removeTag(String tagName)
    {
        this.engine.removeTag(this, tagName);
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
    public DifferenceAPI computeDifferenceBetweenAreas(AreaAPI<? extends ByteArrayContent> fromArea, AreaAPI<? extends ByteArrayContent> toArea)
    {
        return this.engine.computeDifferenceBetweenAreas(fromArea, toArea, this.differenceHandler);
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
    public DifferenceAPI computeDifferenceBetweenCommits(MemoryCommit fromCommit, MemoryCommit toCommit)
    {
        return this.engine.computeDifferenceBetweenCommits(fromCommit, toCommit, this.differenceHandler, this, ByteArrayHashMapArea::new, ByteArrayContent::new);
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
        return this.engine.computeDifferenceBetweenBranches(fromBranchName, toBranchName, this.differenceHandler, this, ByteArrayHashMapArea::new, ByteArrayContent::new);
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
    public ComparisonAPI computeComparisonBetweenAreas(AreaAPI<? extends ByteArrayContent> fromArea, AreaAPI<? extends ByteArrayContent> toArea)
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
    public ComparisonAPI computeComparisonBetweenCommits(MemoryCommit fromCommit, MemoryCommit toCommit)
    {
        return this.engine.computeComparisonBetweenCommits(fromCommit, toCommit, this.comparisonHandler, this, ByteArrayHashMapArea::new, ByteArrayContent::new);
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
        return this.engine.computeComparisonBetweenBranches(fromBranchName, toBranchName, this.comparisonHandler, this, ByteArrayHashMapArea::new, ByteArrayContent::new);
    }

    /**
     * Gets the set of branch names in the repo.
     *
     * @return The set of branch names in the repo. If there are no branches in the repo then an empty set is returned.
     */
    @Override
    public Set<String> getBranchNames()
    {
        return this.engine.getBranchNames(this);
    }

    /**
     * Gets the set of tag names in the repo.
     *
     * @return The set of tag names in the repo. If there are no tags in the repo then an empty set is returned.
     */
    @Override
    public Set<String> getTagNames()
    {
        return this.engine.getTagNames(this);
    }

    /**
     * Gets the repo that is being handled.
     *
     * @return The repo that is being handled.
     */
    @Override
    public MemoryRepo<ByteArrayContent, ByteArrayHashMapArea> getRepo()
    {
        return this;
    }

    /**
     * Sets the repo that is being handled.
     *
     * @param repo The repo that is being handled.
     */
    @Override
    public void setRepo(MemoryRepo<ByteArrayContent, ByteArrayHashMapArea> repo)
    {
        throw new IllegalArgumentException("Cannot set a memory nano repo to another memory nano repo. That doesn't make sense for Object Oriented nano repos.");
    }

    /**
     * Gets the engine that is used to work with the repo.
     * An alternate (but compatible) engine can be plugged in to modify the algorithm being used for working with the repo.
     *
     * @return The engine that is used to work with the repo.
     */
    @Override
    public MemoryRepoEngine<ByteArrayContent, ByteArrayHashMapArea> getEngine()
    {
        return this.engine;
    }

    /**
     * Sets the engine that is used to work with the repo.
     * An alternate (but compatible) engine can be plugged in to modify the algorithm being used for working with the repo.
     *
     * @param engine The engine that is used to work with the repo.
     */
    @Override
    public void setEngine(MemoryRepoEngine<ByteArrayContent, ByteArrayHashMapArea> engine)
    {
        this.engine = engine;
    }

    /**
     * Gets the clock to use for creating timestamps.
     *
     * @return The clock to use for creating timestamps.
     */
    @Override public ClockAPI<? extends TimestampAPI> getClock()
    {
        return this.clock;
    }

    /**
     * Sets the clock to use for creating timestamps.
     *
     * @param clock The clock to use for creating timestamps.
     */
    @Override public void setClock(ClockAPI<? extends TimestampAPI> clock)
    {
        this.clock = clock;
    }

    /**
     * Gets the handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     *
     * @return The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    @Override
    public DifferenceHandlerAPI<? extends DifferenceEngineAPI> getDifferenceHandler()
    {
        return this.differenceHandler;
    }

    /**
     * Sets the handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     *
     * @param differenceHandler The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    @Override
    public void setDifferenceHandler(DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler)
    {
        this.differenceHandler = differenceHandler;
    }

    /**
     * Gets the handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     *
     * @return The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    @Override
    public ComparisonHandlerAPI<? extends ComparisonEngineAPI> getComparisonHandler()
    {
        return this.comparisonHandler;
    }

    /**
     * Sets the handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     *
     * @param comparisonHandler The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    @Override
    public void setComparisonHandler(ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler)
    {
        this.comparisonHandler = comparisonHandler;
    }

    /**
     * Gets the handler to use for merges.
     *
     * @return The handler to use for merges.
     */
    @Override
    public MergeHandlerAPI<? extends MergeEngineAPI> getMergeHandler()
    {
        return this.mergeHandler;
    }

    /**
     * Sets the handler to use for merges.
     *
     * @param mergeHandler The handler to use for merges.
     */
    @Override
    public void setMergeHandler(MergeHandlerAPI<? extends MergeEngineAPI> mergeHandler)
    {
        this.mergeHandler = mergeHandler;
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
    public MemorySearchQuery prepareSearchQuery(SearchQueryDefinitionAPI searchQueryDefinition)
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
    public MemorySearchResults searchWithQuery(MemorySearchQuery searchQuery)
    {
        return this.engine.searchWithQuery(searchQuery, null, this, ByteArrayHashMapArea::new, ByteArrayContent::new);
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
    public MemorySearchResults searchWithQuery(MemorySearchQuery searchQuery, SearchParametersAPI overrideParameters)
    {
        return this.engine.searchWithQuery(searchQuery, overrideParameters, this, ByteArrayHashMapArea::new, ByteArrayContent::new);
    }

    /**
     * Searches for commits that match the given search definition.
     *
     * @param searchQueryDefinition The definition of the search to perform.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    @Override
    public MemorySearchResults search(SearchQueryDefinitionAPI searchQueryDefinition)
    {
        MemorySearchQuery searchQuery = prepareSearchQuery(searchQueryDefinition);
        return searchWithQuery(searchQuery);
    }

    /**
     * Searches for commits that match the given search definition.
     *
     * @param searchQueryDefinition The definition of the search to perform.
     * @param overrideParameters    Parameters to override the defaults of the search definition with. Pass null to use the parameters in the search definition.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    @Override
    public MemorySearchResults search(SearchQueryDefinitionAPI searchQueryDefinition, SearchParametersAPI overrideParameters)
    {
        MemorySearchQuery searchQuery = prepareSearchQuery(searchQueryDefinition);
        return searchWithQuery(searchQuery, overrideParameters);
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
    public MemoryCommit mergeIntoBranchFromAnotherBranch(String destinationBranchName, String sourceBranchName, String message, StringAreaAPI commitTags)
    {
        return this.engine.mergeIntoBranchFromAnotherBranch(destinationBranchName, sourceBranchName, message, commitTags, this.mergeHandler, this.comparisonHandler, this.differenceHandler, this, ByteArrayHashMapArea::new, ByteArrayContent::new, this.byteArrayIndex, this.clock);
    }

    /**
     * Merges a commit into a branch.
     * The merge handler is used to resolve any merge conflicts if there are any.
     *
     * @param destinationBranchName The branch that we should merge into.
     * @param sourceCommit          The commit that we should merge from.
     * @param message               The commit message to use for the merge.
     * @param commitTags            The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @return The commit that was performed for the merge.
     */
    @Override public MemoryCommit mergeIntoBranchFromCommit(String destinationBranchName, MemoryCommit sourceCommit, String message, StringAreaAPI commitTags)
    {
        return this.getEngine().mergeIntoBranchFromCommit(destinationBranchName, sourceCommit, message, commitTags, this.mergeHandler, this.comparisonHandler, this.differenceHandler, this, ByteArrayHashMapArea::new, ByteArrayContent::new, this.byteArrayIndex, this.clock);
    }

    /**
     * Merges a branch into a commit.
     * The merge handler is used to resolve any merge conflicts if there are any.
     *
     * @param destinationCommit The commit that we should merge into.
     * @param sourceBranchName  The branch that we should merge from.
     * @param message           The commit message to use for the merge.
     * @param commitTags        The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @return The commit that was performed for the merge.
     */
    @Override public MemoryCommit mergeIntoCommitFromBranch(MemoryCommit destinationCommit, String sourceBranchName, String message, StringAreaAPI commitTags)
    {
        return this.getEngine().mergeIntoCommitFromBranch(destinationCommit, sourceBranchName, message, commitTags, this.mergeHandler, this.comparisonHandler, this.differenceHandler, this, ByteArrayHashMapArea::new, ByteArrayContent::new, this.byteArrayIndex, this.clock);
    }

    /**
     * Merges a commit into another commit.
     * The merge handler is used to resolve any merge conflicts if there are any.
     *
     * @param destinationCommit The commit that we should merge into.
     * @param sourceCommit      The commit that we should merge from.
     * @param message           The commit message to use for the merge.
     * @param commitTags        The commit tags to add to this commit. This allows an arbitrary amount of information to be associated with this commit. See {@link CommitTags} for helper methods here. Any {@link StringAreaAPI} can be used here.
     * @return The commit that was performed for the merge.
     */
    @Override public MemoryCommit mergeCommits(MemoryCommit destinationCommit, MemoryCommit sourceCommit, String message, StringAreaAPI commitTags)
    {
        return this.getEngine().mergeCommits(destinationCommit, sourceCommit, message, commitTags, this.mergeHandler, this.comparisonHandler, this.differenceHandler, this, ByteArrayHashMapArea::new, ByteArrayContent::new, this.byteArrayIndex, this.clock);
    }

    /**
     * Casts or clones the given area to the specific type required by this repo handler.
     *
     * @param areaToCastOrClone The area to cast if it is already the required type or to clone if it is a different area type.
     * @return A compatible area for the repo handler which is either a cast of the same instance or a completely new clone of it if it is an incompatible type.
     */
    @Override
    public ByteArrayHashMapArea castOrCloneArea(AreaAPI<? extends ContentAPI> areaToCastOrClone)
    {
        return this.engine.castOrCloneArea(areaToCastOrClone, this::createArea, ByteArrayContent::new, this.byteArrayIndex);
    }
}
