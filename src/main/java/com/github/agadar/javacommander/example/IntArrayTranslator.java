package com.github.agadar.javacommander.example;

import com.github.agadar.javacommander.translator.OptionTranslator;

/**
 * Example implementation of OptionTranslator. Converts a string, assumed to
 * represent a comma-separated integer list, to an integer array. E.g. the
 * string "5,8,2" is parsed to the integer array [5, 8, 2].
 *
 * @author Agadar
 */
public class IntArrayTranslator implements OptionTranslator<int[]> {

    @Override
    public int[] translateString(String stringToParse) {
        String[] splitOnCommas = stringToParse.split(",");
        int[] value = new int[splitOnCommas.length];

        for (int i = 0; i < splitOnCommas.length; i++) {
            splitOnCommas[i] = splitOnCommas[i].trim();
            value[i] = OptionTranslator.parseString(splitOnCommas[i], int.class);
        }
        return value;
    }

}
