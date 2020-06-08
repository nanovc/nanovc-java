package io.nanovc.searches.commits;

import org.junit.jupiter.api.Test;
import io.nanovc.SearchQueryDefinition;
import io.nanovc.searches.commits.expressions.AllRepoCommitsExpression;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests creating {@link SimpleSearchQueryDefinition}'s.
 */
public class SimpleSearchQueryDefinitionTests
{
    @Test
    public void singleCommitQuery()
    {
        SearchQueryDefinition definition = SimpleSearchQueryDefinition.forSingleCommit(AllRepoCommitsExpression.allRepoCommits().tip());
        assertEquals("tipOf([All Repo Commits])", definition.toString());
    }

    @Test
    public void listOfCommitsQuery()
    {
        SearchQueryDefinition definition = SimpleSearchQueryDefinition.forListOfCommits(AllRepoCommitsExpression.allRepoCommits());
        assertEquals("[All Repo Commits]", definition.toString());
    }

    @Test
    public void singleCommitAndListOfCommitsQuery()
    {
        SearchQueryDefinition definition = new SimpleSearchQueryDefinition(
            AllRepoCommitsExpression.allRepoCommits().tip(),
            AllRepoCommitsExpression.allRepoCommits(),
            new HashMapSearchParameters()
            );
        assertEquals("tipOf([All Repo Commits])", definition.toString());
    }

}
