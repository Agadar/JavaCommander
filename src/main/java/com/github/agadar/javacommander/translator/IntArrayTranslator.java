package com.github.agadar.javacommander.translator;

import com.github.agadar.javacommander.JavaCommanderException;

/**
 * Example implementation of OptionTranslator that converts a String to an int
 * array in such a way that "1,2,3" becomes an array with the elements 1, 2 and
 * 3.
 *
 * @author Agadar
 */
public class IntArrayTranslator implements OptionTranslator<int[]> {

    @Override
    public int[] translateString(String s) throws JavaCommanderException {
        String[] split = s.split(",");
        int[] value = new int[split.length];

        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
            value[i] = OptionTranslator.parseToPrimitive(split[i], int.class);
        }

        return value;
    }

}
