package io.nanovc.areas;

import io.nanovc.AreaAPI;
import io.nanovc.content.StringContent;

import java.util.Map;

/**
 * Base tests for various implementations of Map Content Areas that store {@link StringContent}.
 * These are common tests that we expect all map implementations to pass.
 */
public abstract class StringMapAreaTestsBase<TMapArea extends AreaAPI<StringContent> & Map<String, StringContent>>
    extends MapAreaTestsBase<StringContent, TMapArea>
{
    /**
     * Creates the specific type of content for the given value.
     *
     * @param content Creates the specific type of content that we want to put into the content area.
     * @return The specific type of content for the given value.
     */
    @Override protected StringContent createContent(String content)
    {
        return new StringContent(content);
    }

    /**
     * Reads the value of the content.
     *
     * @param stringContent The content to read out of.
     * @return The string value of the content.
     */
    @Override protected String readContent(StringContent stringContent)
    {
        return stringContent.value;
    }

}
