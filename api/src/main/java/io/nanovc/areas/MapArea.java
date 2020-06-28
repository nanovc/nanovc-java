package io.nanovc.areas;

import io.nanovc.AreaAPI;
import io.nanovc.AreaEntry;
import io.nanovc.ContentAPI;
import io.nanovc.RepoPath;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * An area that is backed by any {@link Map} implementation.
 * The key is the absolute repo path for the content.
 * The value is the content.
 */
public class MapArea<TContent extends ContentAPI>
    implements Map<String, TContent>, AreaAPI<TContent>
{
    /**
     * This is the map being wrapped.
     */
    private final Map<String, TContent> map;

    /**
     * Creates a new {@link MapArea} using the given {@link Map} implementation.
     * @param mapToWrap The map implementation to use for this content area.
     */
    public MapArea(Map<String, TContent> mapToWrap)
    {
        this.map = mapToWrap;
    }

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
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of key-value mappings in this map
     */
    @Override public int size()
    {
        return map.size();
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    @Override public boolean isEmpty()
    {
        return map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.  More formally, returns <tt>true</tt> if and only if
     * this map contains a mapping for a key <tt>k</tt> such that
     * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
     * at most one such mapping.)
     *
     * @param key key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *     key
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified key is null and this map
     *                              does not permit null keys
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.  More formally, returns <tt>true</tt> if and only if
     * this map contains at least one mapping to a value <tt>v</tt> such that
     * <tt>(value==null ? v==null : value.equals(v))</tt>.  This operation
     * will probably require time linear in the map size for most
     * implementations of the <tt>Map</tt> interface.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *     specified value
     * @throws ClassCastException   if the value is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified value is null and this
     *                              map does not permit null values
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>If this map permits null values, then a return value of
     * {@code null} does not <i>necessarily</i> indicate that the map
     * contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to {@code null}.  The {@link #containsKey
     * containsKey} operation may be used to distinguish these two cases.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *     {@code null} if this map contains no mapping for the key
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified key is null and this map
     *                              does not permit null keys
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override public TContent get(Object key)
    {
        return map.get(key);
    }

    /**
     * Associates the specified value with the specified key in this map
     * (optional operation).  If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.  (A map
     * <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only
     * if {@link #containsKey(Object) m.containsKey(k)} would return
     * <tt>true</tt>.)
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *     <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *     (A <tt>null</tt> return can also indicate that the map
     *     previously associated <tt>null</tt> with <tt>key</tt>,
     *     if the implementation supports <tt>null</tt> values.)
     * @throws UnsupportedOperationException if the <tt>put</tt> operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the class of the specified key or value
     *                                       prevents it from being stored in this map
     * @throws NullPointerException          if the specified key or value is null
     *                                       and this map does not permit null keys or values
     * @throws IllegalArgumentException      if some property of the specified key
     *                                       or value prevents it from being stored in this map
     */
    @Override public TContent put(String key, TContent value)
    {
        return map.put(key, value);
    }

    /**
     * Removes the mapping for a key from this map if it is present
     * (optional operation).   More formally, if this map contains a mapping
     * from key <tt>k</tt> to value <tt>v</tt> such that
     * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping
     * is removed.  (The map can contain at most one such mapping.)
     *
     * <p>Returns the value to which this map previously associated the key,
     * or <tt>null</tt> if the map contained no mapping for the key.
     *
     * <p>If this map permits null values, then a return value of
     * <tt>null</tt> does not <i>necessarily</i> indicate that the map
     * contained no mapping for the key; it's also possible that the map
     * explicitly mapped the key to <tt>null</tt>.
     *
     * <p>The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *     <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the key is of an inappropriate type for
     *                                       this map
     *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if the specified key is null and this
     *                                       map does not permit null keys
     *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override public TContent remove(Object key)
    {
        return map.remove(key);
    }

    /**
     * Copies all of the mappings from the specified map to this map
     * (optional operation).  The effect of this call is equivalent to that
     * of calling {@link #put(String, ContentAPI)}  put(k, v)} on this map once
     * for each mapping from key <tt>k</tt> to value <tt>v</tt> in the
     * specified map.  The behavior of this operation is undefined if the
     * specified map is modified while the operation is in progress.
     *
     * @param m mappings to be stored in this map
     * @throws UnsupportedOperationException if the <tt>putAll</tt> operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the class of a key or value in the
     *                                       specified map prevents it from being stored in this map
     * @throws NullPointerException          if the specified map is null, or if
     *                                       this map does not permit null keys or values, and the
     *                                       specified map contains null keys or values
     * @throws IllegalArgumentException      if some property of a key or value in
     *                                       the specified map prevents it from being stored in this map
     */
    @Override public void putAll(Map<? extends String, ? extends TContent> m)
    {
        map.putAll(m);
    }

    /**
     * Removes all of the mappings from this map (optional operation).
     * The map will be empty after this call returns.
     *
     * @throws UnsupportedOperationException if the <tt>clear</tt> operation
     *                                       is not supported by this map
     */
    @Override public void clear()
    {
        map.clear();
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     *
     * @return a set view of the keys contained in this map
     */
    @Override public Set<String> keySet()
    {
        return map.keySet();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this map
     */
    @Override public Collection<TContent> values()
    {
        return map.values();
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
    @Override public Set<Entry<String, TContent>> entrySet()
    {
        return map.entrySet();
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
