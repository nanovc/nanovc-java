package io.nanovc.areas;

import io.nanovc.AreaEntry;
import io.nanovc.content.StringContent;

import java.nio.charset.StandardCharsets;

/**
 * An area for storing strings.
 * It is backed by a {@link StringLinkedHashMapArea} which preserves order for the content that was added.
 * The key is the absolute repo path for the content.
 * The value is the {@link StringContent}.
 */
public class UTF8StringLinkedHashMapArea
    extends StringHashMapArea
{
    /**
     * Creates a new string hash map area which uses the {@link StandardCharsets#UTF_8} for encoding the strings to bytes.
     */
    public UTF8StringLinkedHashMapArea()
    {
        super(StandardCharsets.UTF_8);
    }

    /**
     * Creates a new string area that is a copy of the given one.
     * @param stringArea The string area to copy the content from.
     * @return A new string area that is a copy of the given string area.
     */
    public static UTF8StringLinkedHashMapArea fromStringArea(StringAreaAPI stringArea)
    {
        UTF8StringLinkedHashMapArea area = new UTF8StringLinkedHashMapArea();
        for (AreaEntry<StringContent> areaEntry : stringArea)
        {
            area.putString(areaEntry.path,  areaEntry.content.value);
        }
        return area;
    }
}
