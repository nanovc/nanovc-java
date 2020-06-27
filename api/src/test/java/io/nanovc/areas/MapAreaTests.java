package io.nanovc.areas;

import io.nanovc.content.ByteArrayContent;
import io.nanovc.content.StringContent;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * Test the {@link MapArea}.
 */
public class MapAreaTests
{

    /**
     * Tests the {@link MapArea} with a {@link HashMap} implementation.
     */
    public static class MapAreaTests_HashMap extends StringMapAreaTestsBase<MapArea<StringContent>>
    {

        /**
         * Creates the map area being tested.
         *
         * @return A new instance of the map area under test.
         */
        @Override protected MapArea<StringContent> createMapAreaUnderTest()
        {
            return new MapArea<>(new HashMap<>());
        }

        /**
         * Tests that we can create the {@link MapArea}.
         */
        @Test
        public void testCreation()
        {
            new MapArea<StringContent>(new HashMap<>());
            new MapArea<ByteArrayContent>(new HashMap<>());
        }
    }

    /**
     * Tests the {@link MapArea} with a {@link LinkedHashMap} implementation.
     */
    public static class MapAreaTests_LinkedHashMap extends StringMapAreaTestsBase<MapArea<StringContent>>
    {

        /**
         * Creates the map area being tested.
         *
         * @return A new instance of the map area under test.
         */
        @Override protected MapArea<StringContent> createMapAreaUnderTest()
        {
            return new MapArea<>(new LinkedHashMap<>());
        }

        /**
         * Tests that we can create the {@link MapArea}.
         */
        @Test
        public void testCreation()
        {
            new MapArea<StringContent>(new LinkedHashMap<>());
            new MapArea<ByteArrayContent>(new LinkedHashMap<>());
        }
    }

    /**
     * Tests the {@link MapArea} with a {@link java.util.TreeMap} implementation.
     */
    public static class MapAreaTests_TreeMap extends StringMapAreaTestsBase<MapArea<StringContent>>
    {

        /**
         * Creates the map area being tested.
         *
         * @return A new instance of the map area under test.
         */
        @Override protected MapArea<StringContent> createMapAreaUnderTest()
        {
            return new MapArea<>(new TreeMap<>());
        }

        /**
         * Tests that we can create the {@link MapArea}.
         */
        @Test
        public void testCreation()
        {
            new MapArea<StringContent>(new TreeMap<>());
            new MapArea<ByteArrayContent>(new TreeMap<>());
        }
    }

}
