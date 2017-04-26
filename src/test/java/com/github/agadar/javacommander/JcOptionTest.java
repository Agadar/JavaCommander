package com.github.agadar.javacommander;

import com.github.agadar.javacommander.translator.NoTranslator;
import com.github.agadar.javacommander.translator.OptionTranslator;

import org.junit.Test;
import static org.junit.Assert.*;

/**
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
        final JcOption instance = new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", NoTranslator.class);
        assertEquals("one", instance.getPrimaryName());
    }

    /**
     * Test of hasDescription method, of class JcOption.
     */
    @Test
    public void testHasDescription() {
        System.out.println("hasDescription");

        // Test true.
        JcOption instance = new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", NoTranslator.class);
        assertTrue(instance.hasDescription());

        // Test false with null.
        instance = new JcOption<>(new String[]{"one", "two", "three"}, null, false, String.class, "defaultValue", NoTranslator.class);
        assertFalse(instance.hasDescription());

        // Test false with empty.
        instance = new JcOption<>(new String[]{"one", "two", "three"}, "", false, String.class, "defaultValue", NoTranslator.class);
        assertFalse(instance.hasDescription());
    }

    /**
     * Test of hasTranslator method, of class JcOption.
     */
    @Test
    public void testHasTranslator() {
        System.out.println("hasTranslator");

        // Test false with null.
        JcOption instance = new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", null);
        assertFalse(instance.hasTranslator());

        // Test false with NoTranslator.class.
        instance = new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", NoTranslator.class);
        assertFalse(instance.hasTranslator());

        // Test true.
        instance = new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", StringTranslator.class);
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
        final JcOption instance = new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", null);

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
        JcOption instance = new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", null);
        assertEquals(3, instance.numberOfNames());

        // Test with 6
        instance = new JcOption<>(new String[]{"one", "two", "three", "four", "five", "six"}, "description", false, String.class, "defaultValue", null);
        assertEquals(6, instance.numberOfNames());
    }
}
