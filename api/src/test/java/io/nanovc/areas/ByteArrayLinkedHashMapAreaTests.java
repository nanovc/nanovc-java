package io.nanovc.areas;

/**
 * Test the {@link ByteArrayLinkedHashMapArea}.
 */
public class ByteArrayLinkedHashMapAreaTests extends ByteArrayMapAreaTestsBase<ByteArrayLinkedHashMapArea>
{
    /**
     * Creates the map area being tested.
     *
     * @return A new instance of the map area under test.
     */
    @Override protected ByteArrayLinkedHashMapArea createMapAreaUnderTest()
    {
        return new ByteArrayLinkedHashMapArea();
    }
}
