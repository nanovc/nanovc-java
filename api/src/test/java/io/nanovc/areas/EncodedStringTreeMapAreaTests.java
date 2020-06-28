package io.nanovc.areas;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link EncodedStringTreeMapArea}.
 */
public class EncodedStringTreeMapAreaTests extends EncodedStringAreaAPITestsBase<EncodedStringTreeMapArea>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected EncodedStringTreeMapArea createMapAreaUnderTest()
    {
        return new EncodedStringTreeMapArea();
    }

    /**
     * Tests that we can create the {@link EncodedStringTreeMapArea}.
     */
    @Test
    public void testCreation()
    {
        new EncodedStringTreeMapArea();
        EncodedStringTreeMapArea.fromStringArea(new StringHashMapArea());
    }

}
