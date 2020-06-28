package io.nanovc.areas;

/**
 * Test the {@link ByteArrayTreeMapArea}.
 */
public class ByteArrayTreeMapAreaTests extends ByteArrayMapAreaTestsBase<ByteArrayTreeMapArea>
{
    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected ByteArrayTreeMapArea createMapAreaUnderTest()
    {
        return new ByteArrayTreeMapArea();
    }
}
