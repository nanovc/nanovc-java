package io.nanovc.areas;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link EncodedStringLinkedHashMapArea}.
 */
public class EncodedStringLinkedHashMapAreaTests extends EncodedStringAreaAPITestsBase<EncodedStringLinkedHashMapArea>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected EncodedStringLinkedHashMapArea createMapAreaUnderTest()
    {
        return new EncodedStringLinkedHashMapArea();
    }

    /**
     * Tests that we can create the {@link EncodedStringLinkedHashMapArea}.
     */
    @Test
    public void testCreation()
    {
        new EncodedStringLinkedHashMapArea();
        EncodedStringLinkedHashMapArea.fromStringArea(new StringHashMapArea());
    }

}
