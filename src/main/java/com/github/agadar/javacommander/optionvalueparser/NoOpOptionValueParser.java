package com.github.agadar.javacommander.optionvalueparser;

/**
 * No-op parser. Required because Java annotations don't allow null values. It
 * is never actually used, but if it is, then it simply returns the string that
 * was supplied.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NoOpOptionValueParser implements OptionValueParser<String> {

    @Override
    public String parse(String stringToParse) {
        return stringToParse;
    }
}
