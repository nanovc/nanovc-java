/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.reflective;

import io.nanovc.Area;
import io.nanovc.Content;
import io.nanovc.RepoPath;
import io.nanovc.memory.MemoryCommitBase;
import io.nanovc.memory.MemoryRepoBase;

/**
 * The base class for an in-memory repo for storing objects as repo's using reflection.
 * NOTE: This repo represents the state of the version controlled data.
 *
 * @param <TContent> The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>    The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>  The specific type of commit that is created in the repo.
 */
public abstract class ReflectiveObjectMemoryRepoBase<
    TContent extends Content,
    TArea extends Area<TContent>,
    TCommit extends MemoryCommitBase<TCommit>
    >
    extends MemoryRepoBase<
    TContent,
    TArea,
    TCommit
    >
    implements ReflectiveObjectMemoryRepoAPI<
    TContent,
    TArea,
    TCommit
    >
{
    /**
     * The repo path to where the type of the object is stored.
     */
    private RepoPath objectTypeRepoPath = RepoPath.atRoot().resolve("type");

    /**
     * The repo path to where the content of the object is stored.
     */
    private RepoPath objectContentRepoPath = RepoPath.atRoot().resolve("fields");

    /**
     * Gets the repo path to where the type of the object is stored.
     *
     * @return The repo path to where the type of the object is stored.
     */
    @Override
    public RepoPath getObjectTypeRepoPath()
    {
        return objectTypeRepoPath;
    }

    /**
     * Gets the repo path to where the type of the object is stored.
     *
     * @param objectTypeRepoPath The repo path to where the type of the object is stored.
     */
    @Override
    public void setObjectTypeRepoPath(RepoPath objectTypeRepoPath)
    {
        this.objectTypeRepoPath = objectTypeRepoPath;
    }

    /**
     * Gets the repo path to where the content of the object is stored.
     *
     * @return The repo path to where the content of the object is stored.
     */
    @Override
    public RepoPath getObjectContentRepoPath()
    {
        return this.objectContentRepoPath;
    }

    /**
     * Gets the repo path to where the content of the object is stored.
     *
     * @param objectContentRepoPath The repo path to where the content of the object is stored.
     */
    @Override
    public void setObjectContentRepoPath(RepoPath objectContentRepoPath)
    {
        this.objectContentRepoPath = objectContentRepoPath;
    }
}
