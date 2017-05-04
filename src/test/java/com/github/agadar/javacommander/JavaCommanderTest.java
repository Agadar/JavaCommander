package com.github.agadar.javacommander;

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

    private static AnnotatedClass foo;

    @BeforeClass
    public static void setUpClass() {
        foo = new AnnotatedClass();
    }

    /**
     * Test of execute method, of class JavaCommander.
     */
    @Test
    public void testExecute_String() {
        System.out.println("execute_String");
        // TODO: code.
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'bar' method.
     */
    @Test
    public void testExecute_List_bar() {
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
     */
    @Test
    public void testExecute_List_barWithParams() {
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
     */
    @Test
    public void testExecute_List_barWithDefaultParams() {
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
     */
    @Test
    public void testExecute_List_barWithBazParam() {
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
     */
    @Test
    public void testExecute_List_barNameless() {
        System.out.println("execute_List_barNameless");

        // Setup JavaCommander instance.
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test calls.
        jcCommander.execute(Arrays.asList("barNameless", "someString"));
        jcCommander.execute(Arrays.asList("barNameless", "arg0", "someString"));
    }

    /**
     * Test of execute method, of class JavaCommander testing the 'barEmptyName'
     * method.
     */
    @Test
    public void testExecute_List_barEmptyName() {
        System.out.println("execute_List_barEmptyName");

        // Setup JavaCommander instance.
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);

        // Test calls.
        jcCommander.execute(Arrays.asList("someString"));
        jcCommander.execute(Arrays.asList("arg0", "someString"));

        jcCommander.execute(Arrays.asList("", "someString"));
        jcCommander.execute(Arrays.asList("", "arg0", "someString"));
    }
}
