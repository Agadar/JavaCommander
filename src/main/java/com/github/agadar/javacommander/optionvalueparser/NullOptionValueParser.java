package com.github.agadar.javacommander.optionvalueparser;

/**
 * Null parser. Required because Java annotations don't allow null values. It
 * simply always returns null.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NullOptionValueParser extends OptionValueParser<Void> {

    @Override
    public Void parse(String stringToParse) {
        return null;
    }
}
