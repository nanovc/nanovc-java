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
import io.nanovc.areas.ByteArrayAreaAPI;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.clocks.ClockWithVMNanos;
import io.nanovc.content.ByteArrayContent;
import io.nanovc.epochs.EpochWithVMNanos;
import io.nanovc.ByteArrayIndex;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import io.nanovc.searches.commits.HashMapSearchParameters;
import io.nanovc.searches.commits.expressions.AllRepoCommitsExpression;
import io.nanovc.searches.commits.expressions.CommitsExpression;
import io.nanovc.searches.commits.expressions.Expression;
import io.nanovc.searches.commits.expressions.TipOfExpression;
import io.nanovc.timestamps.TimestampWithVMNanos;

import java.util.*;

/**
 * The base class for the engine for working with a nano version control repository in memory.
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
public abstract class MemoryRepoEngineBase<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends MemoryCommitAPI<TCommit>,
    TSearchQuery extends MemorySearchQueryAPI<TCommit>,
    TSearchResults extends MemorySearchResultsAPI<TCommit, TSearchQuery>,
    TRepo extends MemoryRepoAPI<TContent, TArea, TCommit>
    >
    extends RepoEngineBase<
    TContent,
    TArea,
    TCommit,
    TSearchQuery,
    TSearchResults,
    TRepo
    >
    implements MemoryRepoEngineAPI<
    TContent,
    TArea,
    TCommit,
    TSearchQuery,
    TSearchResults,
    TRepo
    >
{
    /**
     * Creates a new content area.
     *
     * @param areaSupplier The user specified factory method for the specific type of content area to create.
     * @return A new content area.
     */
    public TArea createArea(AreaFactory<TContent, TArea> areaSupplier)
    {
        return areaSupplier.createArea();
    }

    /**
     * Creates a new instance of the content with the given bytes.
     *
     * @param bytes          The bytes to use for the content.
     * @param contentFactory The user supplied content factory to use.
     * @return A new instance of the content for the given bytes.
     */
    @Override
    public TContent createContent(byte[] bytes, ContentFactory<TContent> contentFactory)
    {
        return contentFactory.createContent(bytes);
    }

    /**
     * A factory method for an area to use for snapshots.
     * This is used when we are committing content and the snapshot is used for the {@link MemoryCommit#snapshot}.
     *
     * @return A new empty area for snapshots.
     */
    public ByteArrayAreaAPI createSnapshotArea()
    {
        return new ByteArrayHashMapArea();
    }

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
    public TCommit commit(TArea contentAreaToCommit, String message, TRepo repo, ByteArrayIndex byteArrayIndex, ClockAPI<? extends TimestampAPI> clock)
    {
        // Create the specific commit:
        TCommit commit = constructCommit(contentAreaToCommit, message, byteArrayIndex, clock);

        // Save this commit as a new dangling commit for the repo because there is no branch or tag pointing at it:
        repo.getDanglingCommits().add(commit);

        // Return the commit that was created:
        return commit;
    }

    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     * It tracks the given commit as the parent.
     *
     * @param contentAreaToCommit The content area to commit to the repo.
     * @param message             The commit message.
     * @param repo               The repo to commit the content area to.
     * @param byteArrayIndex      The byte array index to use when creating snap-shots for the content.
     * @param clock               The clock to use for generating the timestamp for the commit.
     * @param parentCommit        The parent commit that we want to make this commit from.
     * @return The commit for this content area.
     */
    @Override
    public TCommit commit(TArea contentAreaToCommit, String message, TRepo repo, ByteArrayIndex byteArrayIndex, ClockAPI<? extends TimestampAPI> clock, TCommit parentCommit)
    {
        // Create the specific commit:
        TCommit commit = constructCommit(contentAreaToCommit, message, byteArrayIndex, clock);

        // Keep track of the parent commit:
        commit.setFirstParent(parentCommit);

        // Get the dangling commits for the repo:
        LinkedHashSet<TCommit> danglingCommits = repo.getDanglingCommits();

        // Save this commit as a new dangling commit for the repo because there is no branch or tag pointing at it:
        danglingCommits.add(commit);

        // Remove the parent commit from the dangling commits if it is there:
        danglingCommits.remove(parentCommit);

        // Return the commit that was created:
        return commit;
    }

    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     * It tracks the given commit as the parent.
     *
     * @param contentAreaToCommit The content area to commit to the repo.
     * @param message             The commit message.
     * @param repo               The repo to commit the content area to.
     * @param byteArrayIndex      The byte array index to use when creating snap-shots for the content.
     * @param clock               The clock to use for generating the timestamp for the commit.
     * @param firstParentCommit   The parent commit that we want to make this commit from.
     * @param otherParentCommits  The other parents to have in addition to the first parent commit.
     * @return The commit for this content area.
     */
    @Override
    public TCommit commit(TArea contentAreaToCommit, String message, TRepo repo, ByteArrayIndex byteArrayIndex, ClockAPI<? extends TimestampAPI> clock, TCommit firstParentCommit, List<TCommit> otherParentCommits)
    {
        // Create the specific commit:
        TCommit commit = constructCommit(contentAreaToCommit, message, byteArrayIndex, clock);

        // Keep track of the parent commits:
        commit.setFirstParent(firstParentCommit);
        commit.setOtherParents(otherParentCommits);

        // Get the dangling commits for the repo:
        LinkedHashSet<TCommit> danglingCommits = repo.getDanglingCommits();

        // Save this commit as a new dangling commit for the repo because there is no branch or tag pointing at it:
        danglingCommits.add(commit);

        // Remove the parent commits from the dangling commits if they are there because they are no longer at the tip:
        danglingCommits.remove(firstParentCommit);
        danglingCommits.removeAll(otherParentCommits);

        // Return the commit that was created:
        return commit;
    }

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
    public TCommit commitToBranch(TArea contentAreaToCommit, String branchName, String message, TRepo repo, ByteArrayIndex byteArrayIndex, ClockAPI<? extends TimestampAPI> clock)
    {
        // Create the specific commit:
        TCommit commit = constructCommit(contentAreaToCommit, message, byteArrayIndex, clock);

        // Check whether we already have this branch and get the last commit if we do:
        TCommit previousCommit = repo.getBranchTips().get(branchName);
        if (previousCommit != null)
        {
            // We have a commit for this branch already.

            // Save a reference to the previous commit as the parent for the new commit:
            commit.setFirstParent(previousCommit);
        }

        // Save this commit as the tip for this branch:
        createBranchAtCommit(commit, branchName, repo);

        // Return the commit that was created:
        return commit;
    }


    /**
     * Creates a new branch with the given name and makes it point at the given commit.
     * If the repo already has a branch with this name then it is updated to point at this commit.
     *
     * @param commit     The commit where the new branch should be created.
     * @param branchName The name of the branch to create at the commit.
     * @param repo       The repo to update with the new branch.
     */
    @Override
    public void createBranchAtCommit(TCommit commit, String branchName, TRepo repo)
    {
        // Save this commit as the tip for this branch:
        repo.getBranchTips().put(branchName, commit);
    }

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
    public TCommit constructCommit(TArea contentAreaToCommit, String message, ByteArrayIndex byteArrayIndex, ClockAPI<? extends TimestampAPI> clock)
    {
        // Get the timestamp of this commit:
        TimestampAPI timestamp = clock.now();

        // Create the specific commit:
        TCommit commit = this.createCommit();

        // Save the timestamp:
        commit.setTimestamp(timestamp);

        // Save the commit message:
        commit.setMessage(message);

        // Create an area for this snapshot:
        ByteArrayAreaAPI snapshotArea = createSnapshotArea();

        // Go through each entry in the content area that we need to commit:
        for (AreaEntry<TContent> areaEntry : contentAreaToCommit)
        {
            // Get the bytes for this content:
            final byte[] bytes = areaEntry.content.asByteArray();

            // Pass the bytes through our index to check if we can reuse anything from the index:
            final byte[] indexedBytes = byteArrayIndex.addOrLookup(bytes);

            // Add the content to the snapshot area:
            snapshotArea.putBytes(areaEntry.path, indexedBytes);
        }
        // Now we have created the snapshot.

        // Save the snapshot on the memory commit:
        commit.setSnapshot(snapshotArea);

        // Return the commit that was created:
        return commit;
    }


    /**
     * Checks out the content for the given commit into the given content area.
     *
     * @param commit         The commit to check out.
     * @param repo           The repo to check out from.
     * @param areaToUpdate   The area to update with the content for the commit.
     * @param contentFactory The content factory to use when populating the content area.
     */
    public void checkoutIntoArea(TCommit commit, TRepo repo, TArea areaToUpdate, ContentFactory<TContent> contentFactory)
    {
        // Go through the content in the snapshot for the commit:
        for (AreaEntry<ByteArrayContent> snapshotEntry : commit.getSnapshot())
        {
            // Create the specifically typed content for the destination area:
            TContent destinationContent = createContent(snapshotEntry.content.getEfficientByteArray(), contentFactory);

            // Put the content into the destination area:
            areaToUpdate.putContent(snapshotEntry.path, destinationContent);
        }
    }

    /**
     * Checks out the content for the given commit into a new content area.
     *
     * @param commit         The commit to check out.
     * @param repo           The repo to check out from.
     * @param areaFactory    The user specified factory method for the specific type of content area to create.
     * @param contentFactory The content factory to use when populating the content area.
     * @return A new content area with the content from the checkout.
     */
    public TArea checkout(TCommit commit, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory)
    {
        // Create a new content area for the destination of the checkout:
        TArea area = createArea(areaFactory);

        // Checkout the content into this area:
        checkoutIntoArea(commit, repo, area, contentFactory);

        return area;
    }

    /**
     * Creates a new byte array index that can be used for providing Value-Equality semantics for the byte arrays we have in the repo.
     *
     * @return A new byte array index to use.
     */
    public ByteArrayIndex createByteArrayIndex()
    {
        return new HashWrapperByteArrayIndex();
    }

    /**
     * Creates a new clock for creating timestamps.
     *
     * @return A new clock for creating timestamps.
     */
    @Override
    public ClockAPI<? extends TimestampAPI> createClock()
    {
        return new ClockWithVMNanos();
    }

    /**
     * Gets the latest commit for the branch with the given name.
     *
     * @param branchName The name of the branch to get the latest commit for.
     * @param repo       The repo to get the commit from.
     * @return The latest commit for the given branch. Null if there is no branch with the given name.
     */
    public TCommit getLatestCommitForBranch(String branchName, TRepo repo)
    {
        // Get the latest commit (tip) of the branch if it exists:
        return repo.getBranchTips().get(branchName);
    }

    /**
     * Gets the set of branch names in the repo.
     * The set is a snapshot of the names that currently exist for the repo.
     *
     * @param repo The repo to get the branch names from.
     * @return The set of branch names in the repo. If there are no branches in the repo then an empty set is returned. The set returned is unmodifiable so that you have a snapshot of the branches at this point in time.
     */
    public Set<String> getBranchNames(TRepo repo)
    {
        // Get the branch names:
        // NOTE: We want a snapshot because we don't want any internal changes to bleed out or vice-versa.
        return new HashSet<>(repo.getBranchTips().keySet());
    }

    /**
     * Gets the set of tag names in the repo.
     * The set is a snapshot of the names that currently exist for the repo.
     *
     * @param repo The repo to get the tag names from.
     * @return The set of tag names in the repo. If there are no tags in the repo then an empty set is returned. The set returned is unmodifiable so that you have a snapshot of the tags at this point in time.
     */
    public Set<String> getTagNames(TRepo repo)
    {
        // Get the tag names:
        // NOTE: We want a snapshot because we don't want any internal changes to bleed out or vice-versa.
        return new HashSet<>(repo.getTags().keySet());
    }


    /**
     * Tags the commit with the given name.
     * This tag name can be used to reference a specific commit in the history, independently of the branches.
     *
     * @param repo    The repo to tag the commit in.
     * @param commit  The commit to tag with a name.
     * @param tagName The name of the tag to give to this commit.
     */
    public void tagCommit(TRepo repo, TCommit commit, String tagName)
    {
        repo.getTags().put(tagName, commit);
    }

    /**
     * Gets the commit with the given tag name.
     *
     * @param repo    The repo to get the tagged commit from.
     * @param tagName The name of the tagged commit.
     * @return The commit with the given tag name. Null if there is no tag with this name.
     */
    @Override
    public TCommit getCommitForTag(TRepo repo, String tagName)
    {
        return repo.getTags().get(tagName);
    }

    /**
     * Removes the tag with the given name from the repo.
     *
     * @param repo    The repo to remove the tag from.
     * @param tagName The name of the tag to remove. If this tag doesn't exist then nothing happens.
     */
    @Override
    public void removeTag(TRepo repo, String tagName)
    {
        repo.getTags().remove(tagName);
    }

    /**
     * Optimizes the timestamps for the repo.
     * This process might re-base epochs and timestamps to better reuse epochs.
     *
     * @param repo The repo to optimize the timestamps in.
     */
    @Override
    public void optimizeTimestamps(TRepo repo)
    {
        // Find all the commits that have timestamps and epoch's that are relative to the Virtual Machine nano seconds.

        // Create a set for all of the distinct commits:
        Set<TCommit> commits = new HashSet<>(repo.getDanglingCommits());

        // Create an identity map so that we don't walk loops in the commit graph:
        IdentityHashMap<TCommit, TCommit> identities = new IdentityHashMap<>();

        // Start walking each branch to search for all of the commits:
        for (TCommit commit : repo.getBranchTips().values())
        {
            // Walk this commit recursively:
            extractCommitsRecursively(commit, identities, commits);
        }
        // Now we have all the commits for the repo.

        // Keep track of the epoch with the smallest nano time window:
        EpochWithVMNanos bestEpochWithVMNanos = null;

        // Keep track of commits which had timestamps with Virtual Machine nano times:
        Set<TCommit> commitsToProcess = new HashSet<>();

        // Go through each commit and find the epoch with the smallest Virtual Machine nano time window:
        for (TCommit commit : commits)
        {
            // Check whether the commit has a timestamp that has a Virtual Machine nano time:
            if (commit.getTimestamp() instanceof TimestampWithVMNanos)
            {
                // This timestamp has Virtual Machine nano time:
                TimestampWithVMNanos timestampWithVMNanos = (TimestampWithVMNanos) commit.getTimestamp();

                // Save this commit as one of the ones that we are processing:
                commitsToProcess.add(commit);

                // Check whether this is our first epoch:
                if (bestEpochWithVMNanos == null)
                {
                    // This is our first epoch.
                    bestEpochWithVMNanos = timestampWithVMNanos.epoch;
                }
                else
                {
                    // This is not our first epoch.

                    // Check whether this timestamps epoch is different to the best epoch so far:
                    if (timestampWithVMNanos.epoch != bestEpochWithVMNanos)
                    {
                        // This timestamp has a different epoch to the best epoch.

                        // Check whether this epoch has a window that is lest than our best:
                        if (timestampWithVMNanos.epoch.getNanoTimeDurationLong() < bestEpochWithVMNanos.getNanoTimeDurationLong())
                        {
                            // We have a new best epoch with the smallest window.

                            // Save this as the best epoch:
                            bestEpochWithVMNanos = timestampWithVMNanos.epoch;
                        }
                    }
                }
            }
        }
        // Now we have picked out all the commits that we want to re-base and we have the epoch with the smallest window.

        // Go through each commit and re-base the epochs:
        for (TCommit commit : commitsToProcess)
        {
            // Check whether the commit has a timestamp that has a Virtual Machine nano time:
            if (commit.getTimestamp() instanceof TimestampWithVMNanos)
            {
                // This timestamp has Virtual Machine nano time:
                TimestampWithVMNanos timestampWithVMNanos = (TimestampWithVMNanos) commit.getTimestamp();

                // Check whether the epoch is different to the best epoch:
                if (timestampWithVMNanos.epoch != bestEpochWithVMNanos)
                {
                    // The epoch for this timestamp is different to the best epoch.

                    // Create a new timestamp which is relative to the best epoch:
                    TimestampWithVMNanos rebasedTimestamp = new TimestampWithVMNanos(bestEpochWithVMNanos, timestampWithVMNanos.nanoTime);

                    // Update the timestamp on the commit:
                    commit.setTimestamp(rebasedTimestamp);
                }
            }
        }
    }

    /**
     * Computes a difference between the given areas.
     * The areas could have come from anywhere.
     *
     * @param fromArea          The first area to find differences from.
     * @param toArea            The second area to find differences to.
     * @param differenceHandler The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @return The differences between the given areas.
     */
    @Override
    public DifferenceAPI computeDifferenceBetweenAreas(AreaAPI<? extends TContent> fromArea, AreaAPI<? extends TContent> toArea, DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler)
    {
        return differenceHandler.computeDifference(fromArea, toArea);
    }

    /**
     * Computes a difference between the given commits.
     * It is assumed that the commits come from this repo.
     *
     * @param fromCommit        The first commit to find differences from.
     * @param toCommit          The second commit to find differences to.
     * @param differenceHandler The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @param repo              The repo to check out from.
     * @param areaFactory       The user specified factory method for the specific type of content area to create.
     * @param contentFactory    The content factory to use when populating the content area.
     * @return The differences between the given commits.
     */
    @Override
    public DifferenceAPI computeDifferenceBetweenCommits(TCommit fromCommit, TCommit toCommit, DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory)
    {
        // Get the areas for each commit:
        TArea fromArea = checkout(fromCommit, repo, areaFactory, contentFactory);
        TArea toArea = checkout(toCommit, repo, areaFactory, contentFactory);

        // Delegate to the other implementation:
        return computeDifferenceBetweenAreas(fromArea, toArea, differenceHandler);
    }

    /**
     * Computes a difference between the given branches.
     *
     * @param fromBranchName    The first branch to find differences from.
     * @param toBranchName      The second branch to find differences to.
     * @param differenceHandler The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @param repo              The repo to check out from.
     * @param areaFactory       The user specified factory method for the specific type of content area to create.
     * @param contentFactory    The content factory to use when populating the content area.
     * @return The differences between the given branches.
     */
    @Override
    public DifferenceAPI computeDifferenceBetweenBranches(String fromBranchName, String toBranchName, DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory)
    {
        // Get the commits for each branch:
        TCommit fromCommit = getLatestCommitForBranch(fromBranchName, repo);
        TCommit toCommit = getLatestCommitForBranch(toBranchName, repo);

        // Delegate to the other implementation:
        return computeDifferenceBetweenCommits(fromCommit, toCommit, differenceHandler, repo, areaFactory, contentFactory);
    }

    /**
     * Computes a comparison between the given areas.
     * The areas could have come from anywhere.
     *
     * @param fromArea          The first area to find comparisons from.
     * @param toArea            The second area to find comparisons to.
     * @param comparisonHandler The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @return The comparisons between the given areas.
     */
    @Override
    public ComparisonAPI computeComparisonBetweenAreas(AreaAPI<? extends TContent> fromArea, AreaAPI<? extends TContent> toArea, ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler)
    {
        return comparisonHandler.compare(fromArea, toArea);
    }

    /**
     * Computes a comparison between the given commits.
     * It is assumed that the commits come from this repo.
     *
     * @param fromCommit        The first commit to find comparisons from.
     * @param toCommit          The second commit to find comparisons to.
     * @param comparisonHandler The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @param repo              The repo to check out from.
     * @param areaFactory       The user specified factory method for the specific type of content area to create.
     * @param contentFactory    The content factory to use when populating the content area.
     * @return The comparisons between the given commits.
     */
    @Override
    public ComparisonAPI computeComparisonBetweenCommits(TCommit fromCommit, TCommit toCommit, ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory)
    {
        // Get the areas for each commit:
        TArea fromArea = checkout(fromCommit, repo, areaFactory, contentFactory);
        TArea toArea = checkout(toCommit, repo, areaFactory, contentFactory);

        // Delegate to the other implementation:
        return computeComparisonBetweenAreas(fromArea, toArea, comparisonHandler);
    }

    /**
     * Computes a comparison between the given branches.
     *
     * @param fromBranchName    The first branch to find comparisons from.
     * @param toBranchName      The second branch to find comparisons to.
     * @param comparisonHandler The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @param repo              The repo to check out from.
     * @param areaFactory       The user specified factory method for the specific type of content area to create.
     * @param contentFactory    The content factory to use when populating the content area.
     * @return The comparisons between the given branches.
     */
    @Override
    public ComparisonAPI computeComparisonBetweenBranches(String fromBranchName, String toBranchName, ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory)
    {
        // Get the commits for each branch:
        TCommit fromCommit = getLatestCommitForBranch(fromBranchName, repo);
        TCommit toCommit = getLatestCommitForBranch(toBranchName, repo);

        // Delegate to the other implementation:
        return computeComparisonBetweenCommits(fromCommit, toCommit, comparisonHandler, repo, areaFactory, contentFactory);
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
        // Create the specific search query instance:
        TSearchQuery searchQuery = createSearchQuery(searchQueryDefinition);

        //TODO: It would be good to optimize the search query based on common expression reductions.

        return searchQuery;
    }

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
    @Override
    public TSearchResults searchWithQuery(TSearchQuery searchQuery, SearchParametersAPI overrideParameters, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory)
    {
        // Create the specific search results:
        TSearchResults searchResults = createSearchResults(searchQuery);

        // Get the default parameters from the search query definition:
        SearchParametersAPI parameters = searchQuery.getDefinition().getParameters();

        // Check whether we need to override the parameters:
        if (overrideParameters != null)
        {
            // We need to override some parameters.

            // Merge the override parameters onto the
            HashMapSearchParameters updatedSearchParameters = new HashMapSearchParameters();
            updatedSearchParameters.putAll(parameters);
            updatedSearchParameters.putAll(overrideParameters);

            // Use the updated parameters:
            parameters = updatedSearchParameters;
        }
        // Now we have the parameters to use for this search.

        // Get the list of commits to add to:
        List<TCommit> commitsToAddTo = searchResults.getCommits();

        // Get the expression to search for:
        Expression<CommitAPI> singleCommitExpression = searchQuery.getDefinition().getSingleCommitExpression();
        if (singleCommitExpression != null)
        {
            // We must search for a single commit.

            // Start walking the expression to get a single commit:
            walkSingleCommitSearchExpression(singleCommitExpression, parameters, repo, commitsToAddTo);
        }
        else
        {
            // Get the expression for the list of commits:
            Expression<List<CommitAPI>> listOfCommitsExpression = searchQuery.getDefinition().getListOfCommitsExpression();

            // Start walking the expression to get a list of commits:
            walkListOfCommitsSearchExpression(listOfCommitsExpression, parameters, repo, commitsToAddTo);
        }

        return searchResults;
    }

    public void walkListOfCommitsSearchExpression(Expression<List<CommitAPI>> expression, SearchParametersAPI parameters, TRepo repo, List<TCommit> commitsToAddTo)
    {

        switch (expression.getClass().getSimpleName())
        {
            case "AllRepoCommitsExpression":
                walkAllRepoCommitsExpression((AllRepoCommitsExpression) expression, parameters, repo, commitsToAddTo);
                break;
        }
    }

    public void walkSingleCommitSearchExpression(Expression<CommitAPI> expression, SearchParametersAPI parameters, TRepo repo, List<TCommit> commitsToAddTo)
    {
        switch (expression.getClass().getSimpleName())
        {
            case "TipOfExpression":
                walkTipOfExpression((TipOfExpression) expression, parameters, repo, commitsToAddTo);
                break;
        }
    }

    public void walkTipOfExpression(TipOfExpression expression, SearchParametersAPI parameters, TRepo repo, List<TCommit> commitsToAddTo)
    {
        // Get the operand to evaluate to get the list of commits to add to.
        CommitsExpression operand = expression.getOperand();

        // Walk the operand recursively:
        walkListOfCommitsSearchExpression(operand, parameters, repo, commitsToAddTo);

        // Get the tip of the collection:
        // NOTE: We assume that the oldest commits are first and new ones are last.
        TCommit lastCommit = commitsToAddTo.get(commitsToAddTo.size() - 1);

        // Clear the list:
        commitsToAddTo.clear();

        // Add back the last commit:
        commitsToAddTo.add(lastCommit);
    }

    public void walkAllRepoCommitsExpression(AllRepoCommitsExpression expression, SearchParametersAPI parameters, TRepo repo, List<TCommit> commitsToAddTo)
    {
        // Create a set of the commits that are ordered by the timestamp:
        TreeSet<TCommit> commitSet = new TreeSet<>(Comparator.comparing(tCommit -> tCommit.getTimestamp().getInstant()));

        // Keep track of commit instances we have already walked so that we don't traverse loops in the graph of commits:
        IdentityHashMap<TCommit, TCommit> identities = new IdentityHashMap<>();

        // Now we want to get all the commits for the repo.

        // Create an initial set of commits to start traversing:
        Set<TCommit> initialCommitSet = new LinkedHashSet<>();

        // Get the commits for each branch:
        initialCommitSet.addAll(repo.getBranchTips().values());

        // Get the commits for each tag:
        initialCommitSet.addAll(repo.getTags().values());

        // Get all the dangling commits that are not pointed to by a branch:
        initialCommitSet.addAll(repo.getDanglingCommits());

        // Walk the branches recursively and accumulate commits:
        for (TCommit tipCommit : initialCommitSet)
        {
            // Extract the commits for this commit recursively:
            extractCommitsRecursively(tipCommit, identities, commitSet);
        }
        // Now we have all of the commits for this repo.

        // Add all of the commits to the output in commit order:
        commitsToAddTo.addAll(commitSet);
    }

    /**
     * This extracts all the commits recursively by walking the parentage of the commit.
     * It guards against walking commits that it has walked before.
     *
     * @param currentCommit            The current commit that we are walking. This commit will be checked to see if it has already been seen before before processing it.
     * @param previouslySeenIdentities The map of commits that we have processed before. This is used to detect commits that we have seen before so we don't process them again. It also helps us avoid walking through cycles in the commit graph. We use an independent identity map so that we don't care how the specific set implements itself and we also don't care about how the content implements equality.
     * @param commitSetToAddTo         The set of commits to add to as we walk the history of the commits recursively.
     */
    public void extractCommitsRecursively(TCommit currentCommit, IdentityHashMap<TCommit, TCommit> previouslySeenIdentities, Set<TCommit> commitSetToAddTo)
    {
        // Check if we have seen this commit previously so that we can skip it if so:
        if (previouslySeenIdentities.containsKey(currentCommit)) return; // We have processed this commit already.
        // If we get here then we know that we have not seen this commit already.

        // Add this commit:
        previouslySeenIdentities.put(currentCommit, currentCommit);
        commitSetToAddTo.add(currentCommit);

        // Check if we have a parent commit to walk recursively:
        TCommit firstParent = currentCommit.getFirstParent();
        if (firstParent != null)
        {
            // Walk it's first parent recursively:
            extractCommitsRecursively(firstParent, previouslySeenIdentities, commitSetToAddTo);
        }

        // Walk any other parents recursively:
        List<TCommit> otherParents = currentCommit.getOtherParents();
        if (otherParents != null && otherParents.size() > 0)
        {
            // We have other parents to walk recursively.
            for (TCommit otherParent : otherParents)
            {
                // Walk the other parent recursively:
                extractCommitsRecursively(otherParent, previouslySeenIdentities, commitSetToAddTo);
            }
        }
        // When we get here we will have walked all the parent commits recursively for the current commit.
    }

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
    @Override
    public TCommit mergeIntoBranchFromAnotherBranch(String destinationBranchName, String sourceBranchName, String message, MergeHandlerAPI<? extends MergeEngineAPI> mergeHandler, ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler, DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory, ByteArrayIndex byteArrayIndex, ClockAPI<? extends TimestampAPI> clock)
    {
        // Get the commits for each branch:
        TCommit sourceCommit = getLatestCommitForBranch(sourceBranchName, repo);
        TCommit destinationCommit = getLatestCommitForBranch(destinationBranchName, repo);

        // Get the difference between the destination and source commits:
        DifferenceAPI differenceBetweenCommits = computeDifferenceBetweenCommits(destinationCommit, sourceCommit, differenceHandler, repo, areaFactory, contentFactory);

        // Check if there are any differences so that we can construct the content area that we want to commit:
        TArea mergedArea;
        if (differenceBetweenCommits.hasDifferences())
        {
            // We have differences between the commits.

            // Create a new area for the merged result:
            mergedArea = areaFactory.createArea();

            // Get the content areas at each of the commits:
            TArea sourceArea = checkout(sourceCommit, repo, areaFactory, contentFactory);
            TArea destinationArea = checkout(destinationCommit, repo, areaFactory, contentFactory);

            // Get the full comparison between the source and destination commits so that the merge is easier to perform:
            ComparisonAPI comparisonBetweenSourceAndDestination = computeComparisonBetweenAreas(destinationArea, sourceArea, comparisonHandler);

            // Find the common ancestor of the two commits:
            TCommit commonAncestorCommit = findCommonAncestorOfCommits(sourceCommit, destinationCommit);

            // Check if there is a common ancestor between the two commits:
            if (commonAncestorCommit == null)
            {
                // There is no common ancestor between the two commits.

                // Perform a two way merge:
                mergeHandler.mergeIntoAreaWithTwoWayDiff(
                    mergedArea,
                    sourceCommit, destinationCommit,
                    sourceArea, destinationArea,
                    comparisonBetweenSourceAndDestination,
                    contentFactory,
                    byteArrayIndex
                );

            }
            else
            {
                // There is a common ancestor between the two commits.

                // Perform a three way merge:

                // Check out the content area at the common ancestor:
                TArea commonAncestorArea = checkout(commonAncestorCommit, repo, areaFactory, contentFactory);

                // Get the difference between the common ancestor and the source commit:
                DifferenceAPI differenceBetweenAncestorAndSource = computeDifferenceBetweenAreas(commonAncestorArea, sourceArea, differenceHandler);

                // Get the difference between the common ancestor and the destination commit:
                DifferenceAPI differenceBetweenAncestorAndDestination = computeDifferenceBetweenAreas(commonAncestorArea, destinationArea, differenceHandler);

                // Perform the merge:
                mergeHandler.mergeIntoAreaWithThreeWayDiff(
                    mergedArea,
                    commonAncestorCommit, sourceCommit, destinationCommit,
                    commonAncestorArea, sourceArea, destinationArea,
                    comparisonBetweenSourceAndDestination,
                    differenceBetweenAncestorAndSource, differenceBetweenAncestorAndDestination,
                    contentFactory,
                    byteArrayIndex
                );
            }
        }
        else
        {
            // There are no differences between the commits.

            // Use the content area of the destination commit:
            mergedArea = checkout(destinationCommit, repo, areaFactory, contentFactory);
        }
        // Now we have the merged content in the merge area that we want to commit.


        // Commit the merged area:
        TCommit mergeCommit = commitToBranch(mergedArea, destinationBranchName, message, repo, byteArrayIndex, clock);

        // Add the source commit as another parent so that we can keep track of where we merged from:
        mergeCommit.setOtherParents(new ArrayList<>());
        mergeCommit.getOtherParents().add(sourceCommit);

        // Return the merged commit:
        return mergeCommit;
    }

    /**
     * Finds the common ancestor between the two commits.
     *
     * @param commit1 The first commit to start scanning through.
     * @param commit2 The second commit to start scanning through.
     * @return The common ancestor between the two commits. Null if there is no common commit. The first common ancestor is returned if there are multiple common ancestors.
     */
    public TCommit findCommonAncestorOfCommits(TCommit commit1, TCommit commit2)
    {
        // Create an identity map so that we know whether we have traversed commits from commit1 already:
        IdentityHashMap<TCommit, TCommit> identities1 = new IdentityHashMap();

        // Create the set of all parent commits for commit 1:
        HashSet<TCommit> parentCommits1 = new HashSet<>();

        // Extract all the parent commits from the given commit:
        extractCommitsRecursively(commit1, identities1, parentCommits1);

        // Create an identity map so that we know whether we have traversed commits from commit2 already:
        IdentityHashMap<TCommit, TCommit> identities2 = new IdentityHashMap();

        // Find a common ancestor recursively:
        return findCommonAncestorOfCommitsRecursive(commit2, identities2, identities1);
    }

    /**
     * This extracts all the commits recursively by walking the parentage of the commit.
     * It guards against walking commits that it has walked before.
     *
     * @param currentCommit               The current commit that we are walking. This commit will be checked to see if it has already been seen before before processing it.
     * @param previouslySeenIdentities    The map of commits that we have processed before. This is used to detect commits that we have seen before so we don't process them again. It also helps us avoid walking through cycles in the commit graph. We use an independent identity map so that we don't care how the specific set implements itself and we also don't care about how the content implements equality.
     * @param otherCommitsToSearchAgainst The map of commits from the other commit to search against. The first commit that we find that is in this list is considered a common ancestor.
     */
    private TCommit findCommonAncestorOfCommitsRecursive(TCommit currentCommit, IdentityHashMap<TCommit, TCommit> previouslySeenIdentities, IdentityHashMap<TCommit, TCommit> otherCommitsToSearchAgainst)
    {
        // Check if we have seen this commit previously so that we can skip it if so:
        if (previouslySeenIdentities.containsKey(currentCommit)) return null; // We have processed this commit already.
        // If we get here then we know that we have not seen this commit already.

        // Add this commit:
        previouslySeenIdentities.put(currentCommit, currentCommit);

        // Check whether this commit is in the list of commits to search through:
        if (otherCommitsToSearchAgainst.containsKey(currentCommit))
        {
            // We found the common ancestor:
            return currentCommit;
        }
        // If we get here then this commit is not the common ancestor.

        // Check if we have a parent commit to walk recursively:
        TCommit firstParent = currentCommit.getFirstParent();
        if (firstParent != null)
        {
            // Walk it's first parent recursively:
            TCommit commonAncestor = findCommonAncestorOfCommitsRecursive(firstParent, previouslySeenIdentities, otherCommitsToSearchAgainst);

            // Check if we found the first common ancestor:
            if (commonAncestor != null)
            {
                // We found the common ancestor:
                return commonAncestor;
            }
        }

        // Walk any other parents recursively:
        List<TCommit> otherParents = currentCommit.getOtherParents();
        if (otherParents != null && otherParents.size() > 0)
        {
            // We have other parents to walk recursively.
            for (TCommit otherParent : otherParents)
            {
                // Walk the other parent recursively:
                TCommit commonAncestor = findCommonAncestorOfCommitsRecursive(otherParent, previouslySeenIdentities, otherCommitsToSearchAgainst);

                // Check if we found the first common ancestor:
                if (commonAncestor != null)
                {
                    // We found the common ancestor:
                    return commonAncestor;
                }
            }
        }
        // When we get here we will have walked all the parent commits recursively for the current commit.

        // Flag that there are no common ancestors:
        return null;
    }

    /**
     * Casts or clones the given area to the specific type required by this repo handler.
     *
     * @param areaToCastOrClone The area to cast if it is already the required type or to clone if it is a different area type.
     * @param areaFactory       The factory method to use for creating the specific area that we want.
     * @param contentFactory    The factory method to use for creating the specific content that we want.
     * @param byteArrayIndex    The byte array index to use that allows us to re-use byte arrays that we have seen before in the repo.
     * @return A compatible area for the repo handler which is either a cast of the same instance or a completely new clone of it if it is an incompatible type.
     */
    @Override
    public TArea castOrCloneArea(AreaAPI<? extends ContentAPI> areaToCastOrClone, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory, ByteArrayIndex byteArrayIndex)
    {
        // Create the area that we want:
        TArea clonedArea = areaFactory.createArea();

        // Start cloning the content:
        for (AreaEntry<? extends ContentAPI> areaEntry : areaToCastOrClone)
        {
            // Check whether we have already seen these bytes in the byte index:
            byte[] bytes = byteArrayIndex.addOrLookup(areaEntry.content.asByteArray());

            // Create the specific type of content that we need:
            TContent clonedContent = contentFactory.createContent(bytes);

            // Add the content to the area:
            clonedArea.putContent(areaEntry.path, clonedContent);
        }

        return clonedArea;
    }
}
