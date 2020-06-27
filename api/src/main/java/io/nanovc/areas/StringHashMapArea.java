package io.nanovc.areas;

import io.nanovc.RepoPath;
import io.nanovc.content.StringContent;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * An area for storing strings.
 * It is backed by a hash map.
 * The key is the absolute repo path for the content.
 * The value is the {@link StringContent}.
 */
public class StringHashMapArea
    extends HashMapArea<StringContent>
    implements StringAreaAPI
{
    /**
     * The character set to use for encoding the strings as content.
     */
    protected final Charset charset;

    /**
     * Creates a new string hash map area which uses the given charset for encoding the strings to bytes.
     * @param charset The character set to use for encoding the strings to bytes.
     */
    public StringHashMapArea(Charset charset)
    {
        this.charset = charset;
    }

    /**
     * Creates a new string hash map area which uses UTF-8 for encoding the strings to bytes.
     */
    public StringHashMapArea()
    {
        this(StandardCharsets.UTF_8);
    }

    /**
     * Creates a new {@link StringContent} instance for the given value.
     * @param value The value to wrap in string content.
     * @return The wrapped string content.
     */
    protected StringContent createContentFor(String value)
    {
        return new StringContent(value, this.charset);
    }

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param path    The path in the repo where the content must be put.
     * @param content The content to put.
     */
    public void putString(RepoPath path, String content)
    {
        this.put(path.toAbsolutePath().path, createContentFor(content));
    }

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param absolutePath The absolute path in the repo where the content must be put.
     * @param content      The content to put.
     */
    public void putString(String absolutePath, String content)
    {
        this.put(absolutePath, createContentFor(content));
    }

    /**
     * Gets the content at the given path.
     *
     * @param path The path of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    public String getString(RepoPath path)
    {
        StringContent content = this.get(path.toAbsolutePath().path);
        return content == null ? null : content.value;
    }

    /**
     * Gets the string at the given path.
     *
     * @param absolutePath The absolute path in the repo of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    public String getString(String absolutePath)
    {
        StringContent content = this.get(absolutePath);
        return content == null ? null : content.value;
    }
}
