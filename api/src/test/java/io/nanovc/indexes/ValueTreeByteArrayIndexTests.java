package io.nanovc.indexes;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ValueTreeByteArrayIndex}.
 */
public class ValueTreeByteArrayIndexTests extends ByteArrayIndexTestBase<ValueTreeByteArrayIndex>
{

    /**
     * Creates the specific type of {@link ByteArrayIndex} under test.
     *
     * @return A new instance of the {@link ByteArrayIndex} under test.
     */
    @Override
    protected ValueTreeByteArrayIndex createNewByteArrayIndex()
    {
        return new ValueTreeByteArrayIndex();
    }

    @Test
    public void testIndexingByteArrays_Coverage()
    {
        // Create the index:
        ValueTreeByteArrayIndex index = createNewByteArrayIndex();

        // Now we need to create cases that give us coverage for the hash maps in the value nodes:
        // Step 2 Structures:
        assertIndexHasByteArray(index, true, new byte[]{3, 4}); // same hash as 7.
        assertIndexHasByteArray(index, true, new byte[]{2, 5}); // same hash as 7, different value in the value node, thus creating a hash map.
        assertIndexHasByteArray(index, false, new byte[]{2, 5}); // reading from the created hash map.

        // Step 3 Structures:
        assertIndexHasByteArray(index, true, new byte[]{1, 6, 0}); // same hash as 7.
        assertIndexHasByteArray(index, true, new byte[]{2, 4, 1}); // same hash as 7, different value in the value node, thus creating a hash map.
        assertIndexHasByteArray(index, false, new byte[]{2, 4, 1}); // reading from the created hash map.

        // Step 4 Structures:
        assertIndexHasByteArray(index, true, new byte[]{1, 6, 0, 0}); // same hash as 7.
        assertIndexHasByteArray(index, true, new byte[]{2, 4, 1, 0}); // same hash as 7, different value in the value node, thus creating a hash map.
        assertIndexHasByteArray(index, false, new byte[]{2, 4, 1, 0}); // reading from the created hash map.

        // Step 1 Structures: UNCOVERED, because we can't create data to get a hash map in the second step.
        assertIndexHasByteArray(index, true, new byte[]{1, 6, 0, 0, 0}); // same hash as 7.
        assertIndexHasByteArray(index, true, new byte[]{1, 0, 6, 0, 0}); // same hash as 7, different first step value, therefore we can't get into the map branch. Stays uncovered.
        assertIndexHasByteArray(index, false, new byte[]{1, 0, 6, 0, 0}); // reading from the second step node.
    }
}
