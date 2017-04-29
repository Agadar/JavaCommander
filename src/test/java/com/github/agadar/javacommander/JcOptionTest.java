package com.github.agadar.javacommander;

import com.github.agadar.javacommander.translator.NoTranslator;
import com.github.agadar.javacommander.translator.OptionTranslator;
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
     */
    @Test
    public void testGetPrimaryName() {
        System.out.println("getPrimaryName");
        final JcOption instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class, "defaultValue", NoTranslator.class);
        assertEquals("one", instance.getPrimaryName());
    }

    /**
     * Test of hasDescription method, of class JcOption.
     */
    @Test
    public void testHasDescription() {
        System.out.println("hasDescription");

        // Test true.
        JcOption instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class, "defaultValue", NoTranslator.class);
        assertTrue(instance.hasDescription());

        // Test false with null.
        instance = new JcOption<>(Arrays.asList("one", "two", "three"), null, false, String.class, "defaultValue", NoTranslator.class);
        assertFalse(instance.hasDescription());

        // Test false with empty.
        instance = new JcOption<>(Arrays.asList("one", "two", "three"), "", false, String.class, "defaultValue", NoTranslator.class);
        assertFalse(instance.hasDescription());
    }

    /**
     * Test of hasTranslator method, of class JcOption.
     */
    @Test
    public void testHasTranslator() {
        System.out.println("hasTranslator");

        // Test false with null.
        JcOption instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class, "defaultValue", null);
        assertFalse(instance.hasTranslator());

        // Test false with NoTranslator.class.
        instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class, "defaultValue", NoTranslator.class);
        assertFalse(instance.hasTranslator());

        // Test true.
        instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class, "defaultValue", StringTranslator.class);
        assertTrue(instance.hasTranslator());
    }

    private final class StringTranslator implements OptionTranslator<String> {

        @Override
        public String translateString(String stringToParse) {
            return stringToParse;
        }

    }

    /**
     * Test of getNameByIndex method, of class JcOption.
     */
    @Test
    public void testGetNameByIndex() {
        System.out.println("getNameByIndex");
        final JcOption instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class, "defaultValue", null);

        // Check lower bounding.
        assertEquals("one", instance.getNameByIndex(-1));

        // Check upper bounding.
        assertEquals("three", instance.getNameByIndex(3));

        // Check within bounding.
        assertEquals("two", instance.getNameByIndex(1));
    }

    /**
     * Test of numberOfNames method, of class JcOption.
     */
    @Test
    public void testNumberOfNames() {
        System.out.println("numberOfNames");

        // Test with 3
        JcOption instance = new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class, "defaultValue", null);
        assertEquals(3, instance.numberOfNames());

        // Test with 6
        instance = new JcOption<>(Arrays.asList("one", "two", "three", "four", "five", "six"), "description", false, String.class, "defaultValue", null);
        assertEquals(6, instance.numberOfNames());
    }

    /**
     * Test of hashCode method, of class JcOption.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");

        // True result.
        final JcOption instance1 = new JcOption<>(Arrays.asList("one", "two", "three"), "description1", false, String.class, "defaultValue", null);
        final JcOption instance2 = new JcOption<>(Arrays.asList("one"), "description2", true, Integer.class, 10, null);
        assertEquals(instance1.hashCode(), instance2.hashCode());

        // False result.
        final JcOption instance3 = new JcOption<>(Arrays.asList("one1"), "description2", true, Integer.class, 10, null);
        assertNotEquals(instance1.hashCode(), instance3.hashCode());
    }

    /**
     * Test of equals method, of class JcOption.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");

        // True result.
        final JcOption instance1 = new JcOption<>(Arrays.asList("one", "two", "three"), "description1", false, String.class, "defaultValue", null);
        final JcOption instance2 = new JcOption<>(Arrays.asList("one"), "description2", true, Integer.class, 10, null);
        assertEquals(instance1, instance2);

        // False result.
        final JcOption instance3 = new JcOption<>(Arrays.asList("one1"), "description2", true, Integer.class, 10, null);
        assertNotEquals(instance1, instance3);
    }
}
