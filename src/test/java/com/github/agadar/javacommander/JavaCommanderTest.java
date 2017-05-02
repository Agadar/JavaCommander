package com.github.agadar.javacommander;

import com.github.agadar.javacommander.testclass.AnnotatedClass;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
        System.out.println("execute");
        // TODO: code.
    }

    /**
     * Test of execute method, of class JavaCommander.
     */
    @Test
    public void testExecute_List() {
        System.out.println("execute");   
        
        // Setup JavaCommander instance.
        final JavaCommander jcCommander = new JavaCommander();
        jcCommander.registerObject(foo);
        
        // Test proper input.
        jcCommander.execute(Arrays.asList("bar"));
    }

    /**
     * Test of registerObject method, of class JavaCommander.
     */
    @Test
    public void testRegisterObject() {
        System.out.println("registerObject");
        // Merely passes through to a JcRegistry instance.
    }

    /**
     * Test of unregisterObject method, of class JavaCommander.
     */
    @Test
    public void testUnregisterObject() {
        System.out.println("unregisterObject");
        // Merely passes through to a JcRegistry instance.
    }

}
