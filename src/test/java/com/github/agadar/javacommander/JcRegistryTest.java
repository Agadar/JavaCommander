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
        assertEquals(0, jcRegistry.getCommands().size());
        jcRegistry.registerFromObject(foo);

        // Assert the annotations of the methods within foo have been parsed.
        assertEquals(NONSTATIC_METHODS_IN_FOO, jcRegistry.getCommands().size());

        // Unregister the object.
        jcRegistry.unregisterFromObject(foo);

        // Assert the commands have been removed.
        assertEquals(0, jcRegistry.getCommands().size());
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
        assertEquals(0, jcRegistry.getCommands().size());
        jcRegistry.registerFromClass(AnnotatedClass.class);

        // Assert the annotations of the methods within the class have been parsed.
        assertEquals(1, jcRegistry.getCommands().size());

        // Unregister the class.
        jcRegistry.unregisterFromClass(AnnotatedClass.class);

        // Assert the commands have been removed.
        assertEquals(0, jcRegistry.getCommands().size());
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

        jcRegistry.registerFromObject(foo);

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

        jcRegistry.registerFromClass(AnnotatedClass.class);

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

        jcRegistry.unregisterFromObject(foo);

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

        jcRegistry.unregisterFromClass(AnnotatedClass.class);

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
        jcRegistry.registerFromObject(foo);
        assertEquals(NONSTATIC_METHODS_IN_FOO, jcRegistry.getCommands().size());

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
        assertEquals(2, barCommand.getNames().size());
        assertEquals("Bar", barCommand.getNames().get(0));
        assertEquals("bar", barCommand.getNames().get(1));
        assertEquals("barDescription", barCommand.getDescription());
        assertEquals(0, barCommand.numberOfOptions());
        assertTrue(barCommand.isMyObject(foo));

        assertEquals(2, barWithParamsCommand.getNames().size());
        assertEquals("BarWithParams", barWithParamsCommand.getNames().get(0));
        assertEquals("barWithParams", barWithParamsCommand.getNames().get(1));
        assertEquals("barWithParamsDescription", barWithParamsCommand.getDescription());
        assertEquals(3, barWithParamsCommand.numberOfOptions());
        assertTrue(barWithParamsCommand.isMyObject(foo));

        assertEquals(2, barWithDefaultParamsCommand.getNames().size());
        assertEquals("BarWithDefaultParams", barWithDefaultParamsCommand.getNames().get(0));
        assertEquals("barWithDefaultParams", barWithDefaultParamsCommand.getNames().get(1));
        assertEquals("barWithDefaultParamsDescription", barWithDefaultParamsCommand.getDescription());
        assertEquals(3, barWithDefaultParamsCommand.numberOfOptions());
        assertTrue(barWithDefaultParamsCommand.isMyObject(foo));

        assertEquals(2, barWithBazParamCommand.getNames().size());
        assertEquals("BarWithBazParam", barWithBazParamCommand.getNames().get(0));
        assertEquals("barWithBazParam", barWithBazParamCommand.getNames().get(1));
        assertEquals("barWithBazParamDescription", barWithBazParamCommand.getDescription());
        assertEquals(1, barWithBazParamCommand.numberOfOptions());
        assertTrue(barWithBazParamCommand.isMyObject(foo));

        assertEquals(1, barNamelessCommand.getNames().size());
        assertEquals("barNameless", barNamelessCommand.getNames().get(0));
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
        assertEquals(2, stringOption.getNames().size());
        assertEquals("StringParam", stringOption.getNames().get(0));
        assertEquals("stringParam", stringOption.getNames().get(1));
        assertEquals("stringParamDescription", stringOption.getDescription());
        assertNull(stringOption.getDefaultValue());

        assertEquals(2, intOption.getNames().size());
        assertEquals("IntParam", intOption.getNames().get(0));
        assertEquals("intParam", intOption.getNames().get(1));
        assertEquals("intParamDescription", intOption.getDescription());
        assertNull(intOption.getDefaultValue());

        assertEquals(2, boolOption.getNames().size());
        assertEquals("BoolParam", boolOption.getNames().get(0));
        assertEquals("boolParam", boolOption.getNames().get(1));
        assertEquals("boolParamDescription", boolOption.getDescription());
        assertNull(boolOption.getDefaultValue());

        assertEquals(2, stringDefaultOption.getNames().size());
        assertEquals("StringDefaultParam", stringDefaultOption.getNames().get(0));
        assertEquals("stringDefaultParam", stringDefaultOption.getNames().get(1));
        assertEquals("stringDefaultParamDescription", stringDefaultOption.getDescription());
        assertEquals("defaultString", stringDefaultOption.getDefaultValue());

        assertEquals(2, intDefaultOption.getNames().size());
        assertEquals("IntDefaultParam", intDefaultOption.getNames().get(0));
        assertEquals("intDefaultParam", intDefaultOption.getNames().get(1));
        assertEquals("intDefaultParamDescription", intDefaultOption.getDescription());
        assertEquals(15, (int) intDefaultOption.getDefaultValue());

        assertEquals(2, boolDefaultOption.getNames().size());
        assertEquals("BoolDefaultParam", boolDefaultOption.getNames().get(0));
        assertEquals("boolDefaultParam", boolDefaultOption.getNames().get(1));
        assertEquals("boolDefaultParamDescription", boolDefaultOption.getDescription());
        assertEquals(true, boolDefaultOption.getDefaultValue());

        assertEquals(2, bazOption.getNames().size());
        assertEquals("BazParam", bazOption.getNames().get(0));
        assertEquals("bazParam", bazOption.getNames().get(1));
        assertEquals("bazParamDescription", bazOption.getDescription());
        assertEquals(new DataClass("defaultBaz"), bazOption.getDefaultValue());

        assertEquals(1, namelessOption.getNames().size());
        assertEquals("arg0", namelessOption.getNames().get(0));
        assertEquals("barNamelessParamDescription", namelessOption.getDescription());
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
        jcRegistry.registerFromClass(AnnotatedClass.class);
        assertEquals(1, jcRegistry.getCommands().size());

        // Confirm presence of commands by all names.
        assertTrue(jcRegistry.hasCommand("BarStatic"));
        assertTrue(jcRegistry.hasCommand("barStatic"));
        assertFalse(jcRegistry.hasCommand("BarMitzvah"));

        // Get parsed commands.
        final JcCommand barStaticCommand = jcRegistry.getCommand("BarStatic").get();

        // Make sure they're the same as the commands known by synonyms.
        assertSame(barStaticCommand, jcRegistry.getCommand("barStatic").get());

        // Make sure the command fields are correct.
        assertEquals(2, barStaticCommand.getNames().size());
        assertEquals("BarStatic", barStaticCommand.getNames().get(0));
        assertEquals("barStatic", barStaticCommand.getNames().get(1));
        assertEquals("barStaticDescription", barStaticCommand.getDescription());
        assertEquals(0, barStaticCommand.numberOfOptions());
        assertTrue(barStaticCommand.isMyObject(AnnotatedClass.class));
    }
}
