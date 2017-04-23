package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.CommandInvocationException;
import com.github.agadar.javacommander.exception.NoValueForOptionException;
import com.github.agadar.javacommander.exception.OptionAnnotationException;
import com.github.agadar.javacommander.exception.OptionTranslatorException;
import com.github.agadar.javacommander.exception.UnknownCommandException;
import com.github.agadar.javacommander.exception.UnknownOptionException;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the functionalities of com.martin.javacommander.JavaCommander.
 *
 * @author Agadar
 */
public class JavaCommanderTest {

    /**
     * The JavaCommander instance we'll be testing.
     */
    private JavaCommander javaCommander;

    /**
     * Arbitrary test class we'll be testing with.
     */
    private TestClass testClass;

    /**
     * Number of commands in JavaCommander. Equal to number of basic commands
     * plus whatever is found in TestClass.
     */
    private final static int NUMBER_OF_COMMANDS = TestClass.class.getDeclaredMethods().length;

    /**
     * Tests registering the test object.
     *
     */
    public void testRegisterObject() throws OptionAnnotationException, OptionTranslatorException {
        javaCommander = new JavaCommander();
        testClass = new TestClass();
        javaCommander.registerObject(testClass);

        // Ensure the object was registered.
        Collection<JcCommand> commands = javaCommander.jcRegistry.getParsedCommands();
        assertEquals(NUMBER_OF_COMMANDS, commands.size());
    }

    /**
     * Tests unregistering the test object.
     */
    public void testUnregisterObject() {
        javaCommander.unregisterObject(testClass);

        // Ensure the object was unregistered.
        Collection<JcCommand> commands = javaCommander.jcRegistry.getParsedCommands();
        assertEquals(0, commands.size());
    }

    /**
     * No-arguments command
     *
     */
    @Test
    public void testNoArguments() throws OptionAnnotationException, OptionTranslatorException, UnknownCommandException, UnknownOptionException, NoValueForOptionException, CommandInvocationException {
        // Register object
        testRegisterObject();

        // Call command, no args. Should succeed.
        String args = "a";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Call command, with args. Should throw JavaCommanderException.
        args = "a -nonsense 5";
        try {
            javaCommander.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (Exception ex) {
            // Ignore, as this is what should happen.
        }

        // Unregister object
        testUnregisterObject();
    }

    /**
     * Single-argument command, no default value
     *
     * @throws com.github.agadar.javacommander.JavaCommanderException
     */
    @Test
    public void testSingleArgumentWithNoDefault() throws OptionAnnotationException, OptionTranslatorException {
        // Register object
        testRegisterObject();

        // Call command, no arg. Should fail.
        String args = "b0";
        try {
            javaCommander.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (Exception ex) {
            // Ignore, as this is what should happen.
        }

        // Call command, with explicit arg. Should succeed.
        args = "b0 arg0 'somevalue'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Call command, with implicit arg. Should succeed.
        args = "b0 'somevalue'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Unregister object
        testUnregisterObject();
    }

    /**
     * Single-argument command, default value
     *
     * @throws com.github.agadar.javacommander.JavaCommanderException
     */
    @Test
    public void testSingleArgumentWithDefault() throws OptionAnnotationException, OptionTranslatorException {
        // Register object
        testRegisterObject();

        // Call command, no arg. Should succeed.
        String args = "b1";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Call command, with arg. Should succeed.
        args = "b1 'somevalue'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Unregister object
        testUnregisterObject();
    }

    /**
     * Multi-argument command, no default values
     *
     * @throws com.github.agadar.javacommander.JavaCommanderException
     */
    @Test
    public void testMultiArgumentNoDefaults() throws OptionAnnotationException, OptionTranslatorException {
        // Register object
        testRegisterObject();

        // Call command, too few arguments, explicit options. Should fail.
        String args = "c0 arg0 'somevalue'";
        try {
            javaCommander.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (Exception ex) {
            // Ignore, as this is what should happen.
        }

        // Call command, too few arguments, implicit options. Should fail.
        args = "c0 'somevalue'";
        try {
            javaCommander.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (Exception ex) {
            // Ignore, as this is what should happen.
        }

        // Call command, too many arguments, explicit options. Should fail.
        args = "c0 arg0 'somevalue' arg1 '4,5,6' arg2 5";
        try {
            javaCommander.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (Exception ex) {
            // Ignore, as this is what should happen.
        }

        // Call command, too many arguments, implicit options. Should fail.
        args = "c0 'somevalue' '4,5,6' 5";
        try {
            javaCommander.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (Exception ex) {
            // Ignore, as this is what should happen.
        }

        // Call command with explicit options. Should succeed.
        args = "c0 arg1 '4,5,6' arg0 'somevalue'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Call command with implicit options. Should succeed.
        args = "c0 'somevalue' '4,5,6'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Unregister object
        testUnregisterObject();
    }

    /**
     * Multi-argument command, some default values
     *
     * @throws com.github.agadar.javacommander.JavaCommanderException
     */
    @Test
    public void testMultiArgumentSomeDefaults() throws OptionAnnotationException, OptionTranslatorException {
        // Register object
        testRegisterObject();

        // Skipping default values, should succeed.
        String args = "c1 arg1 '4,5,6'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Filling in all values, should succeed.
        args = "c1 arg1 '4,5,6' arg0 'somevalue'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Unregister object
        testUnregisterObject();
    }

    /**
     * Multi-argument command, all default values
     *
     * @throws com.github.agadar.javacommander.JavaCommanderException
     */
    @Test
    public void testMultiArgumentAllDefaults() throws OptionAnnotationException, OptionTranslatorException {
        // Register object
        testRegisterObject();

        // Skipping default values, should succeed.
        String args = "c2";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Filling in all values, should succeed.
        args = "c2 arg1 '4,5,6' arg0 'somevalue'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Unregister object
        testUnregisterObject();
    }

    /**
     * Single test for when option annotations are not part of a command
     * annotation but are instead annotated on parameters.
     *
     * @throws JavaCommanderException
     */
    @Test
    public void testParameterAnnotations() throws OptionAnnotationException, OptionTranslatorException {
        // Register object
        testRegisterObject();

        // Skipping default values, should succeed.
        String args = "d arg1 '4,5,6'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Filling in all values, should succeed.
        args = "d arg1 '4,5,6' arg0 'somevalue'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Unregister object
        testUnregisterObject();
    }

    /**
     * Tests using the master command.
     *
     * @throws JavaCommanderException
     */
    @Test
    public void testMasterCommand() throws OptionAnnotationException, OptionTranslatorException {
        // Register object
        testRegisterObject();

        // Make a call, using a single implicit option
        String args = "'some value'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Make a call, using a single explicit option
        args = "arg0 'some value'";
        try {
            javaCommander.execute(args);
        } catch (Exception ex) {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Unregister object
        testUnregisterObject();
    }
}
