package com.martin.javacommander.translators;

/**
 * Example implementation of OptionTranslator that converts a String to a
 * String array in such a way that "one,two,three" becomes an array with the
 * elements "one", "two" and "three".
 * 
 * @author Martin
 */
public class StringArrayTranslator implements OptionTranslator<String[]>
{
    @Override
    public String[] translateString(String s)
    {
        return s.split(",");
    }    
}
