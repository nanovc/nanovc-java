package io.nanovc.reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link ClassType} for capturing generic type information at runtime.
 */
public class ClassTypeTests
{

    @Test
    public void String() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        ClassType classType = ClassType.of(String.class);
        assertEquals("String", classType.toString());
        assertEquals(String.class, classType.newInstance().getClass());
    }

    @Test
    public void ArrayListOfString_of() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        ClassType classType = ClassType.of(ArrayList.class, String.class);
        assertEquals("ArrayList<String>", classType.toString());
        assertEquals(ArrayList.class, classType.newInstance().getClass());
    }

    @Test
    public void HashMapOfStringToInteger() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        ClassType classType = ClassType.of(HashMap.class, String.class, Integer.class);
        assertEquals("HashMap<String,Integer>", classType.toString());
        assertEquals(HashMap.class, classType.newInstance().getClass());
    }
}
