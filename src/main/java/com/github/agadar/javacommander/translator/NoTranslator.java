package com.github.agadar.javacommander.translator;

/**
 * Default translator that exists solely because Java annotations don't allow
 * null values. It is never used, but if it is, it only returns the exact
 * String that was supplied.
 * 
 * @author Agadar
 */
public class NoTranslator implements OptionTranslator<String>
{
    
    @Override
    public String translateString(String s)
    {
        return s;
    }
}
