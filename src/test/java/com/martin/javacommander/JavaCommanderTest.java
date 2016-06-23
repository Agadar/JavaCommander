package com.martin.javacommander;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the functionalities of com.martin.javacommander.JavaCommander.
 *
 * @author marti
 */
public class JavaCommanderTest
{

    /**
     * The instance we'll be testing on.
     */
    public JavaCommander jc;
    public TestClass tc;

    /**
     * Tests registering the test object.
     *
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public void testRegisterObject() throws JavaCommanderException
    {
        jc = new JavaCommander();
        tc = new TestClass();
        jc.registerObject(tc);

        // Ensure the object was registered.
        List<PCommand> commands = jc.getParsedCommands();
        assertTrue(commands.size() == 8);   // 6 commands from TestClass + 2 default commands (help and exit)
    }

    /**
     * Tests unregistering the test object.
     */
    public void testUnregisterObject()
    {
        jc.unregisterObject(tc);

        // Ensure the object was unregistered.
        List<PCommand> commands = jc.getParsedCommands();
        assertTrue(commands.size() == 2);   // remaining 2 default commands (help and exit)
    }

    /**
     * No-arguments command
     *
     * @throws com.martin.javacommander.JavaCommanderException
     */
    @Test
    public void testNoArguments() throws JavaCommanderException
    {
        System.out.println("execute, no-arguments command");

        // Register object
        testRegisterObject();

        // Call command, no args. Should succeed.
        String args = "a";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Call command, with args. Should throw JavaCommanderException.
        args = "a -nonsense 5";
        try
        {
            jc.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (JavaCommanderException ex)
        {
            // Ignore, as this is what should happen.
        }

        // Unregister object
        testUnregisterObject();
    }

    /**
     * Single-argument command, no default value
     */
    @Test
    public void testSingleArgumentWithNoDefault() throws Exception
    {
        System.out.println("execute, single-argument command, no default value");

        fail("The test case is a prototype.");
    }

    /**
     * Single-argument command, default value
     */
    @Test
    public void testSingleArgumentWithDefault() throws Exception
    {
        System.out.println("execute, single-argument command, default value");

        fail("The test case is a prototype.");
    }

    /**
     * Multi-argument command, no default values
     */
    @Test
    public void testMultiArgumentNoDefaults() throws Exception
    {
        System.out.println("execute, multi-arguments command, no default values");

        fail("The test case is a prototype.");
    }

    /**
     * Multi-argument command, some default values
     */
    @Test
    public void testMultiArgumentSomeDefaults() throws Exception
    {
        System.out.println("execute, multi-arguments command, some default values");

        fail("The test case is a prototype.");
    }

    /**
     * Multi-argument command, all default values
     */
    @Test
    public void testMultiArgumentAllDefaults() throws Exception
    {
        System.out.println("execute, multi-arguments command, all default values");

        fail("The test case is a prototype.");
    }
}
