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
     * @throws com.martin.javacommander.JavaCommanderException
     */
    @Test
    public void testSingleArgumentWithNoDefault() throws JavaCommanderException
    {
        // Register object
        testRegisterObject();

        // Call command, no arg. Should fail.
        String args = "b0";
        try
        {
            jc.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (JavaCommanderException ex)
        {
            // Ignore, as this is what should happen.
        }

        // Call command, with explicit arg. Should succeed.
        args = "b0 arg0 'somevalue'";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }
        
        // Call command, with implicit arg. Should succeed.
        args = "b0 'somevalue'";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Unregister object
        testUnregisterObject();
    }

    /**
     * Single-argument command, default value
     * @throws com.martin.javacommander.JavaCommanderException
     */
    @Test
    public void testSingleArgumentWithDefault() throws JavaCommanderException
    {
        // Register object
        testRegisterObject();

        // Call command, no arg. Should succeed.
        String args = "b1";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }
        
        // Call command, with arg. Should succeed.
        args = "b1 'somevalue'";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }
        
        // Unregister object
        testUnregisterObject();
    }

    /**
     * Multi-argument command, no default values
     * @throws com.martin.javacommander.JavaCommanderException
     */
    @Test
    public void testMultiArgumentNoDefaults() throws JavaCommanderException
    {
        // Register object
        testRegisterObject();
        
        // Call command, too few arguments, explicit options. Should fail.
        String args = "c0 arg0 'somevalue'";
        try
        {
            jc.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (JavaCommanderException ex)
        {
            // Ignore, as this is what should happen.
        }
        
        // Call command, too few arguments, implicit options. Should fail.
        args = "c0 'somevalue'";
        try
        {
            jc.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (JavaCommanderException ex)
        {
            // Ignore, as this is what should happen.
        }
        
        // Call command, too many arguments, explicit options. Should fail.
        args = "c0 arg0 'somevalue' arg1 '4,5,6' arg2 5";
        try
        {
            jc.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (JavaCommanderException ex)
        {
            // Ignore, as this is what should happen.
        }
        
        // Call command, too many arguments, implicit options. Should fail.
        args = "c0 'somevalue' '4,5,6' 5";
        try
        {
            jc.execute(args);
            fail("Input '" + args + "' succeeded even though it shouldn't have!");
        } catch (JavaCommanderException ex)
        {
            // Ignore, as this is what should happen.
        }
        
        // Call command with explicit options. Should succeed.
        args = "c0 arg1 '4,5,6' arg0 'somevalue'";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }
        
        // Call command with implicit options. Should succeed.
        args = "c0 'somevalue' '4,5,6'";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Unregister object
        testUnregisterObject();
    }

    /**
     * Multi-argument command, some default values
     * @throws com.martin.javacommander.JavaCommanderException
     */
    @Test
    public void testMultiArgumentSomeDefaults() throws JavaCommanderException
    {
        // Register object
        testRegisterObject();
        
        // Skipping default values, should succeed.
        String args = "c1 arg1 '4,5,6'";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }
        
        // Filling in all values, should succeed.
        args = "c1 arg1 '4,5,6' arg0 'somevalue'";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }
        
        // Unregister object
        testUnregisterObject();
    }

    /**
     * Multi-argument command, all default values
     * @throws com.martin.javacommander.JavaCommanderException
     */
    @Test
    public void testMultiArgumentAllDefaults() throws JavaCommanderException
    {
        // Register object
        testRegisterObject();
        
        // Skipping default values, should succeed.
        String args = "c2";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }
        
        // Filling in all values, should succeed.
        args = "c2 arg1 '4,5,6' arg0 'somevalue'";
        try
        {
            jc.execute(args);
        } catch (JavaCommanderException ex)
        {
            fail("Input '" + args + "' failed even though it shouldn't have! Exception message: " + ex.getMessage());
        }

        // Unregister object
        testUnregisterObject();
    }
}
