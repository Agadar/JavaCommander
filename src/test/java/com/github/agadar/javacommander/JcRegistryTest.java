package com.github.agadar.javacommander;

import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;
import com.github.agadar.javacommander.translator.NoTranslator;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests com.github.agadar.javacommander.JcRegistry.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class JcRegistryTest {

    private static Foo foo;
    private static JcRegistry jcRegistry;

    @BeforeClass
    public static void SetupClass() throws NoSuchMethodException {
        foo = new Foo();
    }

    @Before
    public void setUp() {
        jcRegistry = new JcRegistry();
    }

    /**
     * Test of registerObject method, of class JcRegistry.
     */
    @Test
    public void testRegisterObject() {
        System.out.println("registerObject");

        // Register.
        jcRegistry.registerObject(foo);
        assertEquals(3, jcRegistry.getParsedCommands().size());

        // Confirm presence of commands by all names.
        assertTrue(jcRegistry.hasCommand("Bar"));
        assertTrue(jcRegistry.hasCommand("bar"));
        assertTrue(jcRegistry.hasCommand("BarWithParams"));
        assertTrue(jcRegistry.hasCommand("barWithParams"));
        assertTrue(jcRegistry.hasCommand("BarWithDefaultParams"));
        assertTrue(jcRegistry.hasCommand("barWithDefaultParams"));

        assertFalse(jcRegistry.hasCommand("BarMitzvah"));

        // Get parsed commands.
        final JcCommand barCommand = jcRegistry.getCommand("Bar").get();
        final JcCommand barWithParamsCommand = jcRegistry.getCommand("BarWithParams").get();
        final JcCommand barWithDefaultParamsCommand = jcRegistry.getCommand("BarWithDefaultParams").get();

        // Make sure they're the same as the commands known by synonyms.
        assertSame(barCommand, jcRegistry.getCommand("bar").get());
        assertSame(barWithParamsCommand, jcRegistry.getCommand("barWithParams").get());
        assertSame(barWithDefaultParamsCommand, jcRegistry.getCommand("barWithDefaultParams").get());

        assertNotSame(barCommand, barWithParamsCommand);
        assertNotSame(barCommand, barWithDefaultParamsCommand);
        assertNotSame(barWithParamsCommand, barWithDefaultParamsCommand);

        // Make sure the command fields are correct.
        assertEquals(2, barCommand.numberOfNames());
        assertEquals("Bar", barCommand.getNameByIndex(0));
        assertEquals("bar", barCommand.getNameByIndex(1));
        assertEquals("barDescription", barCommand.description);
        assertFalse(barCommand.hasOptions());
        assertTrue(barCommand.isMyObject(foo));

        assertEquals(2, barWithParamsCommand.numberOfNames());
        assertEquals("BarWithParams", barWithParamsCommand.getNameByIndex(0));
        assertEquals("barWithParams", barWithParamsCommand.getNameByIndex(1));
        assertEquals("barWithParamsDescription", barWithParamsCommand.description);
        assertTrue(barWithParamsCommand.hasOptions());
        assertTrue(barWithParamsCommand.isMyObject(foo));

        assertEquals(2, barWithDefaultParamsCommand.numberOfNames());
        assertEquals("BarWithDefaultParams", barWithDefaultParamsCommand.getNameByIndex(0));
        assertEquals("barWithDefaultParams", barWithDefaultParamsCommand.getNameByIndex(1));
        assertEquals("barWithDefaultParamsDescription", barWithDefaultParamsCommand.description);
        assertTrue(barWithDefaultParamsCommand.hasOptions());
        assertTrue(barWithDefaultParamsCommand.isMyObject(foo));

        // Get parsed options.
        final JcOption<String> stringOption = barWithParamsCommand.getOptionByName("stringParam").get();
        final JcOption<Integer> intOption = barWithParamsCommand.getOptionByName("intParam").get();
        final JcOption<Boolean> boolOption = barWithParamsCommand.getOptionByName("boolParam").get();

        final JcOption<String> stringDefaultOption = barWithDefaultParamsCommand.getOptionByName("stringDefaultParam").get();
        final JcOption<Integer> intDefaultOption = barWithDefaultParamsCommand.getOptionByName("intDefaultParam").get();
        final JcOption<Boolean> boolDefaultOption = barWithDefaultParamsCommand.getOptionByName("boolDefaultParam").get();

        // Make sure they're the same as the commands known by synonyms.
        assertSame(stringOption, barWithParamsCommand.getOptionByName("StringParam").get());
        assertSame(intOption, barWithParamsCommand.getOptionByName("IntParam").get());
        assertSame(boolOption, barWithParamsCommand.getOptionByName("BoolParam").get());

        assertSame(stringDefaultOption, barWithDefaultParamsCommand.getOptionByName("StringDefaultParam").get());
        assertSame(intDefaultOption, barWithDefaultParamsCommand.getOptionByName("IntDefaultParam").get());
        assertSame(boolDefaultOption, barWithDefaultParamsCommand.getOptionByName("BoolDefaultParam").get());

        // Make sure they're in the correct order.
        assertEquals(0, barWithParamsCommand.indexOfOption(stringOption));
        assertEquals(1, barWithParamsCommand.indexOfOption(intOption));
        assertEquals(2, barWithParamsCommand.indexOfOption(boolOption));

        assertEquals(0, barWithDefaultParamsCommand.indexOfOption(stringDefaultOption));
        assertEquals(1, barWithDefaultParamsCommand.indexOfOption(intDefaultOption));
        assertEquals(2, barWithDefaultParamsCommand.indexOfOption(boolDefaultOption));

        // Make sure the option fields are correct.
        assertEquals(2, stringOption.numberOfNames());
        assertEquals("StringParam", stringOption.getNameByIndex(0));
        assertEquals("stringParam", stringOption.getNameByIndex(1));
        assertEquals("stringParamDescription", stringOption.description);
        assertEquals(NoTranslator.class, stringOption.translator);
        assertEquals(String.class, stringOption.type);
        assertFalse(stringOption.hasDefaultValue);
        assertNull(stringOption.defaultValue);

        assertEquals(2, intOption.numberOfNames());
        assertEquals("IntParam", intOption.getNameByIndex(0));
        assertEquals("intParam", intOption.getNameByIndex(1));
        assertEquals("intParamDescription", intOption.description);
        assertEquals(NoTranslator.class, intOption.translator);
        assertEquals(int.class, intOption.type);
        assertFalse(intOption.hasDefaultValue);
        assertNull(intOption.defaultValue);

        assertEquals(2, boolOption.numberOfNames());
        assertEquals("BoolParam", boolOption.getNameByIndex(0));
        assertEquals("boolParam", boolOption.getNameByIndex(1));
        assertEquals("boolParamDescription", boolOption.description);
        assertEquals(NoTranslator.class, boolOption.translator);
        assertEquals(boolean.class, boolOption.type);
        assertFalse(boolOption.hasDefaultValue);
        assertNull(boolOption.defaultValue);

        assertEquals(2, stringDefaultOption.numberOfNames());
        assertEquals("StringDefaultParam", stringDefaultOption.getNameByIndex(0));
        assertEquals("stringDefaultParam", stringDefaultOption.getNameByIndex(1));
        assertEquals("stringDefaultParamDescription", stringDefaultOption.description);
        assertEquals(NoTranslator.class, stringDefaultOption.translator);
        assertEquals(String.class, stringDefaultOption.type);
        assertTrue(stringDefaultOption.hasDefaultValue);
        assertEquals("defaultString", stringDefaultOption.defaultValue);

        assertEquals(2, intDefaultOption.numberOfNames());
        assertEquals("IntDefaultParam", intDefaultOption.getNameByIndex(0));
        assertEquals("intDefaultParam", intDefaultOption.getNameByIndex(1));
        assertEquals("intDefaultParamDescription", intDefaultOption.description);
        assertEquals(NoTranslator.class, intDefaultOption.translator);
        assertEquals(int.class, intDefaultOption.type);
        assertTrue(intDefaultOption.hasDefaultValue);
        assertEquals(15, (int) intDefaultOption.defaultValue);

        assertEquals(2, boolDefaultOption.numberOfNames());
        assertEquals("BoolDefaultParam", boolDefaultOption.getNameByIndex(0));
        assertEquals("boolDefaultParam", boolDefaultOption.getNameByIndex(1));
        assertEquals("boolDefaultParamDescription", boolDefaultOption.description);
        assertEquals(NoTranslator.class, boolDefaultOption.translator);
        assertEquals(boolean.class, boolDefaultOption.type);
        assertTrue(boolDefaultOption.hasDefaultValue);
        assertEquals(true, boolDefaultOption.defaultValue);
    }

    /**
     * Test of unregisterObject method, of class JcRegistry.
     */
    @Test
    public void testUnregisterObject() {
        System.out.println("unregisterObject");

    }

    /**
     * Test of getCommand method, of class JcRegistry.
     */
    @Test
    public void testGetCommand() {
        System.out.println("getCommand");

    }

    /**
     * Test of getParsedCommands method, of class JcRegistry.
     */
    @Test
    public void testGetParsedCommands() {
        System.out.println("getParsedCommands");

    }

    /**
     * Test of hasCommand method, of class JcRegistry.
     */
    @Test
    public void testHasCommand() {
        System.out.println("hasCommand");

    }

    /**
     * Test of parseString method, of class JcRegistry.
     */
    @Test
    public void testParseString() {
        System.out.println("parseString");

    }

    private static final class Foo {

        @Command(names = {"Bar", "bar"}, description = "barDescription")
        public void Bar() {
        }

        @Command(names = {"BarWithParams", "barWithParams"}, description = "barWithParamsDescription", options = {
            @Option(names = {"StringParam", "stringParam"}, description = "stringParamDescription"),
            @Option(names = {"IntParam", "intParam"}, description = "intParamDescription"),
            @Option(names = {"BoolParam", "boolParam"}, description = "boolParamDescription")})
        public void BarWithParams(String stringParam, int intParam, boolean boolParam) {
        }

        @Command(names = {"BarWithDefaultParams", "barWithDefaultParams"}, description = "barWithDefaultParamsDescription", options = {
            @Option(names = {"StringDefaultParam", "stringDefaultParam"}, description = "stringDefaultParamDescription", hasDefaultValue = true, defaultValue = "defaultString"),
            @Option(names = {"IntDefaultParam", "intDefaultParam"}, description = "intDefaultParamDescription", hasDefaultValue = true, defaultValue = "15"),
            @Option(names = {"BoolDefaultParam", "boolDefaultParam"}, description = "boolDefaultParamDescription", hasDefaultValue = true, defaultValue = "true")})
        public void BarWithDefaultParams(String stringParam, int intParam, boolean boolParam) {
        }
    }
}
