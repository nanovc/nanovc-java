package io.nanovc.indexes;

import io.nanovc.NanoVersionControlTestsBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the functionality related to {@link Bits}.
 */
public class BitsTests extends NanoVersionControlTestsBase
{

    /**
     * Tests that we understand the Java integer notation 0b0000_0000.
     */
    @Test
    public void testJavaByteNotationAsInteger()
    {
        // Positive:
        assertEquals(0, 0b0000_0000);
        assertEquals(1, 0b0000_0001);
        assertEquals(2, 0b0000_0010);
        assertEquals(4, 0b0000_0100);
        assertEquals(8, 0b0000_1000);
        assertEquals(16, 0b0001_0000);
        assertEquals(32, 0b0010_0000);
        assertEquals(64, 0b0100_0000);
        assertEquals(127, 0b0111_1111);

        assertEquals(128, 0b1000_0000);

        assertEquals(256, 0b1_0000_0000);
        assertEquals(512, 0b10_0000_0000);

        assertEquals(16777216, 0b00000001_00000000_00000000_00000000);
        assertEquals(16908288, 0b00000001_00000010_00000000_00000000);
        assertEquals(16909312, 0b00000001_00000010_00000100_00000000);
        assertEquals(16909320, 0b00000001_00000010_00000100_00001000);

        // Negative:
        assertEquals(-128, 0b1111_1111_1111_1111_1111_1111_1000_0000);
        assertEquals(-64, 0b1111_1111_1111_1111_1111_1111_1100_0000);
        assertEquals(-32, 0b1111_1111_1111_1111_1111_1111_1110_0000);
        assertEquals(-16, 0b1111_1111_1111_1111_1111_1111_1111_0000);
        assertEquals(-8, 0b1111_1111_1111_1111_1111_1111_1111_1000);
        assertEquals(-4, 0b1111_1111_1111_1111_1111_1111_1111_1100);
        assertEquals(-2, 0b1111_1111_1111_1111_1111_1111_1111_1110);
        assertEquals(-1, 0b1111_1111_1111_1111_1111_1111_1111_1111);
    }

    /**
     * Tests that we understand the Java byte notation 0b0000_0000.
     */
    @Test
    public void testJavaByteNotationAsByte()
    {
        // Positive:
        assertEquals((byte) 0, (byte) 0b0000_0000);
        assertEquals((byte) 1, (byte) 0b0000_0001);
        assertEquals((byte) 2, (byte) 0b0000_0010);
        assertEquals((byte) 4, (byte) 0b0000_0100);
        assertEquals((byte) 8, (byte) 0b0000_1000);
        assertEquals((byte) 16, (byte) 0b0001_0000);
        assertEquals((byte) 32, (byte) 0b0010_0000);
        assertEquals((byte) 64, (byte) 0b0100_0000);
        assertEquals((byte) 127, (byte) 0b0111_1111);

        assertEquals((byte) 128, (byte) 0b1000_0000);

        assertEquals((byte) 0, (byte) 0b1_0000_0000);
        assertEquals((byte) 256, (byte) 0b1_0000_0000);
        assertEquals((byte) 512, (byte) 0b10_0000_0000);

        // Negative:
        assertEquals((byte) -128, (byte) 0b1000_0000);
        assertEquals((byte) -64, (byte) 0b1100_0000);
        assertEquals((byte) -32, (byte) 0b1110_0000);
        assertEquals((byte) -16, (byte) 0b1111_0000);
        assertEquals((byte) -8, (byte) 0b1111_1000);
        assertEquals((byte) -4, (byte) 0b1111_1100);
        assertEquals((byte) -2, (byte) 0b1111_1110);
        assertEquals((byte) -1, (byte) 0b1111_1111);

        // Bit Shifting: >> = signed right shift
        // https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.19
        assertEquals((byte) (0b1_0000_0000 >> 1), (byte) 0b0_1000_0000);
        assertEquals((byte) (0b1_0000_0000 >>> 1), (byte) 0b0_1000_0000);
    }

    /**
     * Tests that we understand the Java signed shift right operator: >>
     * https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.19
     */
    @Test
    public void testSignedShiftRight()
    {
        // Bit Shifting: >> = signed right shift
        // https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.19
        assertEquals(-128, 0b1111_1111_1111_1111_1111_1111_1000_0000);
        assertEquals(-128 >> 1, 0b1111_1111_1111_1111_1111_1111_1000_0000 >> 1);
        assertEquals(-128 >> 1, 0b1111_1111_1111_1111_1111_1111_1100_0000);
        assertEquals(-64, 0b1111_1111_1111_1111_1111_1111_1100_0000);
    }

