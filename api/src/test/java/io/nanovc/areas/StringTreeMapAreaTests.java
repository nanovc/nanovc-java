package io.nanovc.areas;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * Test the {@link StringTreeMapArea}.
 */
public class StringTreeMapAreaTests extends StringAreaAPITestsBase<StringTreeMapArea>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected StringTreeMapArea createMapAreaUnderTest()
    {
        return new StringTreeMapArea();
    }

    /**
     * Tests that we can create the {@link StringTreeMapArea}.
     */
    @Test
    public void testCreation()
    {
        new StringTreeMapArea();
        new StringTreeMapArea(StandardCharsets.UTF_8);
        new StringTreeMapArea(StandardCharsets.ISO_8859_1);
    }

}
