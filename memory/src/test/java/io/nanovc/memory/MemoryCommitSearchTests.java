package io.nanovc.memory;

import io.nanovc.Commit;
import io.nanovc.RepoPath;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.searches.commits.SimpleSearchQueryDefinition;
import io.nanovc.searches.commits.expressions.AllRepoCommitsExpression;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the API for searching through {@link Commit}'s.
 */
public class MemoryCommitSearchTests extends MemoryNanoVersionControlTestsBase
{
    @Test
    public void testSearchingForAllCommits()
    {
        // Create the repo:
        MemoryNanoRepo nanoRepo = new MemoryNanoRepo();

        // Create some content:
        ByteArrayHashMapArea contentArea = nanoRepo.createArea();
        contentArea.putBytes(RepoPath.atRoot(), new byte[0]);

        // Commit the content to a branch several times to create some history:
        MemoryCommit commit1 = nanoRepo.commitToBranch(contentArea, "master", "First Commit");
        MemoryCommit commit2 = nanoRepo.commitToBranch(contentArea, "master", "Second Commit");
        MemoryCommit commit3 = nanoRepo.commitToBranch(contentArea, "master", "Third Commit");

        // Tag the second commit:
        nanoRepo.tagCommit(commit2, "Interesting");

        // Create a dangling commit:
        nanoRepo.commit(contentArea, "Dingly-Dangly");

        // Create the search query:
        SimpleSearchQueryDefinition searchQueryDefinition = new SimpleSearchQueryDefinition(
            null, AllRepoCommitsExpression.allRepoCommits(), null
        );

        // Run the search:
        MemorySearchResults searchResults = nanoRepo.search(searchQueryDefinition);

        // Get the commits from the search results:
        List<MemoryCommit> allCommits = searchResults.getCommits();

        // Map the results so we can assert them:
        String allCommitStrings = allCommits.stream().map(commit -> commit.message).collect(Collectors.joining("\n"));

        // Make sure the commits are as expected:
        String expectedCommitStrings = "First Commit\n" +
                                       "Second Commit\n" +
                                       "Third Commit\n" +
                                       "Dingly-Dangly";
        assertEquals(expectedCommitStrings, allCommitStrings);
    }
}
