package com.github.agadar.javacommander.translator;

/**
 * Default translator. Required because Java annotations don't allow null
 * values. It is never actually used, but if it is, then it simply returns the
 * string that was supplied.
 *
 * @author Agadar
 */
public class NoTranslator implements OptionTranslator<String> {

    @Override
    public String translateString(String stringToParse) {
        return stringToParse;
    }
}
