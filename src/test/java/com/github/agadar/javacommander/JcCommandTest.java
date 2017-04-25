package com.github.agadar.javacommander;

import com.github.agadar.javacommander.translator.NoTranslator;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class JcCommandTest {

    /**
     * Test of getPrimaryName method, of class JcCommand.
     */
    @Test
    public void testGetPrimaryName() {
        System.out.println("getPrimaryName");
        final JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", new ArrayList<>(), null, null);
        assertEquals("one", jcCommand.getPrimaryName().get());
    }

    /**
     * Test of hasOptions method, of class JcCommand.
     */
    @Test
    public void testHasOptions() {
        System.out.println("hasOptions");

        // Test false.
        JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", new ArrayList<>(), null, null);
        assertFalse(jcCommand.hasOptions());

        // Test true.
        final ArrayList<JcOption> jcOptions = new ArrayList<>();
        jcOptions.add(new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", NoTranslator.class));
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, null, null);
        assertTrue(jcCommand.hasOptions());
    }

    /**
     * Test of hasSynonyms method, of class JcCommand.
     */
    @Test
    public void testHasSynonyms() {
        System.out.println("hasSynonyms");
        
        // Test false.
        JcCommand jcCommand = new JcCommand(new String[]{"one"}, "description", new ArrayList<>(), null, null);
        assertFalse(jcCommand.hasSynonyms());
        
        // Test true.
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", new ArrayList<>(), null, null);
        assertTrue(jcCommand.hasSynonyms());
    }

    /**
     * Test of hasDescription method, of class JcCommand.
     */
    @Test
    public void testHasDescription() {
        System.out.println("hasDescription");
        
        // Test false with null.
        JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, null, new ArrayList<>(), null, null);
        assertFalse(jcCommand.hasDescription());
        
        // Test false with empty.
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "", new ArrayList<>(), null, null);
        assertFalse(jcCommand.hasDescription());
        
        // Test true.
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", new ArrayList<>(), null, null);
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
        JcCommand jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, null, null);
        assertFalse(jcCommand.hasOption("one"));
        
        // Test true with primary name.
        jcOptions = new ArrayList<>();
        jcOptions.add(new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", NoTranslator.class));
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, null, null);
        assertTrue(jcCommand.hasOption("one"));
        
        // test true with synonym.
        jcOptions = new ArrayList<>();
        jcOptions.add(new JcOption<>(new String[]{"one", "two", "three"}, "description", false, String.class, "defaultValue", NoTranslator.class));
        jcCommand = new JcCommand(new String[]{"one", "two", "three"}, "description", jcOptions, null, null);
        assertTrue(jcCommand.hasOption("three"));
    }

}
