/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * An area where {@link Content} resides.
 * If this was an actual git repository then this might be the file system where files would reside.
 * This is true for a working area, however it is logically true for the content of the index (staging area) too.
 * Most people would interact with a check-out of files into a working directory (working area) of the repo. That is a
 * content area too.
 *
 * @param <TContent> The type of content that is being stored in this content area.
 */
public interface Area<TContent extends Content> extends Iterable<AreaEntry<TContent>>
{
    /**
     * Returns true if the content area has any content or false if it is empty
     * @return true if any content exists in this content area.
     */
    boolean hasAnyContent();

    /**
     * Checks whether there is any content at the given path.
     * This method is expected to be cheaper than getting all the content using {@link #getContent(String)}.
     *
     * @param absolutePath The absolute path in the repo of the content to check.
     * @return True if there is content at the given path. False if there is no content at the path.
     */
    default boolean hasContent(String absolutePath)
    {
        return hasContent(RepoPath.at(absolutePath));
    }

    /**
     * Checks whether there is any content at the given path.
     * This method is expected to be cheaper than getting all the content using {@link #getContent(RepoPath)}.
     *
     * @param path The path of the content to get.
     * @return True if there is content at the given path. False if there is no content at the path.
     */
    boolean hasContent(RepoPath path);

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param absolutePath The absolute path in the repo where the content must be put.
     * @param content      The content to put.
     */
    default void putContent(String absolutePath, TContent content)
    {
        putContent(RepoPath.at(absolutePath), content);
    }

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param areaEntry The area entry to add to the area.
     */
    default void putContent(AreaEntry<TContent> areaEntry)
    {
        putContent(areaEntry.path, areaEntry.content);
    }

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param path    The path in the repo where the content must be put.
     * @param content The content to put.
     */
    void putContent(RepoPath path, TContent content);

    /**
     * Gets the content at the given path.
     *
     * @param absolutePath The absolute path in the repo of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    default TContent getContent(String absolutePath)
    {
        return getContent(RepoPath.at(absolutePath));
    }

    /**
     * Gets the content at the given path.
     *
     * @param path The path of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    TContent getContent(RepoPath path);

    /**
     * Removes content at the given path if there is content.
     *
     * @param absolutePath The absolute path in the repo of the content to remove.
     */
    default void removeContent(String absolutePath)
    {
        removeContent(RepoPath.at(absolutePath));
    }

    /**
     * Removes content at the given path if there is content.
     *
     * @param path The path in the repo of the content to remove.
     */
    void removeContent(RepoPath path);

    /**
     * Replaces all the content with the given stream of content.
     * This should clear all the existing content in the area and replace it with only the given content.
     * @param commitContent The content to replace all the current content with.
     */
    void replaceAllContent(Stream<AreaEntry<TContent>> commitContent);

    /**
     * Gets the stream of all the content for this area.
     * @return The stream of content for this area.
     */
    Stream<AreaEntry<Content>> getContentStream();

    /**
     * Gets the stream of all the typed content for this area.
     * @return The stream of typed content for this area.
     */
    Stream<AreaEntry<TContent>> getTypedContentStream();

    /**
     * Clears all the content from the content area
     */
    void clear();

    /**
     * Gets the structure of this area as a list of paths.
     * This is useful for debugging.
     * New lines are made with '\n' instead of the system line separator, so that it's easier to unit test with.
     * @return The structure of this area as a list of paths.
     */
    default String asListString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        // Get the stream of content:
        Stream<AreaEntry<Content>> contentStream = getContentStream();

        // Make sure it's sorted by the absolute path:
        contentStream = contentStream.sorted(Comparator.comparing(areaEntry -> areaEntry.path.toAbsolutePath().path));

        // Print out the structure:
        contentStream.forEachOrdered(areaEntry ->
                                     {
                                         // Add a line separator if necessary:
                                         if (stringBuilder.length() > 0) stringBuilder.append("\n");

                                         // Append the path:
                                         stringBuilder.append(areaEntry.path.toAbsolutePath().path);
                                         stringBuilder.append(" : ");
                                         stringBuilder.append(areaEntry.content);
                                     });

        return stringBuilder.toString();
    }
}
