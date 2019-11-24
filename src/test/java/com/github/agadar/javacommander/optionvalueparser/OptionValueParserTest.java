package com.github.agadar.javacommander.optionvalueparser;

import org.junit.Test;

import com.github.agadar.javacommander.exception.OptionValueParserException;
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
     * 
     * @throws OptionValueParserException
     */
    @Test
    public void testDefaultParse() throws OptionValueParserException {

        // Void
        assertNull(OptionValueParser.parseToType("15", Void.class));

        // String
        assertEquals("string", OptionValueParser.parseToType("string", String.class));

        // int and Integer
        assertEquals(15, (int) OptionValueParser.parseToType("15", int.class));
        assertEquals(-15, (int) OptionValueParser.parseToType("-15", int.class));
        assertEquals(Integer.valueOf(15), OptionValueParser.parseToType("15", Integer.class));
        assertEquals(Integer.valueOf(-15), OptionValueParser.parseToType("-15", Integer.class));

        // short and Short
        assertEquals(15, (short) OptionValueParser.parseToType("15", short.class));
        assertEquals(-15, (short) OptionValueParser.parseToType("-15", short.class));
        assertEquals(Short.valueOf((short) 15), OptionValueParser.parseToType("15", Short.class));
        assertEquals(Short.valueOf((short) -15), OptionValueParser.parseToType("-15", Short.class));

        // long and Long
        assertEquals(15L, (long) OptionValueParser.parseToType("15", long.class));
        assertEquals(-15L, (long) OptionValueParser.parseToType("-15", long.class));
        assertEquals(Long.valueOf(15L), OptionValueParser.parseToType("15", Long.class));
        assertEquals(Long.valueOf(-15L), OptionValueParser.parseToType("-15", Long.class));

        // byte and Byte
        assertEquals(15, (byte) OptionValueParser.parseToType("15", byte.class));
        assertEquals(-15, (byte) OptionValueParser.parseToType("-15", byte.class));
        assertEquals(Byte.valueOf((byte) 15), OptionValueParser.parseToType("15", Byte.class));
        assertEquals(Byte.valueOf((byte) -15), OptionValueParser.parseToType("-15", Byte.class));

        // float and Float
        assertEquals(15F, (float) OptionValueParser.parseToType("15F", float.class), 0.0f);
        assertEquals(-15F, (float) OptionValueParser.parseToType("-15F", float.class), 0.0f);
        assertEquals(Float.valueOf(15F), OptionValueParser.parseToType("15F", Float.class), 0.0f);
        assertEquals(Float.valueOf(-15F), OptionValueParser.parseToType("-15F", Float.class), 0.0f);

        // double and Double
        assertEquals(15D, (double) OptionValueParser.parseToType("15D", double.class), 0.0d);
        assertEquals(-15D, (double) OptionValueParser.parseToType("-15D", double.class), 0.0d);
        assertEquals(Double.valueOf(15D), OptionValueParser.parseToType("15D", Double.class), 0.0d);
        assertEquals(Double.valueOf(-15D), OptionValueParser.parseToType("-15D", Double.class), 0.0d);

        // char and Character
        assertEquals('a', (char) OptionValueParser.parseToType("a", char.class));
        assertEquals(Character.valueOf('a'), OptionValueParser.parseToType("a", Character.class));

        // boolean and Boolean
        assertEquals(true, (boolean) OptionValueParser.parseToType("true", boolean.class));
        assertEquals(Boolean.valueOf(true), OptionValueParser.parseToType("true", Boolean.class));
    }
}
