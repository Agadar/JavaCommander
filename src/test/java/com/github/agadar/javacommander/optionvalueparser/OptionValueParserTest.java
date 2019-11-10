package com.github.agadar.javacommander.optionvalueparser;

import org.junit.Test;

import com.github.agadar.javacommander.optionvalueparser.OptionValueParser;

import static org.junit.Assert.*;

/**
 * Tests com.github.agadar.javacommander.optionvalueparser.OptionValueParser.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class OptionValueParserTest {

    /**
     * Test of static parseString function, of interface OptionValueParser.
     */
    @Test
    public void testDefaultParse() {

        // String
        assertEquals("string", OptionValueParser.defaultParse("string", String.class));

        // int and Integer
        assertEquals(15, (int) OptionValueParser.defaultParse("15", int.class));
        assertEquals(-15, (int) OptionValueParser.defaultParse("-15", int.class));
        assertEquals(Integer.valueOf(15), OptionValueParser.defaultParse("15", Integer.class));
        assertEquals(Integer.valueOf(-15), OptionValueParser.defaultParse("-15", Integer.class));

        // short and Short
        assertEquals(15, (short) OptionValueParser.defaultParse("15", short.class));
        assertEquals(-15, (short) OptionValueParser.defaultParse("-15", short.class));
        assertEquals(Short.valueOf((short) 15), OptionValueParser.defaultParse("15", Short.class));
        assertEquals(Short.valueOf((short) -15), OptionValueParser.defaultParse("-15", Short.class));

        // long and Long
        assertEquals(15L, (long) OptionValueParser.defaultParse("15", long.class));
        assertEquals(-15L, (long) OptionValueParser.defaultParse("-15", long.class));
        assertEquals(Long.valueOf(15L), OptionValueParser.defaultParse("15", Long.class));
        assertEquals(Long.valueOf(-15L), OptionValueParser.defaultParse("-15", Long.class));

        // byte and Byte
        assertEquals(15, (byte) OptionValueParser.defaultParse("15", byte.class));
        assertEquals(-15, (byte) OptionValueParser.defaultParse("-15", byte.class));
        assertEquals(Byte.valueOf((byte) 15), OptionValueParser.defaultParse("15", Byte.class));
        assertEquals(Byte.valueOf((byte) -15), OptionValueParser.defaultParse("-15", Byte.class));

        // float and Float
        assertEquals(15F, (float) OptionValueParser.defaultParse("15F", float.class), 0.0f);
        assertEquals(-15F, (float) OptionValueParser.defaultParse("-15F", float.class), 0.0f);
        assertEquals(Float.valueOf(15F), OptionValueParser.defaultParse("15F", Float.class), 0.0f);
        assertEquals(Float.valueOf(-15F), OptionValueParser.defaultParse("-15F", Float.class), 0.0f);

        // double and Double
        assertEquals(15D, (double) OptionValueParser.defaultParse("15D", double.class), 0.0d);
        assertEquals(-15D, (double) OptionValueParser.defaultParse("-15D", double.class), 0.0d);
        assertEquals(Double.valueOf(15D), OptionValueParser.defaultParse("15D", Double.class), 0.0d);
        assertEquals(Double.valueOf(-15D), OptionValueParser.defaultParse("-15D", Double.class), 0.0d);

        // char and Character
        assertEquals('a', (char) OptionValueParser.defaultParse("a", char.class));
        assertEquals(Character.valueOf('a'), OptionValueParser.defaultParse("a", Character.class));

        // boolean and Boolean
        assertEquals(true, (boolean) OptionValueParser.defaultParse("true", boolean.class));
        assertEquals(Boolean.valueOf(true), OptionValueParser.defaultParse("true", Boolean.class));
    }
}
