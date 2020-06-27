package io.nanovc.areas;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * Test the {@link StringLinkedHashMapArea}.
 */
public class StringLinkedHashMapAreaTests extends StringAreaAPITestsBase<StringLinkedHashMapArea>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected StringLinkedHashMapArea createMapAreaUnderTest()
    {
        return new StringLinkedHashMapArea();
    }

    /**
     * Tests that we can create the {@link StringLinkedHashMapArea}.
     */
    @Test
    public void testCreation()
    {
        new StringLinkedHashMapArea();
        new StringLinkedHashMapArea(StandardCharsets.UTF_8);
        new StringLinkedHashMapArea(StandardCharsets.ISO_8859_1);
    }

}
