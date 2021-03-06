package com.github.agadar.javacommander;

import com.github.agadar.javacommander.example.IntArrayOptionValueParser;
import com.github.agadar.javacommander.exception.OptionValueParserException;
import com.github.agadar.javacommander.optionvalueparser.NullOptionValueParser;
import com.github.agadar.javacommander.testclass.DataClass;
import com.github.agadar.javacommander.testclass.DataClassOptionValueParser;

import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests com.github.agadar.javacommander.JcCommandOption.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class JcCommandOptionTest {

    /**
     * Test of getPrimaryName method, of class JcCommandOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testGetPrimaryName() throws OptionValueParserException {
        System.out.println("getPrimaryName");
        var instance = new JcCommandOption<>(Arrays.asList("one", "two", "three"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);
        assertEquals("one", instance.getPrimaryName());
    }

    /**
     * Test of hashCode method, of class JcCommandOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testHashCode() throws OptionValueParserException {
        System.out.println("hashCode");

        // True result.
        var instance1 = new JcCommandOption<>(Arrays.asList("one", "two", "three"), "description1", String.class,
                "defaultValue", null, null);
        var instance2 = new JcCommandOption<>(Arrays.asList("one"), "description2", Integer.class, "10", null, null);
        assertEquals(instance1.hashCode(), instance2.hashCode());

        // False result.
        var instance3 = new JcCommandOption<>(Arrays.asList("one1"), "description2", Integer.class, "10", null, null);
        assertNotEquals(instance1.hashCode(), instance3.hashCode());
    }

    /**
     * Test of equals method, of class JcCommandOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testEquals() throws OptionValueParserException {
        System.out.println("equals");

        // True result.
        var instance1 = new JcCommandOption<>(Arrays.asList("one", "two", "three"), "description1", String.class,
                "defaultValue", null, null);
        var instance2 = new JcCommandOption<>(Arrays.asList("one"), "description2", Integer.class, "10", null, null);
        assertEquals(instance1, instance2);

        // False result.
        var instance3 = new JcCommandOption<>(Arrays.asList("one1"), "description2", Integer.class, "10", null, null);
        assertNotEquals(instance1, instance3);
    }

    /**
     * Test of parseOptionValue method, of class JcCommandOption.
     *
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testParseOptionValue() throws OptionValueParserException {
        System.out.println("parseOptionValue");

        // Test primitive option, that has no parser.
        var instance1 = new JcCommandOption<Integer>(Arrays.asList("one", "two", "three"), "description1", int.class,
                null, null, null);
        assertEquals(Integer.valueOf(15), instance1.parseOptionValue("15"));

        // Test primitive option, that has a parse.
        var instance2 = new JcCommandOption<int[]>(Arrays.asList("one", "two", "three"), "description2", int[].class,
                null, null, IntArrayOptionValueParser.class);
        assertArrayEquals(new int[] { 15, 10, 5 }, instance2.parseOptionValue("15,10,5"));

        // Test object option, that has a parser.
        var instance3 = new JcCommandOption<>(Arrays.asList("one", "two", "three"), "description3", DataClass.class,
                null, null, DataClassOptionValueParser.class);
        assertEquals(new DataClass("dataClassString"), instance3.parseOptionValue("dataClassString"));
    }
}
