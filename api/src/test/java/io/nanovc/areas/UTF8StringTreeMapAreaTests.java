package io.nanovc.areas;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link UTF8StringTreeMapArea}.
 */
public class UTF8StringTreeMapAreaTests extends StringAreaAPITestsBase<UTF8StringTreeMapArea>
{

    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected UTF8StringTreeMapArea createMapAreaUnderTest()
    {
        return new UTF8StringTreeMapArea();
    }

    /**
     * Tests that we can create the {@link UTF8StringTreeMapArea}.
     */
    @Test
    public void testCreation()
    {
        new UTF8StringTreeMapArea();
        UTF8StringTreeMapArea.fromStringArea(new StringHashMapArea());
    }

}
