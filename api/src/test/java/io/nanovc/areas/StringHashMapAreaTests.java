package io.nanovc.areas;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * Test the {@link StringHashMapArea}.
 */
public class StringHashMapAreaTests extends StringAreaAPITestsBase<StringHashMapArea>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected StringHashMapArea createMapAreaUnderTest()
    {
        return new StringHashMapArea();
    }

    /**
     * Tests that we can create the {@link StringHashMapArea}.
     */
    @Test
    public void testCreation()
    {
        new StringHashMapArea();
        new StringHashMapArea(StandardCharsets.UTF_8);
        new StringHashMapArea(StandardCharsets.ISO_8859_1);
    }

}
