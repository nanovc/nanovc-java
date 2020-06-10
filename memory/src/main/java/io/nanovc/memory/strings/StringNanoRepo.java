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
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.clocks.ClockWithVMNanos;
import io.nanovc.comparisons.HashMapComparisonHandler;
import io.nanovc.content.StringContent;
import io.nanovc.differences.HashMapDifferenceHandler;
import io.nanovc.indexes.ByteArrayIndex;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import io.nanovc.memory.*;
import io.nanovc.merges.LastWinsMergeHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
     * The clock that we use when we create commits.
     */
    private Clock<? extends Timestamp> clock = COMMON_CLOCK;

    /**
     * A common clock that is used as the default for Nano Repos.
     */
    public static final ClockWithVMNanos COMMON_CLOCK = new ClockWithVMNanos();

    /**
     * The encoding to use when committing strings.
     */
    private Charset encoding = StandardCharsets.UTF_8;

    /**
     * The handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     */
    private DifferenceHandler<? extends DifferenceEngine> differenceHandler = COMMON_DIFFERENCE_HANDLER;

    /**
     * A common difference handler that is used as the default for Nano Repos.
     */
    public static final HashMapDifferenceHandler COMMON_DIFFERENCE_HANDLER = new HashMapDifferenceHandler();

    /**
     * The handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     */
    private ComparisonHandler<? extends ComparisonEngine> comparisonHandler = COMMON_COMPARISON_HANDLER;

    /**
     * A common comparison handler that is used as the default for Nano Repos.
     */
    public static final HashMapComparisonHandler COMMON_COMPARISON_HANDLER = new HashMapComparisonHandler();

    /**
     * The handler to use for merges.
     */
    private MergeHandler<? extends MergeEngine> mergeHandler = COMMON_MERGE_HANDLER;

    /**
     * A common merge handler that is used as the default for Nano Repos.
     */
    public static final LastWinsMergeHandler COMMON_MERGE_HANDLER = new LastWinsMergeHandler();

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
     * @param byteArrayIndex The byte array index to reuse. This allows us to keep a shared pool of byte arrays for the content that is created. This index could be shared across multiple repos to save memory. Plug in an alternative handler or use {@link HashWrapperByteArrayIndex}.
     * @param engine The engine to use for the version control functionality. All of the version control logic is delegated to this engine. You can plug in an alternative engine to modify the behaviour for this repo. Plug in an alternative handler or use {@link #COMMON_ENGINE}.
     * @param clock The clock to use when creating commits for this repo. Plug in an alternative handler or use {@link #COMMON_CLOCK}.
     * @param encoding The encoding to use to store the strings in this repo as bytes in the content areas of commits. Plug in an alternative handler or use {@link StandardCharsets#UTF_8}.
     * @param differenceHandler The handler to use when computing differences between commits. Plug in an alternative handler or use {@link #COMMON_DIFFERENCE_HANDLER}.
     * @param comparisonHandler The handler to use when computing comparisons between commits. Plug in an alternative handler or use {@link #COMMON_COMPARISON_HANDLER}.
     * @param mergeHandler The handler to use when merging commits. Plug in an alternative handler or use {@link #COMMON_MERGE_HANDLER}.
     */
    public StringNanoRepo(ByteArrayIndex byteArrayIndex, StringMemoryRepoEngineAPI<StringContent, StringHashMapArea, MemoryCommit, MemorySearchQuery, MemorySearchResults, StringMemoryRepo> engine, Clock<? extends Timestamp> clock, Charset encoding, DifferenceHandler<? extends DifferenceEngine> differenceHandler, ComparisonHandler<? extends ComparisonEngine> comparisonHandler, MergeHandler<? extends MergeEngine> mergeHandler)
    {
        this.byteArrayIndex = byteArrayIndex;
        this.engine = engine;
        this.clock = clock;
        this.encoding = encoding;
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
        return new StringHashMapArea(this.getEncoding());
    }

    /**
     * Commit the given content to the repo.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @return The commit for this content.
     */
    @Override
    public MemoryCommit commit(StringHashMapArea contentAreaToCommit, String message)
    {
        return this.getEngine().commit(contentAreaToCommit, message, this, this.getByteArrayIndex(), this.getClock());
    }

    /**
     * Commit the given content to the repo.
     * It tracks the given commit as the parent.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @param parentCommit        The parent commit that we want to make this commit from.
     * @return The commit for this content.
     */
    @Override
    public MemoryCommit commit(StringHashMapArea contentAreaToCommit, String message, MemoryCommit parentCommit)
    {
        return this.getEngine().commit(contentAreaToCommit, message, this, this.getByteArrayIndex(), this.getClock(), parentCommit);
    }

    /**
     * Commit the given content to the repo.
     * It tracks the given commits as the parents.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @param firstParentCommit   The parent commit that we want to make this commit from.
     * @param otherParentCommits  The other parents to have in addition to the first parent commit.
     * @return The commit for this content area.
     */
    @Override
    public MemoryCommit commit(StringHashMapArea contentAreaToCommit, String message, MemoryCommit firstParentCommit, MemoryCommit... otherParentCommits)
    {
        return this.getEngine().commit(contentAreaToCommit, message, this, this.getByteArrayIndex(), this.getClock(), firstParentCommit, Arrays.asList(otherParentCommits));
    }

    /**
     * Commit the given content to the repo.
     * It tracks the given commits as the parents.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @param firstParentCommit   The parent commit that we want to make this commit from.
     * @param otherParentCommits  The other parents to have in addition to the first parent commit.
     * @return The commit for this content area.
     */
    @Override
    public MemoryCommit commit(StringHashMapArea contentAreaToCommit, String message, MemoryCommit firstParentCommit, List<MemoryCommit> otherParentCommits)
    {
        return this.getEngine().commit(contentAreaToCommit, message, this, this.getByteArrayIndex(), this.getClock(), firstParentCommit, otherParentCommits);
    }

    /**
     * Commit the given content to the given branch in the the repo.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param branch              The branch to commit to. If the branch doesn't exist, it is created.
     * @param message             The commit message.
     * @return The commit for this content.
     */
    @Override
    public MemoryCommit commitToBranch(StringHashMapArea contentAreaToCommit, String branch, String message)
    {
        return this.getEngine().commitToBranch(contentAreaToCommit, branch, message, this, this.getByteArrayIndex(), this.getClock());
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
    public Difference computeDifferenceBetweenAreas(Area<? extends StringContent> fromArea, Area<? extends StringContent> toArea)
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
    public Difference computeDifferenceBetweenCommits(MemoryCommit fromCommit, MemoryCommit toCommit)
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
    public Difference computeDifferenceBetweenBranches(String fromBranchName, String toBranchName)
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
    public Comparison computeComparisonBetweenAreas(Area<? extends StringContent> fromArea, Area<? extends StringContent> toArea)
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
    public Comparison computeComparisonBetweenCommits(MemoryCommit fromCommit, MemoryCommit toCommit)
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
    public Comparison computeComparisonBetweenBranches(String fromBranchName, String toBranchName)
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
        return new StringContent(contentBytes, this.getEncoding());
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
    public StringHashMapArea castOrCloneArea(Area<? extends Content> areaToCastOrClone)
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
     * Gets the handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     *
     * @return The handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     */
    @Override
    public DifferenceHandler<? extends DifferenceEngine> getDifferenceHandler()
    {
        return this.differenceHandler;
    }

    /**
     * Sets the handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     *
     * @param differenceHandler The handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     */
    @Override
    public void setDifferenceHandler(DifferenceHandler<? extends DifferenceEngine> differenceHandler)
    {
        this.differenceHandler = differenceHandler;
    }

    /**
     * Gets the handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     *
     * @return The handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     */
    @Override
    public ComparisonHandler<? extends ComparisonEngine> getComparisonHandler()
    {
        return this.comparisonHandler;
    }

    /**
     * Sets the handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     *
     * @param comparisonHandler The handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     */
    @Override
    public void setComparisonHandler(ComparisonHandler<? extends ComparisonEngine> comparisonHandler)
    {
        this.comparisonHandler = comparisonHandler;
    }

    /**
     * Gets the handler to use for merges.
     *
     * @return The handler to use for merges.
     */
    @Override
    public MergeHandler<? extends MergeEngine> getMergeHandler()
    {
        return this.mergeHandler;
    }

    /**
     * Sets the handler to use for merges.
     *
     * @param mergeHandler The handler to use for merges.
     */
    @Override
    public void setMergeHandler(MergeHandler<? extends MergeEngine> mergeHandler)
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
    public MemorySearchQuery prepareSearchQuery(SearchQueryDefinition searchQueryDefinition)
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
    public MemorySearchResults searchWithQuery(MemorySearchQuery searchQuery, SearchParameters overrideParameters)
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
    public MemorySearchResults search(SearchQueryDefinition searchQueryDefinition)
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
    public MemorySearchResults search(SearchQueryDefinition searchQueryDefinition, SearchParameters overrideParameters)
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
     * @return The commit that was performed for the merge.
     */
    @Override
    public MemoryCommit mergeIntoBranchFromAnotherBranch(String destinationBranchName, String sourceBranchName, String message)
    {
        return this.getEngine().mergeIntoBranchFromAnotherBranch(destinationBranchName, sourceBranchName, message, mergeHandler, getComparisonHandler(), getDifferenceHandler(), this, this::createArea, this::createContent, getByteArrayIndex(), getClock());
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
    public Clock<? extends Timestamp> getClock()
    {
        return clock;
    }

    /**
     * Sets the clock that we use when we create commits.
     *
     * @param clock The clock that we use when we create commits.
     */
    public void setClock(Clock<? extends Timestamp> clock)
    {
        this.clock = clock;
    }

    /**
     * Gets the encoding to use when committing strings.
     *
     * @return The encoding to use when committing strings.
     */
    public Charset getEncoding()
    {
        return encoding;
    }

    /**
     * Sets the encoding to use when committing strings.
     *
     * @param encoding The encoding to use when committing strings.
     */
    public void setEncoding(Charset encoding)
    {
        this.encoding = encoding;
    }

}
