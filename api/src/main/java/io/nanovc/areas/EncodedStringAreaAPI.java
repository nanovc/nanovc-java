package io.nanovc.areas;

import io.nanovc.AreaAPI;
import io.nanovc.RepoPath;
import io.nanovc.content.EncodedStringContent;

/**
 * An area for {@link EncodedStringContent} that is encoded explicitly for each piece of content.
 * This implies that each piece of content could have a potentially different encoding character set.
 * If you want a homogenous string area where all strings are encoded with {@link java.nio.charset.StandardCharsets#UTF_8}
 * then use {@link StringAreaAPI} instead.
 */
public interface EncodedStringAreaAPI extends AreaAPI<EncodedStringContent>
{
    /**
     * Checks whether there is a string at the given path.
     *
     * @param path The path of the content to check.
     * @return True if there is a string at the given path. False if there is no string at the path.
     */
    default boolean hasString(RepoPath path)
    {
        return this.hasContent(path);
    }

    /**
     * Checks whether there is a string at the given path.
     *
     * @param absolutePath The absolute path in the repo of the content to check.
     * @return True if there is a string at the given path. False if there is no string at the path.
     */
    default boolean hasString(String absolutePath)
    {
        return hasContent(absolutePath);
    }

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param path    The path in the repo where the content must be put.
     * @param content The content to put.
     */
    void putString(RepoPath path, String content);

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param absolutePath The absolute path in the repo where the content must be put.
     * @param content      The content to put.
     */
    void putString(String absolutePath, String content);

    /**
     * Gets the content at the given path.
     *
     * @param path The path of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    String getString(RepoPath path);

    /**
     * Gets the string at the given path.
     *
     * @param absolutePath The absolute path in the repo of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    String getString(String absolutePath);

    /**
     * Removes the string at the given path.
     * If there is no content at that path then nothing happens.
     * @param path The path of the content to remove.
     */
    default void removeString(RepoPath path)
    {
        removeContent(path);
    }

    /**
     * Removes the string at the given path.
     * If there is no content at that path then nothing happens.
     * @param absolutePath The absolute path in the repo of the content to remove.
     */
    default void removeString(String absolutePath)
    {
        removeContent(absolutePath);
    }
}
