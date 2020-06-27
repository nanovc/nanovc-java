package io.nanovc.areas;

import io.nanovc.AreaAPI;
import io.nanovc.content.ByteArrayContent;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.nanovc.ByteHelper.bytes;

/**
 * Base tests for various implementations of Map Content Areas that store Byte Arrays.
 * These are common tests that we expect all map implementations to pass.
 */
public abstract class ByteArrayMapAreaTestsBase<TMapArea extends AreaAPI<ByteArrayContent> & Map<String, ByteArrayContent>>
    extends MapAreaTestsBase<ByteArrayContent, TMapArea>
{
    /**
     * Creates the specific type of content for the given value.
     *
     * @param content Creates the specific type of content that we want to put into the content area.
     * @return The specific type of content for the given value.
     */
    @Override protected ByteArrayContent createContent(String content)
    {
        return new ByteArrayContent(bytes(content));
    }

    /**
     * Reads the value of the content.
     *
     * @param byteArrayContent The content to read out of.
     * @return The string value of the content.
     */
    @Override protected String readContent(ByteArrayContent byteArrayContent)
    {
        return new String(byteArrayContent.bytes, StandardCharsets.UTF_8);
    }
}
