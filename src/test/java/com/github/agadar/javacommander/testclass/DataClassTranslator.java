package com.github.agadar.javacommander.testclass;

import com.github.agadar.javacommander.translator.OptionTranslator;

/**
 * Test translator for the DataClass test class.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class DataClassTranslator implements OptionTranslator<DataClass> {

    @Override
    public DataClass translateString(String stringToParse) {
        return new DataClass(stringToParse);
    }
}
