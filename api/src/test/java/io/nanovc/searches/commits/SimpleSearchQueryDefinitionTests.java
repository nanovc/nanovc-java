package io.nanovc.searches.commits;

import org.junit.jupiter.api.Test;
import io.nanovc.SearchQueryDefinitionAPI;
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
        SearchQueryDefinitionAPI definition = SimpleSearchQueryDefinition.forSingleCommit(AllRepoCommitsExpression.allRepoCommits().tip());
        assertEquals("tipOf([All Repo Commits])", definition.toString());
    }

    @Test
    public void listOfCommitsQuery()
    {
        SearchQueryDefinitionAPI definition = SimpleSearchQueryDefinition.forListOfCommits(AllRepoCommitsExpression.allRepoCommits());
        assertEquals("[All Repo Commits]", definition.toString());
    }

    @Test
    public void singleCommitAndListOfCommitsQuery()
    {
        SearchQueryDefinitionAPI definition = new SimpleSearchQueryDefinition(
            AllRepoCommitsExpression.allRepoCommits().tip(),
            AllRepoCommitsExpression.allRepoCommits(),
            new HashMapSearchParameters()
            );
        assertEquals("tipOf([All Repo Commits])", definition.toString());
    }

}
