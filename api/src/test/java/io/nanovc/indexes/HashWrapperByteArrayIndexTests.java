package io.nanovc.indexes;

import io.nanovc.ByteArrayIndex;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link HashWrapperByteArrayIndex}.
 */
public class HashWrapperByteArrayIndexTests extends ByteArrayIndexTestBase<HashWrapperByteArrayIndex>
{
    /**
     * Creates the specific type of {@link ByteArrayIndex} under test.
     *
     * @return A new instance of the {@link ByteArrayIndex} under test.
     */
    @Override
    protected HashWrapperByteArrayIndex createNewByteArrayIndex()
    {
        return new HashWrapperByteArrayIndex();
    }

    /**
     * Tests the performance of indexing 1 Million byte arrays.
     */
    @Test
    @Disabled
    public void testIndexingPerformance_1M()
    {
        final int COUNT = 1_000_000; // Number of random arrays to create.
        final int SIZE = 1_000; // Byte Array upper size limit.

        assertIndexingPerformance(COUNT, SIZE);
    }

}