    /**
     * Tests that we understand the Java unsigned shift right operator: >>>
     * https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.19
     */
    @Test
    public void testUnsignedShiftRight()
    {
        // Bit Shifting: >>> = unsigned right shift
        // https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.19
        assertEquals(-128, 0b1111_1111_1111_1111_1111_1111_1000_0000);
        assertEquals(-128 >>> 1, 0b1111_1111_1111_1111_1111_1111_1000_0000 >>> 1);
        assertEquals(-128 >>> 1, 0b0111_1111_1111_1111_1111_1111_1100_0000);
        assertEquals(2147483584, 0b0111_1111_1111_1111_1111_1111_1100_0000);
    }

    @Test
    public void testByte1ToIntConversion()
    {
        // 0:
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byte1ToInt(new byte[]{0}));
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byte1ToInt(new byte[]{0b00000000}));
        assertEquals(0, Bits.byte1ToInt(new byte[]{0b00000000}));
        assertEquals(0, Bits.byte1ToInt(new byte[]{0}));

        // 1:
        assertEquals(0b00000001_00000000_00000000_00000000, Bits.byte1ToInt(new byte[]{1}));
        assertEquals(0b00000001_00000000_00000000_00000000, Bits.byte1ToInt(new byte[]{0b00000001}));
        assertEquals(16_777_216, Bits.byte1ToInt(new byte[]{0b000000001}));
        assertEquals(16_777_216, Bits.byte1ToInt(new byte[]{1}));
    }

    @Test
    public void testByte2ToIntConversion()
    {
        // 0, 0:
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byte2ToInt(new byte[]{0, 0}));
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byte2ToInt(new byte[]{0b00000000, 0b00000000}));
        assertEquals(0, Bits.byte2ToInt(new byte[]{0b00000000, 0b00000000}));
        assertEquals(0, Bits.byte2ToInt(new byte[]{0, 0}));

        // 1, 2:
        assertEquals(0b00000001_00000010_00000000_00000000, Bits.byte2ToInt(new byte[]{1, 2}));
        assertEquals(0b00000001_00000010_00000000_00000000, Bits.byte2ToInt(new byte[]{0b00000001, 0b00000010}));
        assertEquals(16_908_288, Bits.byte2ToInt(new byte[]{0b00000001, 0b00000010}));
        assertEquals(16_908_288, Bits.byte2ToInt(new byte[]{1, 2}));
    }

    @Test
    public void testByte3ToIntConversion()
    {
        // 0, 0, 0:
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byte3ToInt(new byte[]{0, 0, 0}));
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byte3ToInt(new byte[]{0b00000000, 0b00000000, 0b00000000}));
        assertEquals(0, Bits.byte3ToInt(new byte[]{0b00000000, 0b00000000, 0b00000000}));
        assertEquals(0, Bits.byte3ToInt(new byte[]{0, 0, 0}));

        // 1, 2, 4:
        assertEquals(0b00000001_00000010_00000100_00000000, Bits.byte3ToInt(new byte[]{1, 2, 4}));
        assertEquals(0b00000001_00000010_00000100_00000000, Bits.byte3ToInt(new byte[]{0b00000001, 0b00000010, 0b00000100}));
        assertEquals(16_909_312, Bits.byte3ToInt(new byte[]{0b00000001, 0b00000010, 0b00000100}));
        assertEquals(16_909_312, Bits.byte3ToInt(new byte[]{1, 2, 4}));
    }

    @Test
    public void testByte4ToIntConversion()
    {
        // 0, 0, 0, 0:
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byte4ToInt(new byte[]{0, 0, 0, 0}));
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byte4ToInt(new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000}));
        assertEquals(0, Bits.byte4ToInt(new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000}));
        assertEquals(0, Bits.byte4ToInt(new byte[]{0, 0, 0, 0}));

        // 1, 2, 4, 8:
        assertEquals(0b00000001_00000010_00000100_00001000, Bits.byte4ToInt(new byte[]{1, 2, 4, 8}));
        assertEquals(0b00000001_00000010_00000100_00001000, Bits.byte4ToInt(new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000}));
        assertEquals(16_909_320, Bits.byte4ToInt(new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000}));
        assertEquals(16_909_320, Bits.byte4ToInt(new byte[]{1, 2, 4, 8}));
    }

    @Test
    public void testByteArrayToIntConversion()
    {
        int count;

        // Count of 1:
        count = 1;
        // 0, [0], 0, 0, 0, 0:
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byteArrayToInt(new byte[]{0, 0, 0, 0, 0, 0}, 1, count));
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byteArrayToInt(new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000}, 1, count));
        assertEquals(0, Bits.byteArrayToInt(new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000}, 1, count));
        assertEquals(0, Bits.byteArrayToInt(new byte[]{0, 0, 0, 0, 0, 0}, 1, count));

        // 1, [2], 4, 8, 16, 32:
        assertEquals(0b00000010_00000000_00000000_00000000, Bits.byteArrayToInt(new byte[]{1, 2, 4, 8, 16, 32}, 1, count));
        assertEquals(0b00000010_00000000_00000000_00000000, Bits.byteArrayToInt(new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000}, 1, count));
        assertEquals(33_554_432, Bits.byteArrayToInt(new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000}, 1, count));
        assertEquals(33_554_432, Bits.byteArrayToInt(new byte[]{1, 2, 4, 8, 16, 32}, 1, count));


        // Count of 2:
        count = 2;
        // 0, [0, 0], 0, 0, 0:
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byteArrayToInt(new byte[]{0, 0, 0, 0, 0, 0}, 1, count));
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byteArrayToInt(new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000}, 1, count));
        assertEquals(0, Bits.byteArrayToInt(new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000}, 1, count));
        assertEquals(0, Bits.byteArrayToInt(new byte[]{0, 0, 0, 0, 0, 0}, 1, count));

        // 1, [2, 4], 8, 16, 32:
        assertEquals(0b00000010_00000100_00000000_00000000, Bits.byteArrayToInt(new byte[]{1, 2, 4, 8, 16, 32}, 1, count));
        assertEquals(0b00000010_00000100_00000000_00000000, Bits.byteArrayToInt(new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000}, 1, count));
        assertEquals(33_816_576, Bits.byteArrayToInt(new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000}, 1, count));
        assertEquals(33_816_576, Bits.byteArrayToInt(new byte[]{1, 2, 4, 8, 16, 32}, 1, count));


        // Count of 3:
        count = 3;
        // 0, [0, 0, 0], 0, 0:
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byteArrayToInt(new byte[]{0, 0, 0, 0, 0, 0}, 1, count));
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byteArrayToInt(new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000}, 1, count));
        assertEquals(0, Bits.byteArrayToInt(new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000}, 1, count));
        assertEquals(0, Bits.byteArrayToInt(new byte[]{0, 0, 0, 0, 0, 0}, 1, count));

        // 1, [2, 4, 8], 16, 32:
        assertEquals(0b00000010_00000100_00001000_00000000, Bits.byteArrayToInt(new byte[]{1, 2, 4, 8, 16, 32}, 1, count));
        assertEquals(0b00000010_00000100_00001000_00000000, Bits.byteArrayToInt(new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000}, 1, count));
        assertEquals(33_818_624, Bits.byteArrayToInt(new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000}, 1, count));
        assertEquals(33_818_624, Bits.byteArrayToInt(new byte[]{1, 2, 4, 8, 16, 32}, 1, count));


        // Count of 4:
        count = 4;
        // 0, [0, 0, 0, 0], 0:
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byteArrayToInt(new byte[]{0, 0, 0, 0, 0, 0}, 1, count));
        assertEquals(0b00000000_00000000_00000000_00000000, Bits.byteArrayToInt(new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000}, 1, count));
        assertEquals(0, Bits.byteArrayToInt(new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000}, 1, count));
        assertEquals(0, Bits.byteArrayToInt(new byte[]{0, 0, 0, 0, 0, 0}, 1, count));

        // 1, [2, 4, 8, 16], 32:
        assertEquals(0b00000010_00000100_00001000_00010000, Bits.byteArrayToInt(new byte[]{1, 2, 4, 8, 16, 32}, 1, count));
        assertEquals(0b00000010_00000100_00001000_00010000, Bits.byteArrayToInt(new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000}, 1, count));
        assertEquals(33_818_640, Bits.byteArrayToInt(new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000}, 1, count));
        assertEquals(33_818_640, Bits.byteArrayToInt(new byte[]{1, 2, 4, 8, 16, 32}, 1, count));
    }

}
