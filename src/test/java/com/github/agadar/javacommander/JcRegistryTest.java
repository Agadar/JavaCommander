package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.OptionAnnotationException;
import com.github.agadar.javacommander.exception.OptionValueParserException;
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
    private static final int NONSTATIC_METHODS_IN_FOO = 5;

    @BeforeClass
    public static void SetupClass() {
        foo = new AnnotatedClass();
    }

    @Before
    public void setUp() {
        jcRegistry = new JcRegistry();
    }

    /**
     * Test of unregisterObject method, of class JcRegistry.
     *
     * @throws com.github.agadar.javacommander.exception.OptionAnnotationException
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testUnregisterObject() throws OptionAnnotationException, OptionValueParserException {
        System.out.println("unregisterObject");

        // Assert the registry is empty, then register the object.
        assertEquals(0, jcRegistry.getParsedCommands().size());
        jcRegistry.registerObject(foo);

        // Assert the annotations of the methods within foo have been parsed.
        assertEquals(NONSTATIC_METHODS_IN_FOO, jcRegistry.getParsedCommands().size());

        // Unregister the object.
        jcRegistry.unregisterObject(foo);

        // Assert the commands have been removed.
        assertEquals(0, jcRegistry.getParsedCommands().size());
    }

    /**
     * Test of unregisterClass method, of class JcRegistry.
     *
     * @throws com.github.agadar.javacommander.exception.OptionAnnotationException
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testUnregisterClass() throws OptionAnnotationException, OptionValueParserException {
        System.out.println("unregisterClass");

        // Assert the registry is empty, then register the class.
        assertEquals(0, jcRegistry.getParsedCommands().size());
        jcRegistry.registerClass(AnnotatedClass.class);

        // Assert the annotations of the methods within the class have been parsed.
        assertEquals(1, jcRegistry.getParsedCommands().size());

        // Unregister the class.
        jcRegistry.unregisterClass(AnnotatedClass.class);

        // Assert the commands have been removed.
        assertEquals(0, jcRegistry.getParsedCommands().size());
    }

    /**
     * Test of getCommand method, of class JcRegistry.
     *
     * @throws com.github.agadar.javacommander.exception.OptionAnnotationException
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testGetCommand() throws OptionAnnotationException, OptionValueParserException {
        System.out.println("getCommand");

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
        assertFalse(jcRegistry.getCommand("BarStatic").isPresent());
        assertFalse(jcRegistry.getCommand("barStatic").isPresent());

        jcRegistry.registerObject(foo);

        assertTrue(jcRegistry.getCommand("Bar").isPresent());
        assertTrue(jcRegistry.getCommand("bar").isPresent());
        assertTrue(jcRegistry.getCommand("BarWithParams").isPresent());
        assertTrue(jcRegistry.getCommand("barWithParams").isPresent());
        assertTrue(jcRegistry.getCommand("BarWithDefaultParams").isPresent());
        assertTrue(jcRegistry.getCommand("barWithDefaultParams").isPresent());
        assertTrue(jcRegistry.getCommand("BarWithBazParam").isPresent());
        assertTrue(jcRegistry.getCommand("barWithBazParam").isPresent());
        assertTrue(jcRegistry.getCommand("barNameless").isPresent());
        assertFalse(jcRegistry.getCommand("BarStatic").isPresent());
        assertFalse(jcRegistry.getCommand("barStatic").isPresent());

        jcRegistry.registerClass(AnnotatedClass.class);

        assertTrue(jcRegistry.getCommand("Bar").isPresent());
        assertTrue(jcRegistry.getCommand("bar").isPresent());
        assertTrue(jcRegistry.getCommand("BarWithParams").isPresent());
        assertTrue(jcRegistry.getCommand("barWithParams").isPresent());
        assertTrue(jcRegistry.getCommand("BarWithDefaultParams").isPresent());
        assertTrue(jcRegistry.getCommand("barWithDefaultParams").isPresent());
        assertTrue(jcRegistry.getCommand("BarWithBazParam").isPresent());
        assertTrue(jcRegistry.getCommand("barWithBazParam").isPresent());
        assertTrue(jcRegistry.getCommand("barNameless").isPresent());
        assertTrue(jcRegistry.getCommand("BarStatic").isPresent());
        assertTrue(jcRegistry.getCommand("barStatic").isPresent());

        jcRegistry.unregisterObject(foo);

        assertFalse(jcRegistry.getCommand("Bar").isPresent());
        assertFalse(jcRegistry.getCommand("bar").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithParams").isPresent());
        assertFalse(jcRegistry.getCommand("barWithParams").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithDefaultParams").isPresent());
        assertFalse(jcRegistry.getCommand("barWithDefaultParams").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithBazParam").isPresent());
        assertFalse(jcRegistry.getCommand("barWithBazParam").isPresent());
        assertFalse(jcRegistry.getCommand("barNameless").isPresent());
        assertTrue(jcRegistry.getCommand("BarStatic").isPresent());
        assertTrue(jcRegistry.getCommand("barStatic").isPresent());

        jcRegistry.unregisterClass(AnnotatedClass.class);

        assertFalse(jcRegistry.getCommand("Bar").isPresent());
        assertFalse(jcRegistry.getCommand("bar").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithParams").isPresent());
        assertFalse(jcRegistry.getCommand("barWithParams").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithDefaultParams").isPresent());
        assertFalse(jcRegistry.getCommand("barWithDefaultParams").isPresent());
        assertFalse(jcRegistry.getCommand("BarWithBazParam").isPresent());
        assertFalse(jcRegistry.getCommand("barWithBazParam").isPresent());
        assertFalse(jcRegistry.getCommand("barNameless").isPresent());
        assertFalse(jcRegistry.getCommand("BarStatic").isPresent());
        assertFalse(jcRegistry.getCommand("barStatic").isPresent());
    }

    /**
     * Test of registerObject method, of class JcRegistry.
     *
     * Due to the nature of JcRegistry, this also uses/tests other functions of
     * JcRegistry.
     *
     * @throws com.github.agadar.javacommander.exception.OptionAnnotationException
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testRegisterObject() throws OptionAnnotationException, OptionValueParserException {
        System.out.println("registerObject");

        // Register.
        jcRegistry.registerObject(foo);
        assertEquals(NONSTATIC_METHODS_IN_FOO, jcRegistry.getParsedCommands().size());

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
        assertFalse(jcRegistry.hasCommand("BarMitzvah"));

        // Get parsed commands.
        final JcCommand barCommand = jcRegistry.getCommand("Bar").get();
        final JcCommand barWithParamsCommand = jcRegistry.getCommand("BarWithParams").get();
        final JcCommand barWithDefaultParamsCommand = jcRegistry.getCommand("BarWithDefaultParams").get();
        final JcCommand barWithBazParamCommand = jcRegistry.getCommand("barWithBazParam").get();
        final JcCommand barNamelessCommand = jcRegistry.getCommand("barNameless").get();

        // Make sure they're the same as the commands known by synonyms.
        assertSame(barCommand, jcRegistry.getCommand("bar").get());
        assertSame(barWithParamsCommand, jcRegistry.getCommand("barWithParams").get());
        assertSame(barWithDefaultParamsCommand, jcRegistry.getCommand("barWithDefaultParams").get());
        assertSame(barWithBazParamCommand, jcRegistry.getCommand("barWithBazParam").get());

        // Make sure the command fields are correct.
        assertEquals(2, barCommand.numberOfNames());
        assertEquals("Bar", barCommand.getNameByIndex(0));
        assertEquals("bar", barCommand.getNameByIndex(1));
        assertEquals("barDescription", barCommand.getDescription());
        assertEquals(0, barCommand.numberOfOptions());
        assertTrue(barCommand.isMyObject(foo));

        assertEquals(2, barWithParamsCommand.numberOfNames());
        assertEquals("BarWithParams", barWithParamsCommand.getNameByIndex(0));
        assertEquals("barWithParams", barWithParamsCommand.getNameByIndex(1));
        assertEquals("barWithParamsDescription", barWithParamsCommand.getDescription());
        assertEquals(3, barWithParamsCommand.numberOfOptions());
        assertTrue(barWithParamsCommand.isMyObject(foo));

        assertEquals(2, barWithDefaultParamsCommand.numberOfNames());
        assertEquals("BarWithDefaultParams", barWithDefaultParamsCommand.getNameByIndex(0));
        assertEquals("barWithDefaultParams", barWithDefaultParamsCommand.getNameByIndex(1));
        assertEquals("barWithDefaultParamsDescription", barWithDefaultParamsCommand.getDescription());
        assertEquals(3, barWithDefaultParamsCommand.numberOfOptions());
        assertTrue(barWithDefaultParamsCommand.isMyObject(foo));

        assertEquals(2, barWithBazParamCommand.numberOfNames());
        assertEquals("BarWithBazParam", barWithBazParamCommand.getNameByIndex(0));
        assertEquals("barWithBazParam", barWithBazParamCommand.getNameByIndex(1));
        assertEquals("barWithBazParamDescription", barWithBazParamCommand.getDescription());
        assertEquals(1, barWithBazParamCommand.numberOfOptions());
        assertTrue(barWithBazParamCommand.isMyObject(foo));

        assertEquals(1, barNamelessCommand.numberOfNames());
        assertEquals("barNameless", barNamelessCommand.getNameByIndex(0));
        assertEquals("barNamelessDescription", barNamelessCommand.getDescription());
        assertEquals(1, barNamelessCommand.numberOfOptions());
        assertTrue(barNamelessCommand.isMyObject(foo));

        // Get parsed options.
        var stringOption = barWithParamsCommand.getOptionByName("stringParam").get();
        var intOption = barWithParamsCommand.getOptionByName("intParam").get();
        var boolOption = barWithParamsCommand.getOptionByName("boolParam").get();

        var stringDefaultOption = barWithDefaultParamsCommand.getOptionByName("stringDefaultParam").get();
        var intDefaultOption = barWithDefaultParamsCommand.getOptionByName("intDefaultParam").get();
        var boolDefaultOption = barWithDefaultParamsCommand.getOptionByName("boolDefaultParam").get();

        var bazOption = barWithBazParamCommand.getOptionByName("bazParam").get();

        var namelessOption = barNamelessCommand.getOptionByName("arg0").get();

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
        assertEquals("stringParamDescription", stringOption.getDescription());
        assertFalse(stringOption.hasValueParser());
        assertFalse(stringOption.isHasDefaultValue());
        assertNull(stringOption.getDefaultValue());

        assertEquals(2, intOption.numberOfNames());
        assertEquals("IntParam", intOption.getNameByIndex(0));
        assertEquals("intParam", intOption.getNameByIndex(1));
        assertEquals("intParamDescription", intOption.getDescription());
        assertFalse(intOption.hasValueParser());
        assertFalse(intOption.isHasDefaultValue());
        assertNull(intOption.getDefaultValue());

        assertEquals(2, boolOption.numberOfNames());
        assertEquals("BoolParam", boolOption.getNameByIndex(0));
        assertEquals("boolParam", boolOption.getNameByIndex(1));
        assertEquals("boolParamDescription", boolOption.getDescription());
        assertFalse(boolOption.hasValueParser());
        assertFalse(boolOption.isHasDefaultValue());
        assertNull(boolOption.getDefaultValue());

        assertEquals(2, stringDefaultOption.numberOfNames());
        assertEquals("StringDefaultParam", stringDefaultOption.getNameByIndex(0));
        assertEquals("stringDefaultParam", stringDefaultOption.getNameByIndex(1));
        assertEquals("stringDefaultParamDescription", stringDefaultOption.getDescription());
        assertFalse(stringDefaultOption.hasValueParser());
        assertTrue(stringDefaultOption.isHasDefaultValue());
        assertEquals("defaultString", stringDefaultOption.getDefaultValue());

        assertEquals(2, intDefaultOption.numberOfNames());
        assertEquals("IntDefaultParam", intDefaultOption.getNameByIndex(0));
        assertEquals("intDefaultParam", intDefaultOption.getNameByIndex(1));
        assertEquals("intDefaultParamDescription", intDefaultOption.getDescription());
        assertFalse(intDefaultOption.hasValueParser());
        assertTrue(intDefaultOption.isHasDefaultValue());
        assertEquals(15, (int) intDefaultOption.getDefaultValue());

        assertEquals(2, boolDefaultOption.numberOfNames());
        assertEquals("BoolDefaultParam", boolDefaultOption.getNameByIndex(0));
        assertEquals("boolDefaultParam", boolDefaultOption.getNameByIndex(1));
        assertEquals("boolDefaultParamDescription", boolDefaultOption.getDescription());
        assertFalse(boolDefaultOption.hasValueParser());
        assertTrue(boolDefaultOption.isHasDefaultValue());
        assertEquals(true, boolDefaultOption.getDefaultValue());

        assertEquals(2, bazOption.numberOfNames());
        assertEquals("BazParam", bazOption.getNameByIndex(0));
        assertEquals("bazParam", bazOption.getNameByIndex(1));
        assertEquals("bazParamDescription", bazOption.getDescription());
        assertTrue(bazOption.hasValueParser());
        assertTrue(bazOption.isHasDefaultValue());
        assertEquals(new DataClass("defaultBaz"), bazOption.getDefaultValue());

        assertEquals(1, namelessOption.numberOfNames());
        assertEquals("arg0", namelessOption.getNameByIndex(0));
        assertEquals("barNamelessParamDescription", namelessOption.getDescription());
        assertFalse(namelessOption.hasValueParser());
        assertFalse(namelessOption.isHasDefaultValue());
        assertNull(namelessOption.getDefaultValue());
    }

    /**
     * Test of registerClassmethod, of class JcRegistry.
     *
     * Due to the nature of JcRegistry, this also uses/tests other functions of
     * JcRegistry.
     *
     * @throws com.github.agadar.javacommander.exception.OptionAnnotationException
     * @throws com.github.agadar.javacommander.exception.OptionValueParserException
     */
    @Test
    public void testRegisterClass() throws OptionAnnotationException, OptionValueParserException {
        System.out.println("registerClass");

        // Register.
        jcRegistry.registerClass(AnnotatedClass.class);
        assertEquals(1, jcRegistry.getParsedCommands().size());

        // Confirm presence of commands by all names.
        assertTrue(jcRegistry.hasCommand("BarStatic"));
        assertTrue(jcRegistry.hasCommand("barStatic"));
        assertFalse(jcRegistry.hasCommand("BarMitzvah"));

        // Get parsed commands.
        final JcCommand barStaticCommand = jcRegistry.getCommand("BarStatic").get();

        // Make sure they're the same as the commands known by synonyms.
        assertSame(barStaticCommand, jcRegistry.getCommand("barStatic").get());

        // Make sure the command fields are correct.
        assertEquals(2, barStaticCommand.numberOfNames());
        assertEquals("BarStatic", barStaticCommand.getNameByIndex(0));
        assertEquals("barStatic", barStaticCommand.getNameByIndex(1));
        assertEquals("barStaticDescription", barStaticCommand.getDescription());
        assertEquals(0, barStaticCommand.numberOfOptions());
        assertTrue(barStaticCommand.isMyObject(AnnotatedClass.class));
    }
}
