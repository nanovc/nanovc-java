package io.nanovc.indexes;

import io.nanovc.NanoVersionControlTestsBase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The base class for core tests for each implementation of the {@link ByteArrayIndex}.
 * Sub class this test for each concrete implementation.
 *
 * @param <TIndex> The specific type of the {@link ByteArrayIndex} under test.
 */
public abstract class ByteArrayIndexTestBase<TIndex extends ByteArrayIndex> extends NanoVersionControlTestsBase
{
    /**
     * Creates the specific type of {@link ByteArrayIndex} under test.
     * @return A new instance of the {@link ByteArrayIndex} under test.
     */
    protected abstract TIndex createNewByteArrayIndex();

    @Test
    public void testIndexingEmptyByteArrays()
    {
        assertIndexingByteArrays(true, new byte[0], new byte[0]);
    }

    @Test
    public void testIndexingByteArrays_1_Item_Same()
    {
        assertIndexingByteArrays(true, new byte[]{123}, new byte[]{123});
    }

    @Test
    public void testIndexingByteArrays_1_Item_Different()
    {
        assertIndexingByteArrays(false, new byte[]{123}, new byte[]{111});
    }

    @Test
    public void testIndexingByteArrays_Many_Items_Same()
    {
        // Create the index:
        TIndex index = createNewByteArrayIndex();

        // Run the tests:
        assertIndexHasByteArray(index, true, new byte[0]);
        assertIndexHasByteArray(index, false, new byte[0]);

        assertIndexHasByteArray(index, true, new byte[]{123});
        assertIndexHasByteArray(index, false, new byte[]{123});
        assertIndexHasByteArray(index, true, new byte[]{124});
        assertIndexHasByteArray(index, false, new byte[]{123});
        assertIndexHasByteArray(index, false, new byte[]{124});

        assertIndexHasByteArray(index, true, new byte[]{7});
        assertIndexHasByteArray(index, false, new byte[]{7});

        assertIndexHasByteArray(index, true, new byte[]{1, 2, 4});
        assertIndexHasByteArray(index, false, new byte[]{1, 2, 4});
        assertIndexHasByteArray(index, false, new byte[]{7}); // 7 has the same hashes as 1, 2, 4

        assertIndexHasByteArray(index, true, new byte[]{1, 2, 4, 8, 16, 32});
        assertIndexHasByteArray(index, false, new byte[]{1, 2, 4, 8, 16, 32});

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

    /**
     * Tests the performance of indexing many byte arrays.
     */
    @Test
    @Disabled
    public void testIndexingPerformance()
    {
        final int COUNT = 100_000; // Number of random arrays to create.
        final int SIZE = 1_000; // Byte Array upper size limit.

        assertIndexingPerformance(COUNT, SIZE);
    }

    /**
     * Tests the performance of indexing small byte arrays.
     */
    @Test
    public void testIndexingPerformanceSmall()
    {
        final int COUNT = 1_000; // Number of random arrays to create.
        final int SIZE = 100; // Byte Array upper size limit.

        assertIndexingPerformance(COUNT, SIZE);
    }

    /**
     * Measures the performance for indexing the given arrays.
     * @param COUNT Number of random arrays to create.
     * @param SIZE Byte Array upper size limit.
     */
    public void assertIndexingPerformance(final int COUNT, final int SIZE)
    {
        // Create the random number generator for the bytes:
        Random random = new Random(1234L);

        // Keep a list of the bytes to index so that we only time the index:
        List<byte[]> data = new ArrayList<>(COUNT);

        long startNanos, endNanos;


        // Start tracking the memory usage:
        // https://stackoverflow.com/questions/74674/how-to-do-i-check-cpu-and-memory-usage-in-java
        // https://stackoverflow.com/a/74763/231860
        Runtime runtime = Runtime.getRuntime();
        long startingAllocatedMemory = runtime.totalMemory();
        System.out.printf("Starting allocated memory: %,d Bytes = %,d MB%n", startingAllocatedMemory, startingAllocatedMemory / 1024 / 1024);

        // Generate the random data:
        startNanos = System.nanoTime();
        for (int i = 0; i < COUNT; i++)
        {
            // Get the next length of bytes that we want to produce:
            int size = random.nextInt(SIZE);

            // Create the array:
            byte[] bytes = new byte[size];

            // Fill the array with random bytes:
            random.nextBytes(bytes);

            // Save the data:
            data.add(bytes);
        }
        endNanos = System.nanoTime();
        System.out.printf("Data generation: %,d arrays in %,dms => %,d arrays/s%n", COUNT, (endNanos - startNanos) / 1_000_000, COUNT * 1_000_000_000L / (endNanos - startNanos));

        // Get the allocated memory after generating the data:
        long generatedAllocatedMemory = runtime.totalMemory();
        System.out.printf("Generated allocated memory: %,d Bytes = %,d MB%n", generatedAllocatedMemory, generatedAllocatedMemory / 1024 / 1024);
        System.out.printf("Generated allocated difference: %,d Bytes = %,d MB%n", generatedAllocatedMemory - startingAllocatedMemory, (generatedAllocatedMemory-startingAllocatedMemory) / 1024 / 1024);


        // Create the index:
        ByteArrayIndex index = createNewByteArrayIndex();

        // Keep track of stats around the index:
        int matches = 0, zeroArrayCount = 0;

        // Index the data:
        startNanos = System.nanoTime();
        for (int i = 0; i < COUNT; i++)
        {
            // Get the data:
            byte[] bytes = data.get(i);

            // Index the bytes:
            byte[] bytesFromIndex = index.addOrLookup(bytes);

            // Check whether we have a duplicate value:
            if (bytes != bytesFromIndex)
            {
                // The index returned a different instance, meaning that we already had a byte array with the same values.
                matches++;
            }

            // Keep track of how many zero arrays we had:
            if (bytes.length == 0) zeroArrayCount++;
        }
        endNanos = System.nanoTime();
        System.out.printf("%nData indexing: %,d arrays in %,dms => %,d arrays/s%n%n", COUNT, (endNanos - startNanos) / 1_000_000, COUNT * 1_000_000_000L / (endNanos - startNanos));

        // Get the allocated memory after indexing the data:
        long endingAllocatedMemory = runtime.totalMemory();
        long indexingMemory = endingAllocatedMemory - generatedAllocatedMemory;
        System.out.printf("Ending allocated memory: %,d Bytes = %,d MB%n", endingAllocatedMemory, endingAllocatedMemory / 1024 / 1024);
        System.out.printf("Indexing size: %,d Bytes = %,d MB%n", indexingMemory, indexingMemory / 1024 / 1024);
        System.out.printf("%nIndex Ratio: %,d Index Bytes per Raw Byte = %,d MB%n%n", indexingMemory / SIZE, indexingMemory / SIZE / 1024 / 1024);

        System.out.printf("Array matches: %,d with %,d arrays that have zero length. ", matches, zeroArrayCount);
    }

    /**
     * Checks that indexing the given byte arrays behaves as expected.
     *
     * @param isMatchExpected Flags whether a match is expected in this case. True if the match is expected. False if it is not expected to match in the index.
     * @param bytesToIndex    The first byte array to check. This is supposed to have the same value as the bytesToCheck but it is not expected to be the same instance.
     * @param bytesToCheck    The second byte array to check. This is supposed to have the same value as the bytesToIndex but it is not expected to be the same instance.
     * @return The index that was created for this test case so that it could be chained.
     */
    protected ByteArrayIndex assertIndexingByteArrays(boolean isMatchExpected, byte[] bytesToIndex, byte[] bytesToCheck)
    {
        // Create the empty byte arrays to test with:
        assertNotSame(bytesToIndex, bytesToCheck);

        // Create the index:
        ByteArrayIndex index = createNewByteArrayIndex();

        // Add the bytes to the index:
        assertSame(bytesToIndex, index.addOrLookup(bytesToIndex), "We expect to get the same array instance back because we haven't seen this array before.");

        // Add another instance and check if the index finds a match:
        if (isMatchExpected)
        {
            // A match is expected from the index.
            assertSame(bytesToIndex, index.addOrLookup(bytesToCheck), "We did not get the expected lookup value. We expect to get the original array back because the value should be the same as the indexed value.");
        }
        else
        {
            // A match is not expected from the index.
            byte[] bytesFromIndex = index.addOrLookup(bytesToCheck);
            assertNotSame(bytesToIndex, bytesFromIndex, "We got the same instance from the index when we didn't expect to.");

            // Make sure that the values are the same:
            assertArrayEquals(bytesToCheck, bytesFromIndex, "The values for the arrays should have been the same.");
        }

        return index;
    }

    /**
     * Checks that the index has the given byte array.
     *
     * @param index           The index to use for the test.
     * @param isMatchExpected Flags whether a match is expected in this case. True if the match is expected. False if it is not expected to match in the index.
     * @param bytesToCheck    The byte array to check. This is supposed to have the same value as the bytesToIndex but it is not expected to be the same instance.
     */
    protected void assertIndexHasByteArray(TIndex index, boolean isMatchExpected, byte[] bytesToCheck)
    {
        // Get the value from the index:
        byte[] bytesInIndex = index.addOrLookup(bytesToCheck);

        // Add another instance and check if the index finds a match:
        if (isMatchExpected)
        {
            // A match is expected from the index.
            assertSame(bytesInIndex, bytesToCheck, "We did not get the expected lookup value. We expect to get the original array back because the value should be the same as the indexed value.");
        }
        else
        {
            // A match is not expected from the index.
            assertNotSame(bytesInIndex, bytesToCheck, "We got the same instance from the index when we didn't expect to.");

            // Make sure that the values are the same:
            assertArrayEquals(bytesInIndex, bytesToCheck, "The values for the arrays should have been the same.");
        }
    }

}
