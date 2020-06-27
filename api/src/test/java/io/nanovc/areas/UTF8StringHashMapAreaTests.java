package io.nanovc.areas;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link UTF8StringHashMapArea}.
 */
public class UTF8StringHashMapAreaTests extends StringAreaAPITestsBase<UTF8StringHashMapArea>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected UTF8StringHashMapArea createMapAreaUnderTest()
    {
        return new UTF8StringHashMapArea();
    }

    /**
     * Tests that we can create the {@link UTF8StringHashMapArea}.
     */
    @Test
    public void testCreation()
    {
        new UTF8StringHashMapArea();
        UTF8StringHashMapArea.fromStringArea(new StringHashMapArea());
    }

}
