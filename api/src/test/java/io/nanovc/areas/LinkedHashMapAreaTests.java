package io.nanovc.areas;

import io.nanovc.content.StringContent;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link LinkedHashMapArea}.
 */
public class LinkedHashMapAreaTests extends StringMapAreaTestsBase<LinkedHashMapArea<StringContent>>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected LinkedHashMapArea<StringContent> createMapAreaUnderTest()
    {
        return new LinkedHashMapArea<>();
    }

    /**
     * Tests that we can create the {@link LinkedHashMapArea}.
     */
    @Test
    public void testCreation()
    {
        new LinkedHashMapArea<StringContent>();
    }

}
