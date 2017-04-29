package com.github.agadar.javacommander;

import com.github.agadar.javacommander.translator.NoTranslator;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;

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
        jcOptions.add(new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class, "defaultValue", NoTranslator.class));
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
        jcOptions.add(new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class, "defaultValue", NoTranslator.class));
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, methodToInvokeOn, objectToInvokeOn);
        assertTrue(jcCommand.hasOption("one"));

        // test true with synonym.
        jcOptions = new ArrayList<>();
        jcOptions.add(new JcOption<>(Arrays.asList("one", "two", "three"), "description", false, String.class, "defaultValue", NoTranslator.class));
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
        final JcCommand instance = new JcCommand(new String[]{"one", "two", "three"}, "description", null, methodToInvokeOn, objectToInvokeOn);

        // Check lower bounding.
        assertEquals("one", instance.getNameByIndex(-1));

        // Check upper bounding.
        assertEquals("three", instance.getNameByIndex(3));

        // Check within bounding.
        assertEquals("two", instance.getNameByIndex(1));
    }

    /**
     * Test of numberOfNames method, of class JcCommand.
     */
    @Test
    public void testNumberOfNames() {
        System.out.println("numberOfNames");
        final JcCommand instance = new JcCommand(new String[]{"one", "two", "three"}, "description", null, methodToInvokeOn, objectToInvokeOn);
        assertEquals(3, instance.numberOfNames());
    }

    /**
     * Test of getOptionByIndex method, of class JcCommand.
     */
    @Test
    public void testGetOptionByIndex() {
        System.out.println("getOptionByIndex");

        final JcOption jcOption1 = new JcOption<>(Arrays.asList("one"), "description", false, String.class, "defaultValue", NoTranslator.class);
        final JcOption jcOption2 = new JcOption<>(Arrays.asList("two"), "description", false, String.class, "defaultValue", NoTranslator.class);
        final JcOption jcOption3 = new JcOption<>(Arrays.asList("three"), "description", false, String.class, "defaultValue", NoTranslator.class);

        final ArrayList<JcOption> jcOptions = new ArrayList<>();
        jcOptions.add(jcOption1);
        jcOptions.add(jcOption2);
        jcOptions.add(jcOption3);

        final JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, methodToInvokeOn, objectToInvokeOn);

        // Check lower bounding.
        assertSame(jcOption1, jcCommand.getOptionByIndex(-1).get());

        // Check upper bounding.
        assertSame(jcOption3, jcCommand.getOptionByIndex(3).get());

        // Check within bounding.
        assertSame(jcOption2, jcCommand.getOptionByIndex(1).get());
    }

    /**
     * Test of getOptionByName method, of class JcCommand.
     */
    @Test
    public void testGetOptionByName() {
        System.out.println("getOptionByName");

        final JcOption jcOption1 = new JcOption<>(Arrays.asList("one"), "description", false, String.class, "defaultValue", NoTranslator.class);
        final JcOption jcOption2 = new JcOption<>(Arrays.asList("two"), "description", false, String.class, "defaultValue", NoTranslator.class);
        final JcOption jcOption3 = new JcOption<>(Arrays.asList("three"), "description", false, String.class, "defaultValue", NoTranslator.class);

        final ArrayList<JcOption> jcOptions = new ArrayList<>();
        jcOptions.add(jcOption1);
        jcOptions.add(jcOption2);
        jcOptions.add(jcOption3);

        final JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, methodToInvokeOn, objectToInvokeOn);

        // Test one that exists.
        assertSame(jcOption2, jcCommand.getOptionByName("two").get());

        // Test one that doesn't exist.
        assertFalse(jcCommand.getOptionByName("four").isPresent());
    }

    /**
     * Test of numberOfOptions method, of class JcCommand.
     */
    @Test
    public void testNumberOfOptions() {
        System.out.println("numberOfOptions");

        final JcOption jcOption1 = new JcOption<>(Arrays.asList("one"), "description", false, String.class, "defaultValue", NoTranslator.class);
        final JcOption jcOption2 = new JcOption<>(Arrays.asList("two"), "description", false, String.class, "defaultValue", NoTranslator.class);
        final JcOption jcOption3 = new JcOption<>(Arrays.asList("three"), "description", false, String.class, "defaultValue", NoTranslator.class);

        final ArrayList<JcOption> jcOptions = new ArrayList<>();
        jcOptions.add(jcOption1);
        jcOptions.add(jcOption2);
        jcOptions.add(jcOption3);

        final JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, methodToInvokeOn, objectToInvokeOn);
        assertEquals(3, jcCommand.numberOfOptions());
    }

    /**
     * Test of indexOfOption method, of class JcCommand.
     */
    @Test
    public void testIndexOfOption() {
        System.out.println("indexOfOption");

    }

    /**
     * Test of hashCode method, of class JcCommand.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");

    }

    /**
     * Test of equals method, of class JcCommand.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");

    }

    /**
     * Test of invoke method, of class JcCommand.
     */
    @Test
    public void testInvoke() {
        System.out.println("invoke");

    }

    /**
     * Test of isMyObject method, of class JcCommand.
     */
    @Test
    public void testIsMyObject() {
        System.out.println("isMyObject");

    }
}
