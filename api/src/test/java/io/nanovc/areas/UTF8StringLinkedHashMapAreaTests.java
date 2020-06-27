package io.nanovc.areas;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link UTF8StringLinkedHashMapArea}.
 */
public class UTF8StringLinkedHashMapAreaTests extends StringAreaAPITestsBase<UTF8StringLinkedHashMapArea>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected UTF8StringLinkedHashMapArea createMapAreaUnderTest()
    {
        return new UTF8StringLinkedHashMapArea();
    }

    /**
     * Tests that we can create the {@link UTF8StringLinkedHashMapArea}.
     */
    @Test
    public void testCreation()
    {
        new UTF8StringLinkedHashMapArea();
        UTF8StringLinkedHashMapArea.fromStringArea(new StringHashMapArea());
    }

}
