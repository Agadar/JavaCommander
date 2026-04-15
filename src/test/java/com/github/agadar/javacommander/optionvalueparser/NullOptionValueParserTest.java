package com.github.agadar.javacommander.optionvalueparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;


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
        assertNull(new NullOptionValueParser().parse("testString"));
    }
}
