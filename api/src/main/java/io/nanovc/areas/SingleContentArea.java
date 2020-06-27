package io.nanovc.areas;

import io.nanovc.AreaBase;
import io.nanovc.AreaEntry;
import io.nanovc.ContentAPI;
import io.nanovc.RepoPath;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * An area where a single piece of content resides.
 * The path of the content is the last path that was put in the area.
 * Every time you put the content it overwrites the path of the single piece of content.
 * This is useful for repo's with one concept that needs to be version controlled.
 */
public class SingleContentArea<TContent extends ContentAPI>
    extends AreaBase<TContent>
{
    public SingleContentArea()
    {
    }

    /**
     * The single piece of content for this area.
     */
    private TContent content;

    /**
     * The path of the single piece of content in this area.
     * This is expected to be the absolute path of the content.
     */
    private RepoPath absolutePath;

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
        this.content = content;
        this.absolutePath = path.toAbsolutePath();
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
        this.content = content;
        this.absolutePath = RepoPath.at(absolutePath);
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
        return Objects.equals(this.absolutePath, path) ? this.content : null;
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
        return this.absolutePath == null ? null : Objects.equals(this.absolutePath.path, absolutePath) ? this.content : null;
    }

    /**
     * Gets the only piece of content stored in this area.
     * @return The only piece of content stored in this area.
     */
    public TContent getContent()
    {
        return this.content;
    }

    /**
     * Removes content at the given path if there is content.
     *
     * @param path The path in the repo of the content to remove.
     */
    @Override
    public void removeContent(RepoPath path)
    {
        if (Objects.equals(this.absolutePath, path))
        {
            this.content = null;
            this.absolutePath = null;
        }
    }

    /**
     * Removes content at the given path if there is content.
     *
     * @param absolutePath The absolute path in the repo of the content to remove.
     */
    @Override
    public void removeContent(String absolutePath)
    {
        if (this.absolutePath != null && Objects.equals(this.absolutePath.path, absolutePath))
        {
            this.content = null;
            this.absolutePath = null;
        }
    }

    @Override
    public boolean hasAnyContent()
    {
        return this.content != null;
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
        return Objects.equals(this.absolutePath, path);
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
        return this.absolutePath != null && Objects.equals(this.absolutePath.path, absolutePath);
    }

    /**
     * Clears all the content from the content area
     */
    @Override
    public void clear()
    {
        this.content = null;
        this.absolutePath = null;
    }

    /**
     * Gets the stream of all the content for this area.
     *
     * @return The stream of content for this area.
     */
    @Override
    public Stream<AreaEntry<ContentAPI>> getContentStream()
    {
        if (this.content != null && this.absolutePath != null) return Stream.of(new AreaEntry<>(this.absolutePath, this.content));
        else return Stream.empty();
    }

    /**
     * Gets the stream of all the typed content for this area.
     *
     * @return The stream of typed content for this area.
     */
    @Override
    public Stream<AreaEntry<TContent>> getTypedContentStream()
    {
        if (this.content != null && this.absolutePath != null) return Stream.of(new AreaEntry<>(this.absolutePath, this.content));
        else return Stream.empty();
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

            private boolean done = false;

            @Override
            public boolean hasNext()
            {
                return !done && absolutePath != null && content != null;
            }

            @Override
            public AreaEntry<TContent> next()
            {
                done = true;
                return new AreaEntry<>(absolutePath, content);
            }
        };
    }
}
