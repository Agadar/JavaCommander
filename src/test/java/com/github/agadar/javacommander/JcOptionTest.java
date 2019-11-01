package com.github.agadar.javacommander;

import com.github.agadar.javacommander.example.IntArrayTranslator;
import com.github.agadar.javacommander.exception.OptionTranslatorException;
import com.github.agadar.javacommander.testclass.DataClass;
import com.github.agadar.javacommander.testclass.DataClassTranslator;
import com.github.agadar.javacommander.translator.NoTranslator;

import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests com.github.agadar.javacommander.JcOption.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class JcOptionTest {

    /**
     * Test of getPrimaryName method, of class JcOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionTranslatorException
     */
    @Test
    public void testGetPrimaryName() throws OptionTranslatorException {
        System.out.println("getPrimaryName");
        var instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class,
                "defaultValue", NoTranslator.class);
        assertEquals("one", instance.getPrimaryName());
    }

    /**
     * Test of hasDescription method, of class JcOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionTranslatorException
     */
    @Test
    public void testHasDescription() throws OptionTranslatorException {
        System.out.println("hasDescription");

        // Test true.
        var instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class,
                "defaultValue", NoTranslator.class);
        assertTrue(instance.hasDescription());

        // Test false with null.
        instance = new JcOption<>(Arrays.asList("one", "two", "three"), null, false, String.class, "defaultValue",
                NoTranslator.class);
        assertFalse(instance.hasDescription());

        // Test false with empty.
        instance = new JcOption<>(Arrays.asList("one", "two", "three"), "", false, String.class, "defaultValue",
                NoTranslator.class);
        assertFalse(instance.hasDescription());
    }

    /**
     * Test of hasTranslator method, of class JcOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionTranslatorException
     */
    @Test
    public void testHasTranslator() throws OptionTranslatorException {
        System.out.println("hasTranslator");

        // Test false with null.
        var instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class,
                "defaultValue", null);
        assertFalse(instance.hasTranslator());

        // Test false with NoTranslator.class.
        instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class,
                "defaultValue", NoTranslator.class);
        assertFalse(instance.hasTranslator());

        // Test true.
        var instance2 = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, DataClass.class,
                "defaultValue", DataClassTranslator.class);
        assertTrue(instance2.hasTranslator());
    }

    /**
     * Test of getNameByIndex method, of class JcOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionTranslatorException
     */
    @Test
    public void testGetNameByIndex() throws OptionTranslatorException {
        System.out.println("getNameByIndex");
        var instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class,
                "defaultValue", null);

        // Check lower bounding.
        assertEquals("one", instance.getNameByIndex(-1));

        // Check upper bounding.
        assertEquals("three", instance.getNameByIndex(3));

        // Check within bounding.
        assertEquals("two", instance.getNameByIndex(1));
    }

    /**
     * Test of numberOfNames method, of class JcOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionTranslatorException
     */
    @Test
    public void testNumberOfNames() throws OptionTranslatorException {
        System.out.println("numberOfNames");

        // Test with 3
        var instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class,
                "defaultValue", null);
        assertEquals(3, instance.numberOfNames());

        // Test with 6
        instance = new JcOption<>(Arrays.asList("one", "two", "three", "four", "five", "six"), "description", false,
                String.class, "defaultValue", null);
        assertEquals(6, instance.numberOfNames());
    }

    /**
     * Test of hashCode method, of class JcOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionTranslatorException
     */
    @Test
    public void testHashCode() throws OptionTranslatorException {
        System.out.println("hashCode");

        // True result.
        var instance1 = new JcOption<>(Arrays.asList("one", "two", "three"), "description1", false,
                String.class, "defaultValue", null);
        var instance2 = new JcOption<>(Arrays.asList("one"), "description2", true, Integer.class, "10",
                null);
        assertEquals(instance1.hashCode(), instance2.hashCode());

        // False result.
        var instance3 = new JcOption<>(Arrays.asList("one1"), "description2", true, Integer.class, "10",
                null);
        assertNotEquals(instance1.hashCode(), instance3.hashCode());
    }

    /**
     * Test of equals method, of class JcOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionTranslatorException
     */
    @Test
    public void testEquals() throws OptionTranslatorException {
        System.out.println("equals");

        // True result.
        var instance1 = new JcOption<>(Arrays.asList("one", "two", "three"), "description1", false,
                String.class, "defaultValue", null);
        var instance2 = new JcOption<>(Arrays.asList("one"), "description2", true, Integer.class, "10",
                null);
        assertEquals(instance1, instance2);

        // False result.
        var instance3 = new JcOption<>(Arrays.asList("one1"), "description2", true, Integer.class, "10",
                null);
        assertNotEquals(instance1, instance3);
    }

    /**
     * Test of translate method, of class JcOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionTranslatorException
     */
    @Test
    public void testTranslate() throws OptionTranslatorException {
        System.out.println("translate");

        // Test primitive option, that has no translator.
        var instance1 = new JcOption<Integer>(Arrays.asList("one", "two", "three"), "description1", false,
                int.class, null, null);
        assertEquals(Integer.valueOf(15), instance1.translate("15"));

        // Test primitive option, that has a translator.
        var instance2 = new JcOption<int[]>(Arrays.asList("one", "two", "three"), "description2", false,
                int[].class, null, IntArrayTranslator.class);
        assertArrayEquals(new int[] { 15, 10, 5 }, instance2.translate("15,10,5"));

        // Test object option, that has a translator.
        var instance3 = new JcOption<>(Arrays.asList("one", "two", "three"), "description3", false,
                DataClass.class, null, DataClassTranslator.class);
        assertEquals(new DataClass("dataClassString"), instance3.translate("dataClassString"));
    }
}
