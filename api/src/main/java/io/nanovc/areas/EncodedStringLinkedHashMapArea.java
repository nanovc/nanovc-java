package io.nanovc.areas;

import io.nanovc.AreaEntry;
import io.nanovc.RepoPath;
import io.nanovc.content.EncodedStringContent;
import io.nanovc.content.StringContent;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * An area for {@link EncodedStringContent} that is encoded with an explicit character set for each piece of content.
 * This implies that each piece of content could have a potentially different encoding character set.
 * If you want a homogenous string area where all strings are encoded with {@link java.nio.charset.StandardCharsets#UTF_8}
 * then use {@link StringLinkedHashMapArea} instead.
 * It is backed by a {@link StringLinkedHashMapArea} which preserves order for the content that was added.
 * The key is the absolute repo path for the content.
 * The value is the {@link StringContent}.
 */
public class EncodedStringLinkedHashMapArea
    extends LinkedHashMapArea<EncodedStringContent>
    implements EncodedStringAreaAPI
{
    /**
     * The character set to use for encoding the strings as content.
     * Not every piece of content in this area is guaranteed to be of this encoding because each one can be of a different encoding,
     * however, this is the default character set to use for encoding new content.
     */
    public final Charset charset;

    /**
     * Creates a new string hash map area which uses the given charset for encoding the strings to bytes.
     * Not every piece of content in this area is guaranteed to be of this encoding because each one can be of a different encoding,
     * however, this is the default character set to use for encoding new content.
     * @param charset The character set to use for encoding the strings to bytes.
     */
    public EncodedStringLinkedHashMapArea(Charset charset)
    {
        this.charset = charset;
    }

    /**
     * Creates a new string hash map area which uses UTF-8 for encoding the strings to bytes.
     * Not every piece of content in this area is guaranteed to be of this encoding because each one can be of a different encoding,
     * however, this is the default character set to use for encoding new content.
     */
    public EncodedStringLinkedHashMapArea()
    {
        this(StandardCharsets.UTF_8);
    }

    /**
     * Creates a new {@link StringContent} instance for the given value.
     * @param value The value to wrap in string content.
     * @return The wrapped string content.
     */
    protected EncodedStringContent createContentFor(String value)
    {
        return new EncodedStringContent(value, this.charset);
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
        EncodedStringContent content = this.get(path.toAbsolutePath().path);
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
        EncodedStringContent content = this.get(absolutePath);
        return content == null ? null : content.value;
    }

    /**
     * Creates a new string area that is a copy of the given one.
     * @param stringArea The string area to copy the content from.
     * @return A new string area that is a copy of the given string area.
     */
    public static EncodedStringLinkedHashMapArea fromStringArea(StringAreaAPI stringArea)
    {
        EncodedStringLinkedHashMapArea area = new EncodedStringLinkedHashMapArea();
        for (AreaEntry<StringContent> areaEntry : stringArea)
        {
            area.putString(areaEntry.path,  areaEntry.content.value);
        }
        return area;
    }
}
