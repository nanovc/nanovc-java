package io.nanovc.areas;

import io.nanovc.content.StringContent;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link HashMapArea}.
 */
public class HashMapAreaTests extends StringMapAreaTestsBase<HashMapArea<StringContent>>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected HashMapArea<StringContent> createMapAreaUnderTest()
    {
        return new HashMapArea<>();
    }

    /**
     * Tests that we can create the {@link HashMapArea}.
     */
    @Test
    public void testCreation()
    {
        new HashMapArea<StringContent>();
    }

}
