package io.nanovc.areas;

import io.nanovc.Area;
import io.nanovc.RepoPath;
import io.nanovc.content.ByteArrayContent;

/**
 * An area for {@link ByteArrayContent}.
 */
public interface ByteArrayArea extends Area<ByteArrayContent>
{
    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param path    The path in the repo where the content must be put.
     * @param bytes   The bytes to put at the path.
     */
    void putBytes(RepoPath path, byte[] bytes);

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param absolutePath The absolute path in the repo where the content must be put.
     * @param bytes        The bytes to put at the path.
     */
    void putBytes(String absolutePath, byte[] bytes);

    /**
     * Gets the content at the given path.
     *
     * @param path The path of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    byte[] getBytes(RepoPath path);

    /**
     * Gets the content at the given path.
     *
     * @param absolutePath The absolute path in the repo of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    byte[] getBytes(String absolutePath);
}
