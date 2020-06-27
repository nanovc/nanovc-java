package io.nanovc.areas;

import io.nanovc.AreaAPI;
import io.nanovc.AreaEntry;
import io.nanovc.ContentAPI;
import io.nanovc.RepoPath;

import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * An area that is backed by a hash map.
 * The key is the absolute repo path for the content.
 * The value is the content.
 */
public class HashMapArea<TContent extends ContentAPI>
    extends HashMap<String, TContent>
    implements AreaAPI<TContent>
{
    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param path    The path in the repo where the content must be put.
     * @param content The content to put.
     */
    @Override
    public void putContent(RepoPath path, TContent content)
    {
        this.put(path.toAbsolutePath().path, content);
    }

    /**
     * Creates and puts the given content into this map.
     * If content at this path already exists, it is replaced.
     *
     * @param absolutePath The absolute path in the repo where the content must be put.
     * @param content      The content to put.
     */
    @Override
    public void putContent(String absolutePath, TContent content)
    {
        this.put(absolutePath, content);
    }

    /**
     * Gets the content at the given path.
     *
     * @param path The path of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    @Override
    public TContent getContent(RepoPath path)
    {
        return this.get(path.toAbsolutePath().path);
    }

    /**
     * Gets the content at the given path.
     *
     * @param absolutePath The absolute path in the repo of the content to get.
     * @return The content at the given path. Null if there is no content at the path.
     */
    @Override
    public TContent getContent(String absolutePath)
    {
        return this.get(absolutePath);
    }

    /**
     * Removes content at the given path if there is content.
     *
     * @param path The path in the repo of the content to remove.
     */
    @Override
    public void removeContent(RepoPath path)
    {
        this.remove(path.toAbsolutePath().path);
    }

    /**
     * Removes content at the given path if there is content.
     *
     * @param absolutePath The absolute path in the repo of the content to remove.
     */
    @Override
    public void removeContent(String absolutePath)
    {
        this.remove(absolutePath);
    }

    /**
     * Replaces all the content with the given stream of content.
     * This should clear all the existing content in the area and replace it with only the given content.
     *
     * @param commitContent The content to replace all the current content with.
     */
    @Override
    public void replaceAllContent(Stream<AreaEntry<TContent>> commitContent)
    {
        this.clear();
        commitContent.forEach(areaEntry -> this.putContent(areaEntry.path, areaEntry.content));
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<AreaEntry<TContent>> iterator()
    {
        return new Iterator<AreaEntry<TContent>>()
        {
            private final Iterator<Entry<String, TContent>> entrySetIterator = entrySet().iterator();

            @Override
            public boolean hasNext()
            {
                return entrySetIterator.hasNext();
            }

            @Override
            public AreaEntry<TContent> next()
            {
                // Get the next entry:
                Entry<String, TContent> nextEntry = entrySetIterator.next();

                return new AreaEntry<>(RepoPath.at(nextEntry.getKey()), nextEntry.getValue());
            }
        };
    }

    /**
     * Returns true if the content area has any content or false if it is empty
     *
     * @return true if any content
     */
    @Override
    public boolean hasAnyContent()
    {
        return this.size() > 0;
    }

    /**
     * Checks whether there is any content at the given path.
     * This method is expected to be cheaper than getting all the content using {@link #getContent(RepoPath)}.
     *
     * @param path The path of the content to get.
     * @return True if there is content at the given path. False if there is no content at the path.
     */
    @Override
    public boolean hasContent(RepoPath path)
    {
        return this.containsKey(path.toAbsolutePath().path);
    }

    /**
     * Checks whether there is any content at the given path.
     * This method is expected to be cheaper than getting all the content using {@link #getContent(String)}.
     *
     * @param absolutePath The absolute path in the repo of the content to check.
     * @return True if there is content at the given path. False if there is no content at the path.
     */
    @Override
    public boolean hasContent(String absolutePath)
    {
        return this.containsKey(absolutePath);
    }

    /**
     * Gets the stream of all the content for this area.
     *
     * @return The stream of content for this area.
     */
    @Override
    public Stream<AreaEntry<ContentAPI>> getContentStream()
    {
        return this.entrySet().stream().map(entry -> new AreaEntry<>(RepoPath.at(entry.getKey()), entry.getValue()));
    }

    /**
     * Gets the stream of all the typed content for this area.
     *
     * @return The stream of typed content for this area.
     */
    @Override
    public Stream<AreaEntry<TContent>> getTypedContentStream()
    {
        return this.entrySet().stream().map(entry -> new AreaEntry<>(RepoPath.at(entry.getKey()), entry.getValue()));
    }
}
