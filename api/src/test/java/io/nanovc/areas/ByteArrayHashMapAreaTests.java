package io.nanovc.areas;

/**
 * Test the {@link ByteArrayHashMapArea}.
 */
public class ByteArrayHashMapAreaTests extends ByteArrayMapAreaTestsBase<ByteArrayHashMapArea>
{
    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected ByteArrayHashMapArea createMapAreaUnderTest()
    {
        return new ByteArrayHashMapArea();
    }
}
