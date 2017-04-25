package com.github.agadar.javacommander.translator;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class NoTranslatorTest {

    /**
     * Test of translateString method, of class NoTranslator.
     */
    @Test
    public void testTranslateString() {
        System.out.println("translateString");
        assertEquals("testString", new NoTranslator().translateString("testString"));
    }

}
