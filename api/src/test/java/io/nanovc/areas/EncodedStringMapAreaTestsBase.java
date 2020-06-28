package io.nanovc.areas;

import io.nanovc.AreaAPI;
import io.nanovc.content.EncodedStringContent;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Base tests for various implementations of Map Content Areas that store {@link EncodedStringContent}.
 * These are common tests that we expect all map implementations to pass.
 */
public abstract class EncodedStringMapAreaTestsBase<TMapArea extends AreaAPI<EncodedStringContent> & Map<String, EncodedStringContent>>
    extends MapAreaTestsBase<EncodedStringContent, TMapArea>
{
    /**
     * Creates the specific type of content for the given value.
     *
     * @param content Creates the specific type of content that we want to put into the content area.
     * @return The specific type of content for the given value.
     */
    @Override protected EncodedStringContent createContent(String content)
    {
        return new EncodedStringContent(content, StandardCharsets.UTF_8);
    }

    /**
     * Reads the value of the content.
     *
     * @param stringContent The content to read out of.
     * @return The string value of the content.
     */
    @Override protected String readContent(EncodedStringContent stringContent)
    {
        return stringContent.value;
    }

}
