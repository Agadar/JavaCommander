package com.github.agadar.javacommander;

import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;
import com.github.agadar.javacommander.translator.IntArrayTranslator;

/**
 * Used by JavaCommanderTest for testing com.martin.javacommander.JavaCommander.
 * 
 * @author marti
 */
public class TestClass
{
    public static String RESULT;
    
    @Command()
    public void a()
    {
        RESULT = "a";
    }
    
    @Command(options = { @Option(hasDefaultValue = false) })
    public void b0(String arg0)
    {
        RESULT = arg0;
    }
    
    @Command(options = { @Option(hasDefaultValue = true, defaultValue = "default") })
    public void b1(String arg0)
    {
        RESULT = arg0;
    }
    
    @Command(options = { 
        @Option(hasDefaultValue = false),
        @Option(hasDefaultValue = false, translator = IntArrayTranslator.class)})
    public void c0(String arg0, int[] arg1)
    {
        RESULT = arg0 + arg1.length;
    }
    
    @Command(options = { 
    @Option(hasDefaultValue = true, defaultValue = "default"),
    @Option(hasDefaultValue = false, translator = IntArrayTranslator.class)})
    public void c1(String arg0, int[] arg1)
    {
        RESULT = arg0 + arg1.length;
    }
    
    @Command(options = { 
        @Option(hasDefaultValue = true, defaultValue = "default"),
        @Option(hasDefaultValue = true, defaultValue = "1,2,3", translator = IntArrayTranslator.class)})
    public void c2(String arg0, int[] arg1)
    {
        RESULT = arg0 + arg1.length;
    }
}
