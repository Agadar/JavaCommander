package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.CommandInvocationException;
import com.github.agadar.javacommander.exception.OptionValueParserException;
import com.github.agadar.javacommander.optionvalueparser.NullOptionValueParser;
import com.github.agadar.javacommander.testclass.AnnotatedClass;
import com.github.agadar.javacommander.testclass.DataClass;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 * Tests com.github.agadar.javacommander.JcCommand.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class JcCommandTest {

    private static AnnotatedClass objectToInvokeOn;
    private static Method methodToInvokeOn;

    @BeforeClass
    public static void SetupClass() throws NoSuchMethodException {
        objectToInvokeOn = new AnnotatedClass();
        methodToInvokeOn = AnnotatedClass.class.getMethod("bar");
    }

    /**
     * Test of getPrimaryName method, of class JcCommand.
     */
    @Test
    public void testGetPrimaryName() {
        System.out.println("getPrimaryName");
        var jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description",
                new ArrayList<>(), methodToInvokeOn, objectToInvokeOn);
        assertEquals("one", jcCommand.getPrimaryName());
    }

    /**
     * Test of hasOptions method, of class JcCommand.
     *
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testHasOptions() throws OptionValueParserException {
        System.out.println("hasOptions");

        // Test false.
        JcCommand jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description", new ArrayList<>(),
                methodToInvokeOn, objectToInvokeOn);
        assertEquals(0, jcCommand.numberOfOptions());

        // Test true.
        var jcOptions = new ArrayList<JcCommandOption<?>>();
        jcOptions.add(new JcCommandOption<>(Arrays.asList("one", "two", "three"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class));
        jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description", jcOptions, methodToInvokeOn,
                objectToInvokeOn);
        assertEquals(1, jcCommand.numberOfOptions());
    }

    /**
     * Test of hasDescription method, of class JcCommand.
     */
    @Test
    public void testHasDescription() {
        System.out.println("hasDescription");

        // Test false with null.
        JcCommand jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), null, new ArrayList<>(),
                methodToInvokeOn, objectToInvokeOn);
        assertTrue(jcCommand.getDescription().isEmpty());

        // Test false with empty.
        jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "", new ArrayList<>(), methodToInvokeOn,
                objectToInvokeOn);
        assertTrue(jcCommand.getDescription().isEmpty());

        // Test true.
        jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description", new ArrayList<>(),
                methodToInvokeOn, objectToInvokeOn);
        assertFalse(jcCommand.getDescription().isEmpty());
    }

    /**
     * Test of hasOption method, of class JcCommand.
     *
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testHasOption() throws OptionValueParserException {
        System.out.println("hasOption");

        // Test false.
        var jcOptions = new ArrayList<JcCommandOption<?>>();
        var jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description", jcOptions,
                methodToInvokeOn, objectToInvokeOn);
        assertFalse(jcCommand.hasOption("one"));

        // Test true with primary name.
        jcOptions = new ArrayList<>();
        jcOptions.add(new JcCommandOption<>(Arrays.asList("one", "two", "three"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class));
        jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description", jcOptions, methodToInvokeOn,
                objectToInvokeOn);
        assertTrue(jcCommand.hasOption("one"));

        // test true with synonym.
        jcOptions = new ArrayList<>();
        jcOptions.add(new JcCommandOption<>(Arrays.asList("one", "two", "three"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class));
        jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description", jcOptions, methodToInvokeOn,
                objectToInvokeOn);
        assertTrue(jcCommand.hasOption("three"));
    }

    /**
     * Test of getOptionByIndex method, of class JcCommand.
     *
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testGetOptionByIndex() throws OptionValueParserException {
        System.out.println("getOptionByIndex");

        var jcOption1 = new JcCommandOption<>(Arrays.asList("one"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);
        var jcOption2 = new JcCommandOption<>(Arrays.asList("two"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);
        var jcOption3 = new JcCommandOption<>(Arrays.asList("three"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);

        var jcOptions = new ArrayList<JcCommandOption<?>>();
        jcOptions.add(jcOption1);
        jcOptions.add(jcOption2);
        jcOptions.add(jcOption3);

        var jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description", jcOptions,
                methodToInvokeOn, objectToInvokeOn);

        // Check lower bounding.
        assertSame(jcOption1, jcCommand.getOptionByIndex(-1).get());

        // Check upper bounding.
        assertSame(jcOption3, jcCommand.getOptionByIndex(3).get());

        // Check within bounding.
        assertSame(jcOption2, jcCommand.getOptionByIndex(1).get());
    }

    /**
     * Test of getOptionByName method, of class JcCommand.
     *
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testGetOptionByName() throws OptionValueParserException {
        System.out.println("getOptionByName");

        var jcOption1 = new JcCommandOption<>(Arrays.asList("one"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);
        var jcOption2 = new JcCommandOption<>(Arrays.asList("two"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);
        var jcOption3 = new JcCommandOption<>(Arrays.asList("three"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);

        final ArrayList<JcCommandOption<?>> jcOptions = new ArrayList<>();
        jcOptions.add(jcOption1);
        jcOptions.add(jcOption2);
        jcOptions.add(jcOption3);

        var jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description", jcOptions,
                methodToInvokeOn, objectToInvokeOn);

        // Test one that exists.
        assertSame(jcOption2, jcCommand.getOptionByName("two").get());

        // Test one that doesn't exist.
        assertFalse(jcCommand.getOptionByName("four").isPresent());
    }

    /**
     * Test of numberOfOptions method, of class JcCommand.
     *
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testNumberOfOptions() throws OptionValueParserException {
        System.out.println("numberOfOptions");

        var jcOption1 = new JcCommandOption<>(Arrays.asList("one"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);
        var jcOption2 = new JcCommandOption<>(Arrays.asList("two"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);
        var jcOption3 = new JcCommandOption<>(Arrays.asList("three"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);

        var jcOptions = new ArrayList<JcCommandOption<?>>();
        jcOptions.add(jcOption1);
        jcOptions.add(jcOption2);
        jcOptions.add(jcOption3);

        var jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description", jcOptions,
                methodToInvokeOn, objectToInvokeOn);
        assertEquals(3, jcCommand.numberOfOptions());
    }

    /**
     * Test of indexOfOption method, of class JcCommand.
     *
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testIndexOfOption() throws OptionValueParserException {
        System.out.println("indexOfOption");

        var jcOption1 = new JcCommandOption<>(Arrays.asList("one"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);
        var jcOption2 = new JcCommandOption<>(Arrays.asList("two"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);
        var jcOption3 = new JcCommandOption<>(Arrays.asList("three"), "description", Void.class,
                "defaultValue", null, NullOptionValueParser.class);

        var jcOptions = new ArrayList<JcCommandOption<?>>();
        jcOptions.add(jcOption1);
        jcOptions.add(jcOption2);

        var jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description", jcOptions,
                methodToInvokeOn, objectToInvokeOn);

        // Test existing.
        assertEquals(1, jcCommand.indexOfOption(jcOption2));

        // Test non-existing.
        assertEquals(-1, jcCommand.indexOfOption(jcOption3));
    }

    /**
     * Test of hashCode method, of class JcCommand.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");

        // True result.
        var jcCommand1 = new JcCommand(Arrays.asList("one", "two", "three"), "description1", null,
                methodToInvokeOn, objectToInvokeOn);
        var jcCommand2 = new JcCommand(Arrays.asList("one"), "description2", null, methodToInvokeOn,
                objectToInvokeOn);
        assertEquals(jcCommand1.hashCode(), jcCommand2.hashCode());

        // False result.
        var jcCommand3 = new JcCommand(Arrays.asList("two"), "description3", null, methodToInvokeOn,
                objectToInvokeOn);
        assertNotEquals(jcCommand1.hashCode(), jcCommand3.hashCode());
    }

    /**
     * Test of equals method, of class JcCommand.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");

        // True result.
        var jcCommand1 = new JcCommand(Arrays.asList("one", "two", "three"), "description1", null,
                methodToInvokeOn, objectToInvokeOn);
        var jcCommand2 = new JcCommand(Arrays.asList("one"), "description2", null, methodToInvokeOn,
                objectToInvokeOn);
        assertEquals(jcCommand1, jcCommand2);

        // False result.
        var jcCommand3 = new JcCommand(Arrays.asList("two"), "description3", null, methodToInvokeOn,
                objectToInvokeOn);
        assertNotEquals(jcCommand1, jcCommand3);
    }

    /**
     * Test of invoke method, of class JcCommand.
     *
     * @throws java.lang.NoSuchMethodException
     * @throws com.github.agadar.javacommander.exception.CommandInvocationException
     */
    @Test
    public void testInvoke() throws NoSuchMethodException, CommandInvocationException {
        System.out.println("invoke");

        // Invoke without parameters.
        var jcCommand1 = new JcCommand(Arrays.asList("one", "two", "three"), "description1", null,
                methodToInvokeOn, objectToInvokeOn);
        jcCommand1.invoke();

        // Invoke with parameters.
        var jcCommand2 = new JcCommand(Arrays.asList("one", "two", "three"),
                "description1", null,
                AnnotatedClass.class.getMethod("barWithParams", String.class, int.class, boolean.class),
                objectToInvokeOn);
        jcCommand2.invoke("stringParam", 15, true);

        // Invoke with custom parameter.
        var jcCommand3 = new JcCommand(Arrays.asList("one", "two", "three"),
                "description1", null, AnnotatedClass.class.getMethod("barWithBazParam", DataClass.class),
                objectToInvokeOn);
        jcCommand3.invoke(new DataClass("defaultValue"));
    }

    /**
     * Test of isMyObject method, of class JcCommand.
     */
    @Test
    public void testIsMyObject() {
        System.out.println("isMyObject");
        var jcCommand = new JcCommand(Arrays.asList("one", "two", "three"), "description1", null,
                methodToInvokeOn, objectToInvokeOn);
        assertTrue(jcCommand.isMyObject(objectToInvokeOn));
    }
}
