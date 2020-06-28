package io.nanovc.areas;

import io.nanovc.content.StringContent;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link TreeMapArea}.
 */
public class TreeMapAreaTests extends StringMapAreaTestsBase<TreeMapArea<StringContent>>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected TreeMapArea<StringContent> createMapAreaUnderTest()
    {
        return new TreeMapArea<>();
    }

    /**
     * Tests that we can create the {@link TreeMapArea}.
     */
    @Test
    public void testCreation()
    {
        new TreeMapArea<StringContent>();
    }

}
