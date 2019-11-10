package com.github.agadar.javacommander.testclass;

import com.github.agadar.javacommander.optionvalueparser.OptionValueParser;

/**
 * Test parser for the DataClass test class.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class DataClassOptionValueParser implements OptionValueParser<DataClass> {

    @Override
    public DataClass parse(String stringToParse) {
        return new DataClass(stringToParse);
    }
}
