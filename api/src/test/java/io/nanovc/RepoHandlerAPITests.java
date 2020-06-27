/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import io.nanovc.areas.StringHashMapArea;
import io.nanovc.content.StringContent;

import java.util.List;
import java.util.Set;

/**
 * Tests for the {@link RepoHandlerAPI}.
 */
public class RepoHandlerAPITests
{

    public void commitTests()
    {
        MockRepoHandler repoHandler = new MockRepoHandler();
    }

    //#region Mock Implementation

    private static class MockCommit extends CommitBase
    {
        @Override public String getMessage()
        {
            return null;
        }

        @Override public TimestampAPI getTimestamp()
        {
            return null;
        }
    }

    private static class MockSearchQuery extends SearchQueryBase<MockCommit>
    {
        public MockSearchQuery(SearchQueryDefinitionAPI definition)
        {
            super(definition);
        }
    }

    private static class MockSearchResults extends SearchResultsBase<MockCommit, MockSearchQuery>
    {
        public MockSearchResults(MockSearchQuery mockSearchQuery)
        {
            super(mockSearchQuery);
        }

        @Override public List<MockCommit> getCommits()
        {
            return null;
        }
    }

    private static class MockRepo extends RepoBase<StringContent, StringHashMapArea, MockCommit>
    {
    }

    private static class MockRepoEngine extends RepoEngineBase<StringContent, StringHashMapArea, MockCommit, MockSearchQuery, MockSearchResults, MockRepo>
    {
    }

    private static class MockRepoHandler
    extends RepoHandlerBase<StringContent, StringHashMapArea, MockCommit, MockSearchQuery, MockSearchResults, MockRepo, MockRepoEngine>
    {

        @Override public StringHashMapArea createArea()
        {
            return null;
        }

        @Override public MockCommit commit(StringHashMapArea contentAreaToCommit, String message)
        {
            return null;
        }

        @Override public MockCommit commit(StringHashMapArea contentAreaToCommit, String message, MockCommit parentCommit)
        {
            return null;
        }

        @Override public MockCommit commit(StringHashMapArea contentAreaToCommit, String message, MockCommit firstParentCommit, MockCommit... otherParentCommits)
        {
            return null;
        }

        @Override public MockCommit commit(StringHashMapArea contentAreaToCommit, String message, MockCommit firstParentCommit, List<MockCommit> otherParentCommits)
        {
            return null;
        }

        @Override public MockCommit commitToBranch(StringHashMapArea contentAreaToCommit, String branch, String message)
        {
            return null;
        }

        @Override public void createBranchAtCommit(MockCommit mockCommit, String branchName)
        {

        }

        @Override public MockCommit getLatestCommitForBranch(String branchName)
        {
            return null;
        }

        @Override public void checkoutIntoArea(MockCommit mockCommit, StringHashMapArea areaToUpdate)
        {

        }

        @Override public StringHashMapArea checkout(MockCommit mockCommit)
        {
            return null;
        }

        @Override public void tagCommit(MockCommit mockCommit, String tagName)
        {

        }

        @Override public MockCommit getCommitForTag(String tagName)
        {
            return null;
        }

        @Override public void removeTag(String tagName)
        {

        }

        @Override public DifferenceAPI computeDifferenceBetweenAreas(AreaAPI<? extends StringContent> fromArea, AreaAPI<? extends StringContent> toArea)
        {
            return null;
        }

        @Override public DifferenceAPI computeDifferenceBetweenCommits(MockCommit fromCommit, MockCommit toCommit)
        {
            return null;
        }

        @Override public DifferenceAPI computeDifferenceBetweenBranches(String fromBranchName, String toBranchName)
        {
            return null;
        }

        @Override public ComparisonAPI computeComparisonBetweenAreas(AreaAPI<? extends StringContent> fromArea, AreaAPI<? extends StringContent> toArea)
        {
            return null;
        }

        @Override public ComparisonAPI computeComparisonBetweenCommits(MockCommit fromCommit, MockCommit toCommit)
        {
            return null;
        }

        @Override public ComparisonAPI computeComparisonBetweenBranches(String fromBranchName, String toBranchName)
        {
            return null;
        }

        @Override public StringHashMapArea castOrCloneArea(AreaAPI<? extends ContentAPI> areaToCastOrClone)
        {
            return null;
        }

        @Override public Set<String> getBranchNames()
        {
            return null;
        }

        @Override public Set<String> getTagNames()
        {
            return null;
        }

        @Override public MockSearchQuery prepareSearchQuery(SearchQueryDefinitionAPI searchQueryDefinition)
        {
            return null;
        }

        @Override public MockSearchResults searchWithQuery(MockSearchQuery mockSearchQuery)
        {
            return null;
        }

        @Override public MockSearchResults searchWithQuery(MockSearchQuery mockSearchQuery, SearchParametersAPI overrideParameters)
        {
            return null;
        }

        @Override public MockSearchResults search(SearchQueryDefinitionAPI searchQueryDefinition)
        {
            return null;
        }

        @Override public MockSearchResults search(SearchQueryDefinitionAPI searchQueryDefinition, SearchParametersAPI overrideParameters)
        {
            return null;
        }

        @Override public MockCommit mergeIntoBranchFromAnotherBranch(String destinationBranchName, String sourceBranchName, String message)
        {
            return null;
        }

        //#endregion
    }
}
