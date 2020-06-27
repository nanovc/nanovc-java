/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;


import java.util.List;
import java.util.Set;

/**
 * The interface for repo handlers.
 * This represents the public API when working with {@link RepoAPI}'s.
 * It holds common state including the {@link RepoAPI} being worked on and the {@link RepoEngineAPI} that contains the specific algorithm that we are interested in when working with the repo.
 * You can swap out the repo that is being worked on in cases where a correctly configured repo handler must work on multiple repo's.
 * The core functionality is delegated to the {@link RepoEngineAPI} which is stateless and can be reused for multiple {@link RepoAPI}'s and {@link RepoHandlerAPI}'s.
 *
 * @param <TContent>     The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>        The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>      The specific type of commit that is created in the repo.
 * @param <TSearchQuery> The specific type of search query that this engine returns.
 * @param <TRepo>        The specific type of repo that this handler manages.
 * @param <TEngine>      The specific type of engine that manipulates the repo.
 */
public interface RepoHandlerAPI<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends CommitAPI,
    TSearchQuery extends SearchQueryAPI<TCommit>,
    TSearchResults extends SearchResultsAPI<TCommit, TSearchQuery>,
    TRepo extends RepoAPI<TContent, TArea, TCommit>,
    TEngine extends RepoEngineAPI<TContent, TArea, TCommit, TSearchQuery, TSearchResults, TRepo>
    >
{
    /**
     * Creates a new area where content can be placed.
     *
     * @return A new content area that can be used for committing.
     */
    TArea createArea();

    /**
     * Commit the given content to the repo.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @return The commit for this content.
     */
    TCommit commit(TArea contentAreaToCommit, String message);

    /**
     * Commit the given content to the repo.
     * It tracks the given commit as the parent.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param message             The commit message.
     * @param parentCommit        The parent commit that we want to make this commit from.
     * @return The commit for this content.
     */
    TCommit commit(TArea contentAreaToCommit, String message, TCommit parentCommit);

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
    TCommit commit(TArea contentAreaToCommit, String message, TCommit firstParentCommit, TCommit... otherParentCommits);

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
    TCommit commit(TArea contentAreaToCommit, String message, TCommit firstParentCommit, List<TCommit> otherParentCommits);

    /**
     * Commit the given content to the given branch in the the repo.
     *
     * @param contentAreaToCommit The content area to commit to version control.
     * @param branch              The branch to commit to. If the branch doesn't exist, it is created.
     * @param message             The commit message.
     * @return The commit for this content.
     */
    TCommit commitToBranch(TArea contentAreaToCommit, String branch, String message);

    /**
     * Creates a new branch with the given name and makes it point at the given commit.
     * If the repo already has a branch with this name then it is updated to point at this commit.
     *
     * @param commit     The commit where the new branch should be created.
     * @param branchName The name of the branch to create at the commit.
     */
    void createBranchAtCommit(TCommit commit, String branchName);

    /**
     * Gets the latest commit for the branch with the given name.
     *
     * @param branchName The name of the branch to get the latest commit for.
     * @return The latest commit for the given branch. Null if there is no branch with the given name.
     */
    TCommit getLatestCommitForBranch(String branchName);

    /**
     * Checks out the content for the given commit into the given content area.
     *
     * @param commit       The commit to check out.
     * @param areaToUpdate The area to update with the content for the commit.
     */
    void checkoutIntoArea(TCommit commit, TArea areaToUpdate);

    /**
     * Checks out the content for the given commit into a new content area.
     *
     * @param commit The commit to check out.
     * @return A new content area with the content from the checkout.
     */
    TArea checkout(TCommit commit);

    /**
     * Tags the commit with the given name.
     * This tag name can be used to reference a specific commit in the history, independently of the branches.
     *
     * @param commit  The commit to tag with a name.
     * @param tagName The name of the tag to give to this commit.
     */
    void tagCommit(TCommit commit, String tagName);

    /**
     * Gets the commit with the given tag name.
     *
     * @param tagName The name of the tagged commit.
     * @return The commit with the given tag name. Null if there is no tag with this name.
     */
    TCommit getCommitForTag(String tagName);

    /**
     * Removes the tag with the given name from the repo.
     *
     * @param tagName The name of the tag to remove. If this tag doesn't exist then nothing happens.
     */
    void removeTag(String tagName);

    /**
     * Computes a difference between the given areas.
     * The areas could have come from anywhere.
     *
     * @param fromArea The first area to find differences from.
     * @param toArea   The second area to find differences to.
     * @return The differences between the given areas.
     */
    DifferenceAPI computeDifferenceBetweenAreas(AreaAPI<? extends TContent> fromArea, AreaAPI<? extends TContent> toArea);

    /**
     * Computes a difference between the given commits.
     * It is assumed that the commits come from this repo.
     *
     * @param fromCommit The first commit to find differences from.
     * @param toCommit   The second commit to find differences to.
     * @return The differences between the given commits.
     */
    DifferenceAPI computeDifferenceBetweenCommits(TCommit fromCommit, TCommit toCommit);

    /**
     * Computes a difference between the given branches.
     *
     * @param fromBranchName The first branch to find differences from.
     * @param toBranchName   The second branch to find differences to.
     * @return The differences between the given branches.
     */
    DifferenceAPI computeDifferenceBetweenBranches(String fromBranchName, String toBranchName);

    /**
     * Computes a comparison between the given areas.
     * The areas could have come from anywhere.
     *
     * @param fromArea The first area to find comparisons from.
     * @param toArea   The second area to find comparisons to.
     * @return The comparisons between the given areas.
     */
    ComparisonAPI computeComparisonBetweenAreas(AreaAPI<? extends TContent> fromArea, AreaAPI<? extends TContent> toArea);

    /**
     * Computes a comparison between the given commits.
     * It is assumed that the commits come from this repo.
     *
     * @param fromCommit The first commit to find comparisons from.
     * @param toCommit   The second commit to find comparisons to.
     * @return The comparisons between the given commits.
     */
    ComparisonAPI computeComparisonBetweenCommits(TCommit fromCommit, TCommit toCommit);

    /**
     * Computes a comparison between the given branches.
     *
     * @param fromBranchName The first branch to find comparisons from.
     * @param toBranchName   The second branch to find comparisons to.
     * @return The comparisons between the given branches.
     */
    ComparisonAPI computeComparisonBetweenBranches(String fromBranchName, String toBranchName);

    /**
     * Casts or clones the given area to the specific type required by this repo handler.
     * @param areaToCastOrClone The area to cast if it is already the required type or to clone if it is a different area type.
     * @return A compatible area for the repo handler which is either a cast of the same instance or a completely new clone of it if it is an incompatible type.
     */
    TArea castOrCloneArea(AreaAPI<? extends ContentAPI> areaToCastOrClone);

    /**
     * Gets the set of branch names in the repo.
     *
     * @return The set of branch names in the repo. If there are no branches in the repo then an empty set is returned.
     */
    Set<String> getBranchNames();

    /**
     * Gets the set of tag names in the repo.
     *
     * @return The set of tag names in the repo. If there are no tags in the repo then an empty set is returned.
     */
    Set<String> getTagNames();

    /**
     * Gets the repo that is being handled.
     *
     * @return The repo that is being handled.
     */
    TRepo getRepo();

    /**
     * Sets the repo that is being handled.
     *
     * @param repo The repo that is being handled.
     */
    void setRepo(TRepo repo);

    /**
     * Gets the engine that is used to work with the repo.
     * An alternate (but compatible) engine can be plugged in to modify the algorithm being used for working with the repo.
     *
     * @return The engine that is used to work with the repo.
     */
    TEngine getEngine();

    /**
     * Sets the engine that is used to work with the repo.
     * An alternate (but compatible) engine can be plugged in to modify the algorithm being used for working with the repo.
     *
     * @param engine The engine that is used to work with the repo.
     */
    void setEngine(TEngine engine);

    /**
     * Gets the handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     *
     * @return The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    DifferenceHandlerAPI<? extends DifferenceEngineAPI> getDifferenceHandler();

    /**
     * Sets the handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     *
     * @param differenceHandler The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    void setDifferenceHandler(DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler);

    /**
     * Gets the handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     *
     * @return The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    ComparisonHandlerAPI<? extends ComparisonEngineAPI> getComparisonHandler();

    /**
     * Sets the handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     *
     * @param comparisonHandler The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    void setComparisonHandler(ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler);

    /**
     * Gets the handler to use for merges.
     *
     * @return The handler to use for merges.
     */
    MergeHandlerAPI<? extends MergeEngineAPI> getMergeHandler();

    /**
     * Sets the handler to use for merges.
     *
     * @param mergeHandler The handler to use for merges.
     */
    void setMergeHandler(MergeHandlerAPI<? extends MergeEngineAPI> mergeHandler);

    /**
     * Prepares a reusable search query from the given search definition.
     * This search query can be thought of as the compiled/prepared search query.
     * The same search query can be run for multiple repo's without needing to recompute the search query each time.
     *
     * @param searchQueryDefinition The definition of the search to perform.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    TSearchQuery prepareSearchQuery(SearchQueryDefinitionAPI searchQueryDefinition);

    /**
     * Searches for commits that match the given search query.
     * Use this when you want to reuse the search query
     *
     * @param searchQuery The search query to reuse for this search.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    TSearchResults searchWithQuery(TSearchQuery searchQuery);

    /**
     * Searches for commits that match the given search query.
     * Use this when you want to reuse the search query
     *
     * @param searchQuery        The search query to reuse for this search.
     * @param overrideParameters Parameters to override the defaults of the search query with. Pass null to use the parameters in the search query.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    TSearchResults searchWithQuery(TSearchQuery searchQuery, SearchParametersAPI overrideParameters);

    /**
     * Searches for commits that match the given search definition.
     *
     * @param searchQueryDefinition The definition of the search to perform.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    TSearchResults search(SearchQueryDefinitionAPI searchQueryDefinition);

    /**
     * Searches for commits that match the given search definition.
     *
     * @param searchQueryDefinition The definition of the search to perform.
     * @param overrideParameters    Parameters to override the defaults of the search definition with. Pass null to use the parameters in the search definition.
     * @return The query for the search. This query can be evaluated multiple times on different repos. The query needs to be evaluated to get the results.
     */
    TSearchResults search(SearchQueryDefinitionAPI searchQueryDefinition, SearchParametersAPI overrideParameters);

    /**
     * Merges one branch into another.
     * The merge handler is used to resolve any merge conflicts if there are any.
     *
     * @param destinationBranchName The branch that we should merge into.
     * @param sourceBranchName      The branch that we should merge from.
     * @param message               The commit message to use for the merge.
     * @return The commit that was performed for the merge.
     */
    TCommit mergeIntoBranchFromAnotherBranch(String destinationBranchName, String sourceBranchName, String message);
}
