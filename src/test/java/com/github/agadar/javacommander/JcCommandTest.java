package com.github.agadar.javacommander;

import com.github.agadar.javacommander.translator.NoTranslator;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Optional;
import org.junit.After;
import org.junit.AfterClass;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Tests com.github.agadar.javacommander.JcCommand.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class JcCommandTest {

    private static Foo objectToInvokeOn;
    private static Method methodToInvokeOn;

    @BeforeClass
    public static void BeforeClass() throws NoSuchMethodException {
        objectToInvokeOn = new Foo();
        methodToInvokeOn = Foo.class.getMethod("Bar");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of getPrimaryName method, of class JcCommand.
     */
    @Test
    public void testGetPrimaryName() {
        System.out.println("getPrimaryName");
        final JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", new ArrayList<>(), methodToInvokeOn, objectToInvokeOn);
        assertEquals("one", jcCommand.getPrimaryName());
    }

    /**
     * Test of hasOptions method, of class JcCommand.
     */
    @Test
    public void testHasOptions() {
        System.out.println("hasOptions");

        // Test false.
        JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", new ArrayList<>(), methodToInvokeOn, objectToInvokeOn);
        assertFalse(jcCommand.hasOptions());

        // Test true.
        final ArrayList<JcOption> jcOptions = new ArrayList<>();
        jcOptions.add(new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", NoTranslator.class));
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, methodToInvokeOn, objectToInvokeOn);
        assertTrue(jcCommand.hasOptions());
    }

    /**
     * Test of hasSynonyms method, of class JcCommand.
     */
    @Test
    public void testHasSynonyms() {
        System.out.println("hasSynonyms");

        // Test false.
        JcCommand jcCommand = new JcCommand(new String[]{"one"}, "description", new ArrayList<>(), methodToInvokeOn, objectToInvokeOn);
        assertFalse(jcCommand.hasSynonyms());

        // Test true.
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", new ArrayList<>(), methodToInvokeOn, objectToInvokeOn);
        assertTrue(jcCommand.hasSynonyms());
    }

    /**
     * Test of hasDescription method, of class JcCommand.
     */
    @Test
    public void testHasDescription() {
        System.out.println("hasDescription");

        // Test false with null.
        JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, null, new ArrayList<>(), methodToInvokeOn, objectToInvokeOn);
        assertFalse(jcCommand.hasDescription());

        // Test false with empty.
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "", new ArrayList<>(), methodToInvokeOn, objectToInvokeOn);
        assertFalse(jcCommand.hasDescription());

        // Test true.
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", new ArrayList<>(), methodToInvokeOn, objectToInvokeOn);
        assertTrue(jcCommand.hasDescription());
    }

    /**
     * Test of hasOption method, of class JcCommand.
     */
    @Test
    public void testHasOption() {
        System.out.println("hasOption");

        // Test false.
        ArrayList<JcOption> jcOptions = new ArrayList<>();
        JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, methodToInvokeOn, objectToInvokeOn);
        assertFalse(jcCommand.hasOption("one"));

        // Test true with primary name.
        jcOptions = new ArrayList<>();
        jcOptions.add(new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", NoTranslator.class));
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, methodToInvokeOn, objectToInvokeOn);
        assertTrue(jcCommand.hasOption("one"));

        // test true with synonym.
        jcOptions = new ArrayList<>();
        jcOptions.add(new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", NoTranslator.class));
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, methodToInvokeOn, objectToInvokeOn);
        assertTrue(jcCommand.hasOption("three"));
    }

    private static final class Foo {

        public void Bar() {
        }
    }

    /**
     * Test of getNameByIndex method, of class JcCommand.
     */
    @Test
    public void testGetNameByIndex() {
        System.out.println("getNameByIndex");
        int index = 0;
        JcCommand instance = null;
        String expResult = "";
        String result = instance.getNameByIndex(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of numberOfNames method, of class JcCommand.
     */
    @Test
    public void testNumberOfNames() {
        System.out.println("numberOfNames");
        JcCommand instance = null;
        int expResult = 0;
        int result = instance.numberOfNames();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOptionByIndex method, of class JcCommand.
     */
    @Test
    public void testGetOptionByIndex() {
        System.out.println("getOptionByIndex");
        int index = 0;
        JcCommand instance = null;
        Optional<JcOption> expResult = null;
        Optional<JcOption> result = instance.getOptionByIndex(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOptionByName method, of class JcCommand.
     */
    @Test
    public void testGetOptionByName() {
        System.out.println("getOptionByName");
        String optionName = "";
        JcCommand instance = null;
        Optional<JcOption> expResult = null;
        Optional<JcOption> result = instance.getOptionByName(optionName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of numberOfOptions method, of class JcCommand.
     */
    @Test
    public void testNumberOfOptions() {
        System.out.println("numberOfOptions");
        JcCommand instance = null;
        int expResult = 0;
        int result = instance.numberOfOptions();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of indexOfOption method, of class JcCommand.
     */
    @Test
    public void testIndexOfOption() {
        System.out.println("indexOfOption");
        JcOption option = null;
        JcCommand instance = null;
        int expResult = 0;
        int result = instance.indexOfOption(option);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class JcCommand.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        JcCommand instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class JcCommand.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        JcCommand instance = null;
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of invoke method, of class JcCommand.
     */
    @Test
    public void testInvoke() {
        System.out.println("invoke");
        Object[] args = null;
        JcCommand instance = null;
        instance.invoke(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isMyObject method, of class JcCommand.
     */
    @Test
    public void testIsMyObject() {
        System.out.println("isMyObject");
        Object object = null;
        JcCommand instance = null;
        boolean expResult = false;
        boolean result = instance.isMyObject(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
