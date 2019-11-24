package com.github.agadar.javacommander.optionvalueparser;

import org.junit.Test;

import com.github.agadar.javacommander.optionvalueparser.NullOptionValueParser;

import static org.junit.Assert.*;

/**
 * Tests {@link NullOptionValueParser}.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NullOptionValueParserTest {

    /**
     * Test of parse method, of class NoOpOptionValueParser.
     */
    @Test
    public void testParse() {
        System.out.println("parse");
        assertEquals(null, new NullOptionValueParser().parse("testString"));
    }
}
