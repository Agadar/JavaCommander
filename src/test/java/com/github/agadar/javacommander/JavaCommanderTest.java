package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.JavaCommanderException;
import com.github.agadar.javacommander.testclass.AnnotatedClass;

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
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test call.
        jcCommander.execute(Arrays.asList("bar"));
    }

    /**
     * Test of execute method, of class JavaCommander testing the
     * 'barWithParams' method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_List_barWithParams() throws JavaCommanderException {
        System.out.println("execute_List_barWithParams");

        // Setup JavaCommander instance.
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test implicit calls.
        jcCommander.execute(Arrays.asList("BarWithParams", "someString", "15", "true"));
        jcCommander.execute(Arrays.asList("barWithParams", "someString", "15", "true"));

        // Test explicit calls, in order.
        jcCommander.execute(Arrays.asList("BarWithParams", "StringParam", "someString", "IntParam", "15", "BoolParam", "true"));
        jcCommander.execute(Arrays.asList("barWithParams", "StringParam", "someString", "IntParam", "15", "BoolParam", "true"));

        // Test explicit calls, out of order.
        jcCommander.execute(Arrays.asList("BarWithParams", "IntParam", "15", "BoolParam", "true", "StringParam", "someString"));
        jcCommander.execute(Arrays.asList("barWithParams", "IntParam", "15", "BoolParam", "true", "StringParam", "someString"));
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
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test implicit calls, leaving out parameters (expecting default values).
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "someString", "15", "true"));
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "someString", "15"));
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "someString"));
        jcCommander.execute(Arrays.asList("barWithDefaultParams"));

        // Test explicit calls, in order, leaving out parameters (expecting default values).
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "StringDefaultParam", "someString", "IntDefaultParam", "15", "BoolDefaultParam", "true"));
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "StringDefaultParam", "someString", "IntDefaultParam", "15"));
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "StringDefaultParam", "someString"));

        // Test explicit calls, out of order, leaving out parameters (expecting default values).
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "IntDefaultParam", "15", "BoolDefaultParam", "true", "StringDefaultParam", "someString"));
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "IntDefaultParam", "15", "BoolDefaultParam", "true"));
        jcCommander.execute(Arrays.asList("barWithDefaultParams", "IntDefaultParam", "15"));
    }

    /**
     * Test of execute method, of class JavaCommander testing the
     * 'barWithBazParam' method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_List_barWithBazParam() throws JavaCommanderException {
        System.out.println("execute_List_barWithBazParam");

        // Setup JavaCommander instance.
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test calls.
        jcCommander.execute(Arrays.asList("barWithBazParam", "someString"));
        jcCommander.execute(Arrays.asList("barWithBazParam"));
        jcCommander.execute(Arrays.asList("barWithBazParam", "BazParam", "someString"));
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
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test calls.
        jcCommander.execute(Arrays.asList("barNameless", "someString"));
        jcCommander.execute(Arrays.asList("barNameless", "arg0", "someString"));
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
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerClass(AnnotatedClass.class);

        // Test call.
        jcCommander.execute(Arrays.asList("barStatic"));
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
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test call.
        jcCommander.execute("bar");
    }

    /**
     * Test of execute method, of class JavaCommander testing the
     * 'barWithParams' method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_StringbarWithParams() throws JavaCommanderException {
        System.out.println("execute_String_barWithParams");

        // Setup JavaCommander instance.
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test implicit calls.
        jcCommander.execute("BarWithParams someString 15 true");
        jcCommander.execute("barWithParams someString 15 true");

        // Test explicit calls, in order.
        jcCommander.execute("BarWithParams StringParam someString IntParam 15 BoolParam true");
        jcCommander.execute("barWithParams StringParam someString IntParam 15 BoolParam true");

        // Test explicit calls, out of order.
        jcCommander.execute("BarWithParams IntParam 15 BoolParam true StringParam someString");
        jcCommander.execute("barWithParams IntParam 15 BoolParam true StringParam someString");
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
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test implicit calls, leaving out parameters (expecting default values).
        jcCommander.execute("barWithDefaultParams someString 15 true");
        jcCommander.execute("barWithDefaultParams someString 15");
        jcCommander.execute("barWithDefaultParams someString");
        jcCommander.execute("barWithDefaultParams");

        // Test explicit calls, in order, leaving out parameters (expecting default values).
        jcCommander.execute("barWithDefaultParams StringDefaultParam someString IntDefaultParam 15 BoolDefaultParam true");
        jcCommander.execute("barWithDefaultParams StringDefaultParam someString IntDefaultParam 15");
        jcCommander.execute("barWithDefaultParams StringDefaultParam someString");

        // Test explicit calls, out of order, leaving out parameters (expecting default values).
        jcCommander.execute("barWithDefaultParams IntDefaultParam 15 BoolDefaultParam true StringDefaultParam someString");
        jcCommander.execute("barWithDefaultParams IntDefaultParam 15 BoolDefaultParam true");
        jcCommander.execute("barWithDefaultParams ntDefaultParam 15");
    }

    /**
     * Test of execute method, of class JavaCommander testing the
     * 'barWithBazParam' method.
     *
     * @throws com.github.agadar.javacommander.exception.JavaCommanderException
     */
    @Test
    public void testExecute_String_barWithBazParam() throws JavaCommanderException {
        System.out.println("execute_String_barWithBazParam");

        // Setup JavaCommander instance.
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test calls.
        jcCommander.execute("barWithBazParam someString");
        jcCommander.execute("barWithBazParam");
        jcCommander.execute("barWithBazParam BazParam someString");
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
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test calls.
        jcCommander.execute("barNameless someString");
        jcCommander.execute("barNameless arg0 someString");
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
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerClass(AnnotatedClass.class);

        // Test call.
        jcCommander.execute("barStatic");
    }

    /**
     * Test of execute method, of class JavaCommander testing multiple commands
     * in one execute call.
     *
     * @throws JavaCommanderException
     */
    @Test
    public void testExecute_String_multiple_commands() throws JavaCommanderException {
        System.out.println("execute_String_multiple_commands");

        // Setup JavaCommander instance.
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test calls.
        jcCommander.execute("barWithParams someString0 15 true; barWithParams someString1 10 false");
        jcCommander.execute("barWithParams someString0 15 true;barWithParams someString1 10 false");
        jcCommander.execute("barWithParams someString0 15 true ;barWithParams someString1 10 false");
        jcCommander.execute("barWithParams someString0 15 true ; barWithParams someString1 10 false");
        jcCommander.execute("barWithParams someString0 15 true; barWithParams someString1 10 false;");
        jcCommander.execute(";barWithParams someString0 15 true; barWithParams someString1 10 false");
        jcCommander.execute(";barWithParams someString0 15 true; barWithParams someString1 10 false;");
        jcCommander.execute(";;;barWithParams someString0 15 true;;;barWithParams someString1 10 false;;;");
    }
}
