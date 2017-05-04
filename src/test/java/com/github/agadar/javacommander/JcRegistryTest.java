package com.github.agadar.javacommander;

import com.github.agadar.javacommander.testclass.AnnotatedClass;
import com.github.agadar.javacommander.testclass.DataClass;

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

    private static AnnotatedClass foo;
    private static JcRegistry jcRegistry;
    private static final int METHODS_IN_FOO = 6;

    @BeforeClass
    public static void SetupClass() {
        foo = new AnnotatedClass();
    }

    @Before
    public void setUp() {
        jcRegistry = new JcRegistry();
    }

    /**
     * Test of unregisterObject method, of class JcRegistry. Currently identical
     * to testGetParsedCommands().
     */
    @Test
    public void testUnregisterObject() {
        System.out.println("unregisterObject");

        // Assert the registry is empty, then register the object.
        assertEquals(0, jcRegistry.getParsedCommands().size());
        jcRegistry.registerObject(foo);

        // Assert the annotations of the methods within foo have been parsed.
        assertEquals(METHODS_IN_FOO, jcRegistry.getParsedCommands().size());

        // Unregister the object.
        jcRegistry.unregisterObject(foo);

        // Assert the commands have been removed.
        assertEquals(0, jcRegistry.getParsedCommands().size());
    }

    /**
     * Test of getCommand method, of class JcRegistry.
     */
    @Test
    public void testGetCommand() {
        System.out.println("getCommand");

        // Assert the commands are not present.
        assertFalse(jcRegistry.getCommand("Bar").isPresent());
        assertFalse(jcRegistry.getCommand("bar").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithParams").isPresent());
        assertFalse(jcRegistry.getCommand("barWithParams").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithDefaultParams").isPresent());
        assertFalse(jcRegistry.getCommand("barWithDefaultParams").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithBazParam").isPresent());
        assertFalse(jcRegistry.getCommand("barWithBazParam").isPresent());
        assertFalse(jcRegistry.getCommand("barNameless").isPresent());
        assertFalse(jcRegistry.getCommand("").isPresent());

        jcRegistry.registerObject(foo); // Register foo.

        // Assert the commands are present.
        assertTrue(jcRegistry.getCommand("Bar").isPresent());
        assertTrue(jcRegistry.getCommand("bar").isPresent());
        assertTrue(jcRegistry.getCommand("BarWithParams").isPresent());
        assertTrue(jcRegistry.getCommand("barWithParams").isPresent());
        assertTrue(jcRegistry.getCommand("BarWithDefaultParams").isPresent());
        assertTrue(jcRegistry.getCommand("barWithDefaultParams").isPresent());
        assertTrue(jcRegistry.getCommand("BarWithBazParam").isPresent());
        assertTrue(jcRegistry.getCommand("barWithBazParam").isPresent());
        assertTrue(jcRegistry.getCommand("barNameless").isPresent());
        assertTrue(jcRegistry.getCommand("").isPresent());

        jcRegistry.unregisterObject(foo);   // Unregister foo.

        // Assert the commands are no longer present.
        assertFalse(jcRegistry.getCommand("Bar").isPresent());
        assertFalse(jcRegistry.getCommand("bar").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithParams").isPresent());
        assertFalse(jcRegistry.getCommand("barWithParams").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithDefaultParams").isPresent());
        assertFalse(jcRegistry.getCommand("barWithDefaultParams").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithBazParam").isPresent());
        assertFalse(jcRegistry.getCommand("barWithBazParam").isPresent());
        assertFalse(jcRegistry.getCommand("barNameless").isPresent());
        assertFalse(jcRegistry.getCommand("").isPresent());
    }

    /**
     * Test of getParsedCommands method, of class JcRegistry. Currently
     * identical to testUnregisterObject().
     */
    @Test
    public void testGetParsedCommands() {
        System.out.println("getParsedCommands");

        // Assert the registry is empty, then register the object.
        assertEquals(0, jcRegistry.getParsedCommands().size());
        jcRegistry.registerObject(foo);

        // Assert the annotations of the methods within foo have been parsed.
        assertEquals(METHODS_IN_FOO, jcRegistry.getParsedCommands().size());

        // Unregister the object.
        jcRegistry.unregisterObject(foo);

        // Assert the commands have been removed.
        assertEquals(0, jcRegistry.getParsedCommands().size());
    }

    /**
     * Test of registerObject method, of class JcRegistry.
     *
     * Due to the nature of JcRegistry, this also uses/tests other functions of
     * JcRegistry.
     */
    @Test
    public void testRegisterObject() {
        System.out.println("registerObject");

        // Register.
        jcRegistry.registerObject(foo);
        assertEquals(METHODS_IN_FOO, jcRegistry.getParsedCommands().size());

        // Confirm presence of commands by all names.
        assertTrue(jcRegistry.hasCommand("Bar"));
        assertTrue(jcRegistry.hasCommand("bar"));
        assertTrue(jcRegistry.hasCommand("BarWithParams"));
        assertTrue(jcRegistry.hasCommand("barWithParams"));
        assertTrue(jcRegistry.hasCommand("BarWithDefaultParams"));
        assertTrue(jcRegistry.hasCommand("barWithDefaultParams"));
        assertTrue(jcRegistry.hasCommand("BarWithBazParam"));
        assertTrue(jcRegistry.hasCommand("barWithBazParam"));
        assertTrue(jcRegistry.getCommand("barNameless").isPresent());
        assertTrue(jcRegistry.getCommand("").isPresent());
        assertFalse(jcRegistry.hasCommand("BarMitzvah"));

        // Get parsed commands.
        final JcCommand barCommand = jcRegistry.getCommand("Bar").get();
        final JcCommand barWithParamsCommand = jcRegistry.getCommand("BarWithParams").get();
        final JcCommand barWithDefaultParamsCommand = jcRegistry.getCommand("BarWithDefaultParams").get();
        final JcCommand barWithBazParamCommand = jcRegistry.getCommand("barWithBazParam").get();
        final JcCommand barNamelessCommand = jcRegistry.getCommand("barNameless").get();
        final JcCommand barEmptyNameCommand = jcRegistry.getCommand("").get();

        // Make sure they're the same as the commands known by synonyms.
        assertSame(barCommand, jcRegistry.getCommand("bar").get());
        assertSame(barWithParamsCommand, jcRegistry.getCommand("barWithParams").get());
        assertSame(barWithDefaultParamsCommand, jcRegistry.getCommand("barWithDefaultParams").get());
        assertSame(barWithBazParamCommand, jcRegistry.getCommand("barWithBazParam").get());

        // Make sure the command fields are correct.
        assertEquals(2, barCommand.numberOfNames());
        assertEquals("Bar", barCommand.getNameByIndex(0));
        assertEquals("bar", barCommand.getNameByIndex(1));
        assertEquals("barDescription", barCommand.description);
        assertEquals(0, barCommand.numberOfOptions());
        assertTrue(barCommand.isMyObject(foo));

        assertEquals(2, barWithParamsCommand.numberOfNames());
        assertEquals("BarWithParams", barWithParamsCommand.getNameByIndex(0));
        assertEquals("barWithParams", barWithParamsCommand.getNameByIndex(1));
        assertEquals("barWithParamsDescription", barWithParamsCommand.description);
        assertEquals(3, barWithParamsCommand.numberOfOptions());
        assertTrue(barWithParamsCommand.isMyObject(foo));

        assertEquals(2, barWithDefaultParamsCommand.numberOfNames());
        assertEquals("BarWithDefaultParams", barWithDefaultParamsCommand.getNameByIndex(0));
        assertEquals("barWithDefaultParams", barWithDefaultParamsCommand.getNameByIndex(1));
        assertEquals("barWithDefaultParamsDescription", barWithDefaultParamsCommand.description);
        assertEquals(3, barWithDefaultParamsCommand.numberOfOptions());
        assertTrue(barWithDefaultParamsCommand.isMyObject(foo));

        assertEquals(2, barWithBazParamCommand.numberOfNames());
        assertEquals("BarWithBazParam", barWithBazParamCommand.getNameByIndex(0));
        assertEquals("barWithBazParam", barWithBazParamCommand.getNameByIndex(1));
        assertEquals("barWithBazParamDescription", barWithBazParamCommand.description);
        assertEquals(1, barWithBazParamCommand.numberOfOptions());
        assertTrue(barWithBazParamCommand.isMyObject(foo));
        
        assertEquals(1, barNamelessCommand.numberOfNames());
        assertEquals("barNameless", barNamelessCommand.getNameByIndex(0));
        assertEquals("barNamelessDescription", barNamelessCommand.description);
        assertEquals(1, barNamelessCommand.numberOfOptions());
        assertTrue(barNamelessCommand.isMyObject(foo));

        assertEquals(1, barEmptyNameCommand.numberOfNames());
        assertEquals("", barEmptyNameCommand.getNameByIndex(0));
        assertEquals("barEmptyNameDescription", barEmptyNameCommand.description);
        assertEquals(1, barEmptyNameCommand.numberOfOptions());
        assertTrue(barEmptyNameCommand.isMyObject(foo));
        
        // Get parsed options.
        final JcOption<String> stringOption = barWithParamsCommand.getOptionByName("stringParam").get();
        final JcOption<Integer> intOption = barWithParamsCommand.getOptionByName("intParam").get();
        final JcOption<Boolean> boolOption = barWithParamsCommand.getOptionByName("boolParam").get();

        final JcOption<String> stringDefaultOption = barWithDefaultParamsCommand.getOptionByName("stringDefaultParam").get();
        final JcOption<Integer> intDefaultOption = barWithDefaultParamsCommand.getOptionByName("intDefaultParam").get();
        final JcOption<Boolean> boolDefaultOption = barWithDefaultParamsCommand.getOptionByName("boolDefaultParam").get();

        final JcOption<DataClass> bazOption = barWithBazParamCommand.getOptionByName("bazParam").get();
        
        final JcOption<DataClass> namelessOption = barNamelessCommand.getOptionByName("arg0").get();      
        final JcOption<DataClass> emptyNameOption = barEmptyNameCommand.getOptionByName("arg0").get();

        // Make sure they're the same as the options known by synonyms.
        assertSame(stringOption, barWithParamsCommand.getOptionByName("StringParam").get());
        assertSame(intOption, barWithParamsCommand.getOptionByName("IntParam").get());
        assertSame(boolOption, barWithParamsCommand.getOptionByName("BoolParam").get());

        assertSame(stringDefaultOption, barWithDefaultParamsCommand.getOptionByName("StringDefaultParam").get());
        assertSame(intDefaultOption, barWithDefaultParamsCommand.getOptionByName("IntDefaultParam").get());
        assertSame(boolDefaultOption, barWithDefaultParamsCommand.getOptionByName("BoolDefaultParam").get());

        assertSame(bazOption, barWithBazParamCommand.getOptionByName("BazParam").get());

        // Make sure they're in the correct order.
        assertEquals(0, barWithParamsCommand.indexOfOption(stringOption));
        assertEquals(1, barWithParamsCommand.indexOfOption(intOption));
        assertEquals(2, barWithParamsCommand.indexOfOption(boolOption));

        assertEquals(0, barWithDefaultParamsCommand.indexOfOption(stringDefaultOption));
        assertEquals(1, barWithDefaultParamsCommand.indexOfOption(intDefaultOption));
        assertEquals(2, barWithDefaultParamsCommand.indexOfOption(boolDefaultOption));

        assertEquals(0, barWithBazParamCommand.indexOfOption(bazOption));

        // Make sure the option fields are correct.
        assertEquals(2, stringOption.numberOfNames());
        assertEquals("StringParam", stringOption.getNameByIndex(0));
        assertEquals("stringParam", stringOption.getNameByIndex(1));
        assertEquals("stringParamDescription", stringOption.description);
        assertFalse(stringOption.hasTranslator());
        assertFalse(stringOption.hasDefaultValue);
        assertNull(stringOption.defaultValue);

        assertEquals(2, intOption.numberOfNames());
        assertEquals("IntParam", intOption.getNameByIndex(0));
        assertEquals("intParam", intOption.getNameByIndex(1));
        assertEquals("intParamDescription", intOption.description);
        assertFalse(intOption.hasTranslator());
        assertFalse(intOption.hasDefaultValue);
        assertNull(intOption.defaultValue);

        assertEquals(2, boolOption.numberOfNames());
        assertEquals("BoolParam", boolOption.getNameByIndex(0));
        assertEquals("boolParam", boolOption.getNameByIndex(1));
        assertEquals("boolParamDescription", boolOption.description);
        assertFalse(boolOption.hasTranslator());
        assertFalse(boolOption.hasDefaultValue);
        assertNull(boolOption.defaultValue);

        assertEquals(2, stringDefaultOption.numberOfNames());
        assertEquals("StringDefaultParam", stringDefaultOption.getNameByIndex(0));
        assertEquals("stringDefaultParam", stringDefaultOption.getNameByIndex(1));
        assertEquals("stringDefaultParamDescription", stringDefaultOption.description);
        assertFalse(stringDefaultOption.hasTranslator());
        assertTrue(stringDefaultOption.hasDefaultValue);
        assertEquals("defaultString", stringDefaultOption.defaultValue);

        assertEquals(2, intDefaultOption.numberOfNames());
        assertEquals("IntDefaultParam", intDefaultOption.getNameByIndex(0));
        assertEquals("intDefaultParam", intDefaultOption.getNameByIndex(1));
        assertEquals("intDefaultParamDescription", intDefaultOption.description);
        assertFalse(intDefaultOption.hasTranslator());
        assertTrue(intDefaultOption.hasDefaultValue);
        assertEquals(15, (int) intDefaultOption.defaultValue);

        assertEquals(2, boolDefaultOption.numberOfNames());
        assertEquals("BoolDefaultParam", boolDefaultOption.getNameByIndex(0));
        assertEquals("boolDefaultParam", boolDefaultOption.getNameByIndex(1));
        assertEquals("boolDefaultParamDescription", boolDefaultOption.description);
        assertFalse(boolDefaultOption.hasTranslator());
        assertTrue(boolDefaultOption.hasDefaultValue);
        assertEquals(true, boolDefaultOption.defaultValue);

        assertEquals(2, bazOption.numberOfNames());
        assertEquals("BazParam", bazOption.getNameByIndex(0));
        assertEquals("bazParam", bazOption.getNameByIndex(1));
        assertEquals("bazParamDescription", bazOption.description);
        assertTrue(bazOption.hasTranslator());
        assertTrue(bazOption.hasDefaultValue);
        assertEquals(new DataClass("defaultBaz"), bazOption.defaultValue);
        
        assertEquals(1, namelessOption.numberOfNames());
        assertEquals("arg0", namelessOption.getNameByIndex(0));
        assertEquals("barNamelessParamDescription", namelessOption.description);
        assertFalse(namelessOption.hasTranslator());
        assertFalse(namelessOption.hasDefaultValue);
        assertNull(namelessOption.defaultValue);
        
        assertEquals(1, emptyNameOption.numberOfNames());
        assertEquals("arg0", emptyNameOption.getNameByIndex(0));
        assertEquals("barEmptyNameParamDescription", emptyNameOption.description);
        assertFalse(emptyNameOption.hasTranslator());
        assertFalse(emptyNameOption.hasDefaultValue);
        assertNull(emptyNameOption.defaultValue);
    }
}
