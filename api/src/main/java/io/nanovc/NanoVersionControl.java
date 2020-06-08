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
