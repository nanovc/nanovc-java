package io.nanovc.areas;

import io.nanovc.AreaAPI;
import io.nanovc.AreaEntry;
import io.nanovc.ContentAPI;
import io.nanovc.RepoPath;
import io.nanovc.content.ByteArrayContent;

/**
 * An area for storing byte arrays.
 * It is backed by a {@link HashMapArea} which is efficient to access but does not preserve its order.
 * The key is the absolute repo path for the content.
 * The value is the {@link ByteArrayContent}.
 */
public class ByteArrayHashMapArea
    extends HashMapArea<ByteArrayContent>
    implements ByteArrayAreaAPI
{
    /**
     * Creates a new {@link ByteArrayContent} instance for the given value.
     * @param bytes The bytes to wrap as content.
     * @return The wrapped byte array content.
     */
    protected ByteArrayContent createContentFor(byte[] bytes)
    {
        return new ByteArrayContent(bytes);
    }

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param path    The path in the repo where the content must be put.
     * @param bytes   The bytes to put at the path.
     */
    public void putBytes(RepoPath path, byte[] bytes)
    {
        this.put(path.toAbsolutePath().path, createContentFor(bytes));
    }

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param absolutePath The absolute path in the repo where the content must be put.
     * @param bytes        The bytes to put at the path.
     */
    public void putBytes(String absolutePath, byte[] bytes)
    {
        this.put(absolutePath, createContentFor(bytes));
    }

    /**
     * Gets the content at the given path.
     *
     * @param path The path of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    public byte[] getBytes(RepoPath path)
    {
        ByteArrayContent content = this.get(path.toAbsolutePath().path);
        return content == null ? null : content.bytes;
    }

    /**
     * Gets the content at the given path.
     *
     * @param absolutePath The absolute path in the repo of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    public byte[] getBytes(String absolutePath)
    {
        ByteArrayContent content = this.get(absolutePath);
        return content == null ? null : content.bytes;
    }

    /**
     * This either casts the given area to a {@link ByteArrayHashMapArea} if it creates a new one with the same content as the given one.
     * When it clones the area, it makes references to the original content objects.
     * @param area The area to either cast or wrap as a ByteArrayHashMapArea.
     * @return The given area either casted to a {@link ByteArrayHashMapArea} if it already is one or a new copy with the same content.
     */
    public static ByteArrayHashMapArea castOrClone(AreaAPI<? extends ContentAPI> area)
    {
        if (area instanceof ByteArrayHashMapArea) return (ByteArrayHashMapArea) area;
        else
        {
            // This is not the desired type.

            // Create a copy of the area:
            ByteArrayHashMapArea clone = new ByteArrayHashMapArea();
            for (AreaEntry<? extends ContentAPI> entry : area)
            {
                clone.putContent(entry.path, new ByteArrayContent(entry.content.asByteArray()));
            }
            return clone;
        }
    }
}
