package com.github.agadar.javacommander.translator;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests com.github.agadar.javacommander.translator.OptionTranslator.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class OptionTranslatorTest {

    /**
     * Test of static parseString function, of interface OptionTranslator.
     */
    @Test
    public void testParseString() {

        // String
        assertEquals("string", OptionTranslator.parseString("string", String.class));

        // int and Integer
        assertEquals(15, (int) OptionTranslator.parseString("15", int.class));
        assertEquals(-15, (int) OptionTranslator.parseString("-15", int.class));
        assertEquals(new Integer(15), OptionTranslator.parseString("15", Integer.class));
        assertEquals(new Integer(-15), OptionTranslator.parseString("-15", Integer.class));

        // short and Short
        assertEquals(15, (short) OptionTranslator.parseString("15", short.class));
        assertEquals(-15, (short) OptionTranslator.parseString("-15", short.class));
        assertEquals(new Short((short) 15), OptionTranslator.parseString("15", Short.class));
        assertEquals(new Short((short) -15), OptionTranslator.parseString("-15", Short.class));

        // long and Long
        assertEquals(15L, (long) OptionTranslator.parseString("15", long.class));
        assertEquals(-15L, (long) OptionTranslator.parseString("-15", long.class));
        assertEquals(new Long(15L), OptionTranslator.parseString("15", Long.class));
        assertEquals(new Long(-15L), OptionTranslator.parseString("-15", Long.class));

        // byte and Byte
        assertEquals(15, (byte) OptionTranslator.parseString("15", byte.class));
        assertEquals(-15, (byte) OptionTranslator.parseString("-15", byte.class));
        assertEquals(new Byte((byte) 15), OptionTranslator.parseString("15", Byte.class));
        assertEquals(new Byte((byte) -15), OptionTranslator.parseString("-15", Byte.class));

        // float and Float
        assertEquals(15F, (float) OptionTranslator.parseString("15F", float.class), 0.0f);
        assertEquals(-15F, (float) OptionTranslator.parseString("-15F", float.class), 0.0f);
        assertEquals(new Float(15F), OptionTranslator.parseString("15F", Float.class), 0.0f);
        assertEquals(new Float(-15F), OptionTranslator.parseString("-15F", Float.class), 0.0f);

        // double and Double
        assertEquals(15D, (double) OptionTranslator.parseString("15D", double.class), 0.0d);
        assertEquals(-15D, (double) OptionTranslator.parseString("-15D", double.class), 0.0d);
        assertEquals(new Double(15D), OptionTranslator.parseString("15D", Double.class), 0.0d);
        assertEquals(new Double(-15D), OptionTranslator.parseString("-15D", Double.class), 0.0d);

        // char and Character
        assertEquals('a', (char) OptionTranslator.parseString("a", char.class));
        assertEquals(new Character('a'), OptionTranslator.parseString("a", Character.class));

        // boolean and Boolean
        assertEquals(true, (boolean) OptionTranslator.parseString("true", boolean.class));
        assertEquals(new Boolean(true), OptionTranslator.parseString("true", Boolean.class));
    }
}
