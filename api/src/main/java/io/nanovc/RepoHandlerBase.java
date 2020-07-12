/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;


/**
 * The base class for repo handlers.
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
public abstract class RepoHandlerBase<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends CommitAPI,
    TSearchQuery extends SearchQueryAPI<TCommit>,
    TSearchResults extends SearchResultsAPI<TCommit, TSearchQuery>,
    TRepo extends RepoAPI<TContent, TArea, TCommit>,
    TEngine extends RepoEngineAPI<TContent, TArea, TCommit, TSearchQuery, TSearchResults, TRepo>
    >
    implements RepoHandlerAPI<
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
     * The clock to use for creating timestamps.
     */
    protected ClockAPI<? extends TimestampAPI> clock;

    /**
     * The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    protected DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler;

    /**
     * The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     */
    protected ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler;

    /**
     * The handler to use for merging commits.
     */
    protected MergeHandlerAPI<? extends MergeEngineAPI> mergeHandler;

    /**
     * Creates a new handler for Nano Version Control around the given {@link RepoAPI} and {@link RepoEngineAPI}.
     *
     * @param repo              The repo to manage.
     * @param repoEngine        The repo engine to use internally.
     * @param clock             The clock to use for creating timestamps.
     * @param differenceHandler The handler to use for {@link DifferenceAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @param comparisonHandler The handler to use for {@link ComparisonAPI}s between {@link AreaAPI}s of {@link ContentAPI}.
     * @param mergeHandler      The handler to use for merging commits.
     */
    public RepoHandlerBase(
        TRepo repo,
        TEngine repoEngine,
        ClockAPI<? extends TimestampAPI> clock,
        DifferenceHandlerAPI<? extends DifferenceEngineAPI> differenceHandler,
        ComparisonHandlerAPI<? extends ComparisonEngineAPI> comparisonHandler,
        MergeHandlerAPI<? extends MergeEngineAPI> mergeHandler
    )
    {
        this.repo = repo;
        this.engine = repoEngine;
        this.clock = clock;
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
     * Gets the clock to use for creating timestamps.
     * @return The clock to use for creating timestamps.
     */
    @Override
    public ClockAPI<? extends TimestampAPI> getClock()
    {
        return clock;
    }

    /**
     * Sets the clock to use for creating timestamps.
     * @param clock The clock to use for creating timestamps.
     */
    @Override
    public void setClock(ClockAPI<? extends TimestampAPI> clock)
    {
        this.clock = clock;
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
}
