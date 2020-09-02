/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.strings;

import io.nanovc.*;
import io.nanovc.areas.StringAreaAPI;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.clocks.ClockWithVMNanos;
import io.nanovc.comparisons.HashMapComparisonHandler;
import io.nanovc.content.StringContent;
import io.nanovc.differences.HashMapDifferenceHandler;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import io.nanovc.memory.MemoryCommit;
import io.nanovc.memory.MemorySearchQuery;
import io.nanovc.memory.MemorySearchResults;
import io.nanovc.merges.DiffFromCommonAncestorMergeHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A fully self contained nano repository for version control.
 * Use this class for general purpose storage of history.
 * If you want more control, see the {@link StringMemoryRepoHandler} instead.
 */
public class StringNanoRepo extends StringMemoryRepo
    implements StringMemoryRepoHandlerAPI<
    StringContent,
    StringHashMapArea,
    MemoryCommit,
    MemorySearchQuery,
    MemorySearchResults,
    StringMemoryRepo,
    StringMemoryRepoEngineAPI<
        StringContent,
        StringHashMapArea,
        MemoryCommit,
        MemorySearchQuery,
        MemorySearchResults,
        StringMemoryRepo
        >
    >
{
    /**
     * The byte array index that is used to de-duplicate arrays of bytes that we have seen before.
     */
    private ByteArrayIndex byteArrayIndex;

    /**
     * The engine that is used for the version control operations on this repo.
     */
    private StringMemoryRepoEngineAPI<
        StringContent,
        StringHashMapArea,
        MemoryCommit,
        MemorySearchQuery,
        MemorySearchResults,
        StringMemoryRepo
        > engine = COMMON_ENGINE;

    /**
     * A common engine that is used as the default for Nano Repos.
     */
    public static final StringMemoryRepoEngineAPI<
        StringContent,
        StringHashMapArea,
        MemoryCommit,
        MemorySearchQuery,
        MemorySearchResults,
        StringMemoryRepo
        > COMMON_ENGINE = new StringMemoryRepoEngine();

    /**
     * A common clock that is used as the default for Nano Repos.
     */
    public static final ClockWithVMNanos COMMON_CLOCK = new ClockWithVMNanos();

    /**
     * The clock that we use when we create commits.
     */
    private ClockAPI<? extends TimestampAPI> clock = COMMON_CLOCK;

    /**
     * A common difference handler that is used as the default for Nano Repos.
     */
    public static final DifferenceHandlerAPI<? extends DifferenceEngineAPI> COMMON_DIFFERENCE_HANDLER = HashMapDifferenceHandler.COMMON_DIFFERENCE_HANDLER;

    /**
     * The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    protected DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler = COMMON_DIFFERENCE_HANDLER;

    /**
     * A common comparison handler that is used as the default for Nano Repos.
     */
    public static final ComparisonHandlerAPI<? extends ComparisonEngineAPI> COMMON_COMPARISON_HANDLER = HashMapComparisonHandler.COMMON_COMPARISON_HANDLER;

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
     * Creates a new repo for strings and reuses the given byte array index.
     *
     * @param byteArrayIndex The byte array index to reuse. This allows us to keep a shared pool of byte arrays for the content that is created. This index could be shared across multiple repos to save memory.
     */
    public StringNanoRepo(ByteArrayIndex byteArrayIndex)
    {
        this.byteArrayIndex = byteArrayIndex;
    }

    /**
     * Creates a new repo for strings.
     */
    public StringNanoRepo()
    {
        this.byteArrayIndex = new HashWrapperByteArrayIndex();
    }

    /**
     * Creates a new repo for strings.
     *
     * @param byteArrayIndex    The byte array index to reuse. This allows us to keep a shared pool of byte arrays for the content that is created. This index could be shared across multiple repos to save memory. Plug in an alternative handler or use {@link HashWrapperByteArrayIndex}.
     * @param engine            The engine to use for the version control functionality. All of the version control logic is delegated to this engine. You can plug in an alternative engine to modify the behaviour for this repo. Plug in an alternative handler or use {@link #COMMON_ENGINE}.
     * @param clock             The clock to use when creating commits for this repo. Plug in an alternative clock or use {@link #COMMON_CLOCK}.
     * @param differenceHandler The handler to use when computing differences between commits. Plug in an alternative handler or use {@link #COMMON_DIFFERENCE_HANDLER}.
     * @param comparisonHandler The handler to use when computing comparisons between commits. Plug in an alternative handler or use {@link #COMMON_COMPARISON_HANDLER}.
     * @param mergeHandler      The handler to use when merging commits. Plug in an alternative handler or use {@link #COMMON_MERGE_HANDLER}.
     */
    public StringNanoRepo(ByteArrayIndex byteArrayIndex, StringMemoryRepoEngineAPI<StringContent, StringHashMapArea, MemoryCommit, MemorySearchQuery, MemorySearchResults, StringMemoryRepo> engine, ClockBase<? extends TimestampBase> clock, DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler, ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler, MergeHandlerAPI<? extends MergeEngineAPI> mergeHandler)
    {
        this.byteArrayIndex = byteArrayIndex;
        this.engine = engine;
        this.clock = clock;
        this.differenceHandler = differenceHandler;
        this.comparisonHandler = comparisonHandler;
        this.mergeHandler = mergeHandler;
    }

    /**
     * Creates a new area where content can be placed.
     *
     * @return A new content area that can be used for committing.
     */
    @Override
    public StringHashMapArea createArea()
    {
        return new StringHashMapArea();
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
    public MemoryCommit commit(StringHashMapArea contentAreaToCommit, String message, StringAreaAPI commitTags)
    {
        return this.getEngine().commit(contentAreaToCommit, message, commitTags, this, this.getByteArrayIndex(), this.getClock());
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
    public MemoryCommit commit(StringHashMapArea contentAreaToCommit, String message, StringAreaAPI commitTags, MemoryCommit parentCommit)
    {
        return this.getEngine().commit(contentAreaToCommit, message, commitTags, this, this.getByteArrayIndex(), this.getClock(), parentCommit);
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
    public MemoryCommit commit(StringHashMapArea contentAreaToCommit, String message, StringAreaAPI commitTags, MemoryCommit firstParentCommit, MemoryCommit... otherParentCommits)
    {
        return this.getEngine().commit(contentAreaToCommit, message, commitTags, this, this.getByteArrayIndex(), this.getClock(), firstParentCommit, Arrays.asList(otherParentCommits));
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
    @Override public MemoryCommit commit(StringHashMapArea contentAreaToCommit, String message, StringAreaAPI commitTags, List<MemoryCommit> parentCommits)
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
    public MemoryCommit commit(StringHashMapArea contentAreaToCommit, String message, StringAreaAPI commitTags, MemoryCommit firstParentCommit, List<MemoryCommit> otherParentCommits)
    {
        return this.getEngine().commit(contentAreaToCommit, message, commitTags, this, this.getByteArrayIndex(), this.getClock(), firstParentCommit, otherParentCommits);
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
    public MemoryCommit commitToBranch(StringHashMapArea contentAreaToCommit, String branch, String message, StringAreaAPI commitTags)
    {
        return this.getEngine().commitToBranch(contentAreaToCommit, branch, message, commitTags, this, this.getByteArrayIndex(), this.getClock());
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
        this.getEngine().createBranchAtCommit(commit, branchName, this);
    }

    /**
     * Removes the branch with the given name from the repo.
     *
     * @param branchName The name of the branch to remove.
     */
    @Override public void removeBranch(String branchName)
    {
        this.getEngine().removeBranch(this, branchName);
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
        return this.getEngine().getLatestCommitForBranch(branchName, this);
    }

    /**
     * Checks out the content for the given commit into the given content area.
     *
     * @param commit       The commit to check out.
     * @param areaToUpdate The area to update with the content for the commit.
     */
    @Override
    public void checkoutIntoArea(MemoryCommit commit, StringHashMapArea areaToUpdate)
    {
        this.getEngine().checkoutIntoArea(commit, this, areaToUpdate, this::createContent);
    }

    /**
     * Checks out the content for the given commit into a new content area.
     *
     * @param commit The commit to check out.
     * @return A new content area with the content from the checkout.
     */
    @Override
    public StringHashMapArea checkout(MemoryCommit commit)
    {
        return this.getEngine().checkout(commit, this, this::createArea, this::createContent);
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
        this.getEngine().tagCommit(this, commit, tagName);
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
        return this.getEngine().getCommitForTag(this, tagName);
    }

    /**
     * Removes the tag with the given name from the repo.
     *
     * @param tagName The name of the tag to remove. If this tag doesn't exist then nothing happens.
     */
    @Override
    public void removeTag(String tagName)
    {
        this.getEngine().removeTag(this, tagName);
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
    public DifferenceAPI computeDifferenceBetweenAreas(AreaAPI<? extends StringContent> fromArea, AreaAPI<? extends StringContent> toArea)
    {
        return this.getEngine().computeDifferenceBetweenAreas(fromArea, toArea, this.getDifferenceHandler());
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
        return this.getEngine().computeDifferenceBetweenCommits(fromCommit, toCommit, this.getDifferenceHandler(), this, this::createArea, this::createContent);
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
        return this.getEngine().computeDifferenceBetweenBranches(fromBranchName, toBranchName, getDifferenceHandler(), this, this::createArea, this::createContent);
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
    public ComparisonAPI computeComparisonBetweenAreas(AreaAPI<? extends StringContent> fromArea, AreaAPI<? extends StringContent> toArea)
    {
        return this.getEngine().computeComparisonBetweenAreas(fromArea, toArea, getComparisonHandler());
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
        return this.getEngine().computeComparisonBetweenCommits(fromCommit, toCommit, getComparisonHandler(), this, this::createArea, this::createContent);
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
        return this.getEngine().computeComparisonBetweenBranches(fromBranchName, toBranchName, getComparisonHandler(), this, this::createArea, this::createContent);
    }

    /**
     * Creates the content for the given path and with the given bytes.
     *
     * @param contentBytes The bytes for the content.
     * @return The new piece of content with the given bytes at the given path.
     */
    protected StringContent createContent(byte[] contentBytes)
    {
        return new StringContent(contentBytes);
    }

    /**
     * Gets the set of branch names in the repo.
     *
     * @return The set of branch names in the repo. If there are no branches in the repo then an empty set is returned.
     */
    @Override
    public Set<String> getBranchNames()
    {
        return this.getEngine().getBranchNames(this);
    }

    /**
     * Gets the set of tag names in the repo.
     *
     * @return The set of tag names in the repo. If there are no tags in the repo then an empty set is returned.
     */
    @Override
    public Set<String> getTagNames()
    {
        return this.getEngine().getTagNames(this);
    }

    /**
     * Casts or clones the given area to the specific type required by this repo handler.
     *
     * @param areaToCastOrClone The area to cast if it is already the required type or to clone if it is a different area type.
     * @return A compatible area for the repo handler which is either a cast of the same instance or a completely new clone of it if it is an incompatible type.
     */
    @Override
    public StringHashMapArea castOrCloneArea(AreaAPI<? extends ContentAPI> areaToCastOrClone)
    {
        return this.engine.castOrCloneArea(areaToCastOrClone, this::createArea, this::createContent, this.byteArrayIndex);
    }

    /**
     * Gets the repo that is being handled.
     *
     * @return The repo that is being handled.
     */
    @Override
    public StringNanoRepo getRepo()
    {
        return this;
    }

    /**
     * Sets the repo that is being handled.
     *
     * @param repo The repo that is being handled.
     */
    @Override
    public void setRepo(StringMemoryRepo repo)
    {
        throw new IllegalArgumentException("Cannot set a string nano repo to another string nano repo. That doesn't make sense for Object Oriented nano repos.");
    }

    /**
     * Gets the engine that is used to work with the repo.
     * An alternate (but compatible) engine can be plugged in to modify the algorithm being used for working with the repo.
     *
     * @return The engine that is used to work with the repo.
     */
    @Override
    public StringMemoryRepoEngineAPI<StringContent, StringHashMapArea, MemoryCommit, MemorySearchQuery, MemorySearchResults, StringMemoryRepo> getEngine()
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
    public void setEngine(StringMemoryRepoEngineAPI<StringContent, StringHashMapArea, MemoryCommit, MemorySearchQuery, MemorySearchResults, StringMemoryRepo> engine)
    {
        this.engine = engine;
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
        return this.getEngine().prepareSearchQuery(searchQueryDefinition);
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
        return this.getEngine().searchWithQuery(searchQuery, null, this, this::createArea, this::createContent);
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
        return this.getEngine().searchWithQuery(searchQuery, overrideParameters, this, this::createArea, this::createContent);
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
        MemorySearchQuery searchQuery = this.engine.prepareSearchQuery(searchQueryDefinition);
        return this.engine.searchWithQuery(searchQuery, null, this, this::createArea, this::createContent);
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
        MemorySearchQuery searchQuery = this.engine.prepareSearchQuery(searchQueryDefinition);
        return this.engine.searchWithQuery(searchQuery, overrideParameters, this, this::createArea, this::createContent);
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
        return this.getEngine().mergeIntoBranchFromAnotherBranch(destinationBranchName, sourceBranchName, message, commitTags, mergeHandler, getComparisonHandler(), getDifferenceHandler(), this, this::createArea, this::createContent, getByteArrayIndex(), getClock());
    }


    /**
     * Gets the byte array index that is used to de-duplicate arrays of bytes that we have seen before.
     *
     * @return The byte array index that is used to de-duplicate arrays of bytes that we have seen before.
     */
    public ByteArrayIndex getByteArrayIndex()
    {
        return byteArrayIndex;
    }

    /**
     * Sets the byte array index that is used to de-duplicate arrays of bytes that we have seen before.
     *
     * @param byteArrayIndex The byte array index that is used to de-duplicate arrays of bytes that we have seen before.
     */
    public void setByteArrayIndex(ByteArrayIndex byteArrayIndex)
    {
        this.byteArrayIndex = byteArrayIndex;
    }

    /**
     * Gets the clock that we use when we create commits.
     *
     * @return The clock that we use when we create commits.
     */
    public ClockAPI<? extends TimestampAPI> getClock()
    {
        return clock;
    }

    /**
     * Sets the clock that we use when we create commits.
     *
     * @param clock The clock that we use when we create commits.
     */
    public void setClock(ClockAPI<? extends TimestampAPI> clock)
    {
        this.clock = clock;
    }
}
