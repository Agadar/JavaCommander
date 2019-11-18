package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.JavaCommanderException;
import com.github.agadar.javacommander.testclass.AnnotatedClass;
import com.github.agadar.javacommander.testclass.DataClass;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests com.github.agadar.javacommander.JavaCommander.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class JavaCommanderTest {

    /**
     * Instance of the annotated test class.
     */
    private static AnnotatedClass foo;

    @BeforeClass
    public static void setUpClass() {
        foo = new AnnotatedClass();
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'bar' method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_List_bar() throws JavaCommanderException {
        System.out.println("execute_List_bar");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test call.
        jcCommander.execute(Arrays.asList("bar"));
        assertArguments();
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'barWithParams'
     * method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_List_barWithParams() throws JavaCommanderException {
        System.out.println("execute_List_barWithParams");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test implicit calls.
        jcCommander.execute(Arrays.asList("BarWithParams", "someString", "15", "true"));
        assertArguments("someString", 15, true);
        jcCommander.execute(Arrays.asList("barWithParams", "someString", "15", "true"));
        assertArguments("someString", 15, true);

        // Test explicit calls, in order.
        jcCommander.execute(
                Arrays.asList("BarWithParams", "StringParam", "someString", "IntParam", "15", "BoolParam", "true"));
        assertArguments("someString", 15, true);
        jcCommander.execute(
                Arrays.asList("barWithParams", "StringParam", "someString", "IntParam", "15", "BoolParam", "true"));
        assertArguments("someString", 15, true);

        // Test explicit calls, out of order.
        jcCommander.execute(
                Arrays.asList("BarWithParams", "IntParam", "15", "BoolParam", "true", "StringParam", "someString"));
        assertArguments("someString", 15, true);
        jcCommander.execute(
                Arrays.asList("barWithParams", "IntParam", "15", "BoolParam", "true", "StringParam", "someString"));
        assertArguments("someString", 15, true);
    }

    /**
     * Test of execute method, of class JavaCommander testing the
     * 'barWithDefaultParams' method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_List_barWithDefaultParams() throws JavaCommanderException {
        System.out.println("execute_List_barWithDefaultParams");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test implicit calls, leaving out parameters (expecting default values).
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "someString", "15", "true"));
        assertArguments("someString", 15, true);
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "someString", "15"));
        assertArguments("someString", 15, true);
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "someString"));
        assertArguments("someString", 15, true);
        jcCommander.execute(Arrays.asList("barWithDefaultParams"));
        assertArguments("defaultString", 15, true);

        // Test explicit calls, in order, leaving out parameters (expecting default
        // values).
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "StringDefaultParam", "someString", "IntDefaultParam",
                "15", "BoolDefaultParam", "true"));
        assertArguments("someString", 15, true);
        jcCommander.execute(
                Arrays.asList("barWithDefaultParams", "StringDefaultParam", "someString", "IntDefaultParam", "15"));
        assertArguments("someString", 15, true);
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "StringDefaultParam", "someString"));
        assertArguments("someString", 15, true);

        // Test explicit calls, out of order, leaving out parameters (expecting default
        // values).
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "IntDefaultParam", "15", "BoolDefaultParam", "true",
                "StringDefaultParam", "someString"));
        assertArguments("someString", 15, true);
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "IntDefaultParam", "15", "BoolDefaultParam", "true"));
        assertArguments("defaultString", 15, true);
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "IntDefaultParam", "15"));
        assertArguments("defaultString", 15, true);
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'barWithBazParam'
     * method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_List_barWithBazParam() throws JavaCommanderException {
        System.out.println("execute_List_barWithBazParam");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test calls.
        jcCommander.execute(Arrays.asList("barWithBazParam", "someString"));
        assertArguments(new DataClass("someString"));
        jcCommander.execute(Arrays.asList("barWithBazParam"));
        assertArguments(new DataClass("defaultBaz"));
        jcCommander.execute(Arrays.asList("barWithBazParam", "BazParam", "someString"));
        assertArguments(new DataClass("someString"));
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'barNameless'
     * method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_List_barNameless() throws JavaCommanderException {
        System.out.println("execute_List_barNameless");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test calls.
        jcCommander.execute(Arrays.asList("barNameless", "someString"));
        assertArguments("someString");
        jcCommander.execute(Arrays.asList("barNameless", "arg0", "someString"));
        assertArguments("someString");
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'barStatic'
     * method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_List_barStatic() throws JavaCommanderException {
        System.out.println("execute_List_barStatic");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromClass(AnnotatedClass.class);

        // Test call.
        jcCommander.execute(Arrays.asList("barStatic"));
        assertArguments();
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'bar' method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_String_bar() throws JavaCommanderException {
        System.out.println("execute_String_bar");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test call.
        jcCommander.execute("bar");
        assertArguments();
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'barWithParams'
     * method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_StringbarWithParams() throws JavaCommanderException {
        System.out.println("execute_String_barWithParams");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test implicit calls.
        jcCommander.execute("BarWithParams someString 15 true");
        assertArguments("someString", 15, true);
        jcCommander.execute("barWithParams someString 15 true");
        assertArguments("someString", 15, true);

        // Test explicit calls, in order.
        jcCommander.execute("BarWithParams StringParam someString IntParam 15 BoolParam true");
        assertArguments("someString", 15, true);
        jcCommander.execute("barWithParams StringParam someString IntParam 15 BoolParam true");
        assertArguments("someString", 15, true);

        // Test explicit calls, out of order.
        jcCommander.execute("BarWithParams IntParam 15 BoolParam true StringParam someString");
        assertArguments("someString", 15, true);
        jcCommander.execute("barWithParams IntParam 15 BoolParam true StringParam someString");
        assertArguments("someString", 15, true);
    }

    /**
     * Test of execute method, of class JavaCommander testing the
     * 'barWithDefaultParams' method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_String_barWithDefaultParams() throws JavaCommanderException {
        System.out.println("execute_StringbarWithDefaultParams");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test implicit calls, leaving out parameters (expecting default values).
        jcCommander.execute("barWithDefaultParams someString 15 true");
        assertArguments("someString", 15, true);
        jcCommander.execute("barWithDefaultParams someString 15");
        assertArguments("someString", 15, true);
        jcCommander.execute("barWithDefaultParams someString");
        assertArguments("someString", 15, true);
        jcCommander.execute("barWithDefaultParams");
        assertArguments("defaultString", 15, true);

        // Test explicit calls, in order, leaving out parameters (expecting default
        // values).
        jcCommander
                .execute("barWithDefaultParams StringDefaultParam someString IntDefaultParam 15 BoolDefaultParam true");
        assertArguments("someString", 15, true);
        jcCommander.execute("barWithDefaultParams StringDefaultParam someString IntDefaultParam 15");
        assertArguments("someString", 15, true);
        jcCommander.execute("barWithDefaultParams StringDefaultParam someString");
        assertArguments("someString", 15, true);

        // Test explicit calls, out of order, leaving out parameters (expecting default
        // values).
        jcCommander
                .execute("barWithDefaultParams IntDefaultParam 15 BoolDefaultParam true StringDefaultParam someString");
        assertArguments("someString", 15, true);
        jcCommander.execute("barWithDefaultParams IntDefaultParam 15 BoolDefaultParam true");
        assertArguments("defaultString", 15, true);
        jcCommander.execute("barWithDefaultParams IntDefaultParam 15");
        assertArguments("defaultString", 15, true);
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'barWithBazParam'
     * method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_String_barWithBazParam() throws JavaCommanderException {
        System.out.println("execute_String_barWithBazParam");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test calls.
        jcCommander.execute("barWithBazParam someString");
        assertArguments(new DataClass("someString"));
        jcCommander.execute("barWithBazParam");
        assertArguments(new DataClass("defaultBaz"));
        jcCommander.execute("barWithBazParam BazParam someString");
        assertArguments(new DataClass("someString"));
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'barNameless'
     * method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_String_barNameless() throws JavaCommanderException {
        System.out.println("execute_String_barNameless");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test calls.
        jcCommander.execute("barNameless someString");
        assertArguments("someString");
        jcCommander.execute("barNameless arg0 someString");
        assertArguments("someString");
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'barStatic'
     * method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_String_barStatic() throws JavaCommanderException {
        System.out.println("execute_String_barStatic");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromClass(AnnotatedClass.class);

        // Test call.
        jcCommander.execute("barStatic");
        assertArguments();
    }

    /**
     * Test of execute method, of class JavaCommander testing multiple commands in
     * one execute call.
     *
     * @throws JavaCommanderException
     */
    @Test
    public void testExecute_String_multiple_commands() throws JavaCommanderException {
        System.out.println("execute_String_multiple_commands");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test calls.
        jcCommander.execute("barWithParams someString0 15 true; barWithParams someString1 10 false");
        assertArguments("someString1", 10, false);
        jcCommander.execute("barWithParams someString0 15 true;barWithParams someString1 10 false");
        assertArguments("someString1", 10, false);
        jcCommander.execute("barWithParams someString0 15 true ;barWithParams someString1 10 false");
        assertArguments("someString1", 10, false);
        jcCommander.execute("barWithParams someString0 15 true ; barWithParams someString1 10 false");
        assertArguments("someString1", 10, false);
        jcCommander.execute("barWithParams someString0 15 true; barWithParams someString1 10 false;");
        assertArguments("someString1", 10, false);
        jcCommander.execute(";barWithParams someString0 15 true; barWithParams someString1 10 false");
        assertArguments("someString1", 10, false);
        jcCommander.execute(";barWithParams someString0 15 true; barWithParams someString1 10 false;");
        assertArguments("someString1", 10, false);
        jcCommander.execute(";;;barWithParams someString0 15 true;;;barWithParams someString1 10 false;;;");
        assertArguments("someString1", 10, false);
    }

    /**
     * Test of execute method, of class JavaCommander testing the usage of flag
     * options where the flag functionality is used during explicit parsing.
     * 
     * @throws JavaCommanderException
     */
    @Test
    public void testExecute_String_flags_explicit_flagged() throws JavaCommanderException {
        System.out.println("execute_String_flags_explicit_flagged");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test calls.
        jcCommander.execute("barWithFlags stringParam:'someString' flag1 flag2");
        assertArguments("someString", true, false);
    }

    /**
     * Test of execute method, of class JavaCommander testing the usage of flag
     * options where the flag functionality is NOT used during explicit parsing.
     * 
     * @throws JavaCommanderException
     */
    @Test
    public void testExecute_String_flags_explicit_not_flagged() throws JavaCommanderException {
        System.out.println("execute_String_flags_explicit_not_flagged");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test calls.
        jcCommander.execute("barWithFlags stringParam:'someString' flag1:true flag2:false");
        assertArguments("someString", true, false);
    }

    /**
     * Test of execute method, of class JavaCommander testing the usage of flag
     * options where the flag functionality is used during implicit parsing.
     * 
     * @throws JavaCommanderException
     */
    @Test
    public void testExecute_String_flags_implicit_flagged() throws JavaCommanderException {
        System.out.println("execute_String_flags_implicit_flagged");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test calls.
        jcCommander.execute("barWithFlags 'someString' flag1 flag2");
        assertArguments("someString", true, false);
    }

    /**
     * Test of execute method, of class JavaCommander testing the usage of flag
     * options where the flag functionality is NOT used during implicit parsing.
     * 
     * @throws JavaCommanderException
     */
    @Test
    public void testExecute_String_flags_implicit_not_flagged() throws JavaCommanderException {
        System.out.println("execute_String_flags_implicit_not_flagged");

        // Setup JavaCommander instance.
        var jcCommander = new JavaCommander();
        jcCommander.registerFromObject(foo);

        // Test calls.
        jcCommander.execute("barWithFlags 'someString' true false");
        assertArguments("someString", true, false);
    }

    private void assertArguments(Object... arguments) {
        assertArrayEquals(arguments, AnnotatedClass.getLatestArguments());
    }
}
