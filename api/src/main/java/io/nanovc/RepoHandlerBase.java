/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;


import java.time.ZonedDateTime;

/**
 * The base class for repo handlers.
 * This represents the public API when working with {@link Repo}'s.
 * It holds common state including the {@link Repo} being worked on and the {@link RepoEngine} that contains the specific algorithm that we are interested in when working with the repo.
 * You can swap out the repo that is being worked on in cases where a correctly configured repo handler must work on multiple repo's.
 * The core functionality is delegated to the {@link RepoEngine} which is stateless and can be reused for multiple {@link Repo}'s and {@link RepoHandler}'s.
 *
 * @param <TContent>     The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>        The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>      The specific type of commit that is created in the repo.
 * @param <TSearchQuery> The specific type of search query that this engine returns.
 * @param <TRepo>        The specific type of repo that this handler manages.
 * @param <TEngine>      The specific type of engine that manipulates the repo.
 */
public abstract class RepoHandlerBase<
    TContent extends Content,
    TArea extends Area<TContent>,
    TCommit extends Commit,
    TSearchQuery extends SearchQuery<TCommit>,
    TSearchResults extends SearchResults<TCommit, TSearchQuery>,
    TRepo extends Repo<TContent, TArea, TCommit>,
    TEngine extends RepoEngine<TContent, TArea, TCommit, TSearchQuery, TSearchResults, TRepo>
    >
    implements RepoHandler<
    TContent,
    TArea,
    TCommit,
    TSearchQuery,
    TSearchResults,
    TRepo,
    TEngine
    >
{

    /**
     * The repo that is being managed.
     * One Repo Manager is needed for each Repo that is being managed.
     * This represents the common repo state that is implied for all the commands on the RepoManager.
     */
    protected TRepo repo;

    /**
     * The engine that is used to work with the repo.
     * An alternate (but compatible) engine can be plugged in to modify the algorithm being used for working with the repo.
     */
    protected TEngine engine;

    /**
     * The handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     */
    protected DifferenceHandler<? extends DifferenceEngine> differenceHandler;

    /**
     * The handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     */
    protected ComparisonHandler<? extends ComparisonEngine> comparisonHandler;

    /**
     * The handler to use for merging commits.
     */
    protected MergeHandler<? extends MergeEngine> mergeHandler;

    /**
     * The author that is used when creating new commits.
     */
    protected String author;

    /**
     * The committer that is used when creating new commits.
     */
    protected String committer;

    /**
     * The override for time for commits.
     * This is useful to set in testing situations.
     * If this is null then the actual time when the commit is performed is used instead.
     */
    protected ZonedDateTime nowOverride;

    /**
     * Creates a new handler for Nano Version Control around the given {@link Repo} and {@link RepoEngine}.
     *
     * @param repo              The repo to manage.
     * @param repoEngine        The repo engine to use internally.
     * @param differenceHandler The handler to use for {@link Difference}s between {@link Area}s of {@link Content}.
     * @param comparisonHandler The handler to use for {@link Comparison}s between {@link Area}s of {@link Content}.
     * @param mergeHandler      The handler to use for merging commits.
     */
    public RepoHandlerBase(
        TRepo repo,
        TEngine repoEngine,
        DifferenceHandler<? extends DifferenceEngine> differenceHandler,
        ComparisonHandler<? extends ComparisonEngine> comparisonHandler,
        MergeHandler<? extends MergeEngine> mergeHandler
    )
    {
        this.repo = repo;
        this.engine = repoEngine;
        this.differenceHandler = differenceHandler;
        this.comparisonHandler = comparisonHandler;
        this.mergeHandler = mergeHandler;
    }

    /**
     * Creates a new handler for Nano Version Control around the given Repo and RepoEngine.
     * You need to set the repo, engine and sub handlers explicitly after calling this constructor.
     */
    public RepoHandlerBase()
    {
    }

    /**
     * Gets the repo that is being handled.
     *
     * @return The repo that is being handled.
     */
    @Override
    public TRepo getRepo()
    {
        return this.repo;
    }

    /**
     * Sets the repo that is being handled.
     *
     * @param repo The repo that is being handled.
     */
    @Override
    public void setRepo(TRepo repo)
    {
        this.repo = repo;
    }

    /**
     * Gets the engine that is used to work with the repo.
     * An alternate (but compatible) engine can be plugged in to modify the algorithm being used for working with the repo.
     *
     * @return The engine that is used to work with the repo.
     */
    @Override
    public TEngine getEngine()
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
    public void setEngine(TEngine engine)
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
}
