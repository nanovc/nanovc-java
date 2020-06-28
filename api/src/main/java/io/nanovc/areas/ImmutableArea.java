package io.nanovc.areas;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;

/**
 * An immutable area.
 * This means that no {@link ContentAPI} can be added or removed from the area once it has been created.
 * @param <TContent> The specific type of content that is stored in this area.
 */
public interface ImmutableArea<TContent extends ContentAPI>
    extends AreaAPI<TContent>
{
}
