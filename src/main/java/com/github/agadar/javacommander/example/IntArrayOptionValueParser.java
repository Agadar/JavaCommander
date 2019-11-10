package com.github.agadar.javacommander.example;

import com.github.agadar.javacommander.optionvalueparser.OptionValueParser;

/**
 * Example implementation of OptionValueParser. Converts a string, assumed to
 * represent a comma-separated integer list, to an integer array. E.g. the
 * string "5,8,2" is parsed to the integer array [5, 8, 2].
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class IntArrayOptionValueParser implements OptionValueParser<int[]> {

    @Override
    public int[] parse(String stringToParse) {
        String[] splitOnCommas = stringToParse.split(",");
        int[] value = new int[splitOnCommas.length];

        for (int i = 0; i < splitOnCommas.length; i++) {
            splitOnCommas[i] = splitOnCommas[i].trim();
            value[i] = OptionValueParser.defaultParse(splitOnCommas[i], int.class);
        }
        return value;
    }

}
