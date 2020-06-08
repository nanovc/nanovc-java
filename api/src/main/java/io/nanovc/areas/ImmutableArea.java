package io.nanovc.areas;

import io.nanovc.Area;
import io.nanovc.Content;

/**
 * An immutable area.
 * This means that no {@link Content} can be added or removed from the area once it has been created.
 * @param <TContent> The specific type of content that is stored in this area.
 */
public interface ImmutableArea<TContent extends Content> extends Area<TContent>
{
}
