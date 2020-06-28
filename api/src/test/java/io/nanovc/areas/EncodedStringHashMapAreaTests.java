package io.nanovc.areas;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link EncodedStringHashMapArea}.
 */
public class EncodedStringHashMapAreaTests extends EncodedStringAreaAPITestsBase<EncodedStringHashMapArea>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected EncodedStringHashMapArea createMapAreaUnderTest()
    {
        return new EncodedStringHashMapArea();
    }

    /**
     * Tests that we can create the {@link EncodedStringHashMapArea}.
     */
    @Test
    public void testCreation()
    {
        new EncodedStringHashMapArea();
        EncodedStringHashMapArea.fromStringArea(new StringHashMapArea());
    }

}
