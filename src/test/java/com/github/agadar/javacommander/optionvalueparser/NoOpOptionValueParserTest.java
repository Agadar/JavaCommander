package com.github.agadar.javacommander.optionvalueparser;

import org.junit.Test;

import com.github.agadar.javacommander.optionvalueparser.NoOpOptionValueParser;

import static org.junit.Assert.*;

/**
 * Tests
 * com.github.agadar.javacommander.optionvalueparser.NoOpOptionValueParser.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class NoOpOptionValueParserTest {

    /**
     * Test of parse method, of class NoOpOptionValueParser.
     */
    @Test
    public void testParse() {
        System.out.println("parse");
        assertEquals("testString", new NoOpOptionValueParser().parse("testString"));
    }
}
