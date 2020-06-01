package io.nanovc.api;

/**
 * The entry point for Nano Version Control.
 * Nano Version Control is inspired by Git.
 * The idea is that each entity has an entire Git repo structure for the history.
 * The benefit of nano version control at the entity level is that each entity can be independently versioned
 * in it's entirety in memory.
 * No disk operations are required and there is no dependency between any sibling entities.
 *
 * See: https://git-scm.com/book/en/v2/Git-Internals-Git-Objects
 *
 * The RepoHandler can be thought of as the porcelain commands in git.
 * It makes use of the lower level plumbing commands to achieve higher-level objectives.
 */
public class NanoVersionControl
{
    /**
     * The URL to Nano Version Control on the web.
     */
    public static final String URL = "http://nanovc.io";
//
//    /**
//     * Creates a new handler for Nano Version Control.
//     * @return A new handler that can be used for Nano Version Control.
//     */
//    public static GitRepoHandler newHandler()
//    {
//        GitRepoHandler nanoHandler = new GitRepoHandler();
//        return nanoHandler;
//    }
}
