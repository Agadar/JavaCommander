package com.github.agadar.javacommander.optionvalueparser;

import lombok.NonNull;

/**
 * Interface that can be implemented to parse string inputs for a command option
 * to the type of the underlying method parameter.
 *
 * @author Agadar (https://github.com/Agadar/)
 * @param <T> The type to parse strings to.
 */
public interface OptionValueParser<T> {

    /**
     * Parses the given string to a value of type T.
     *
     * @param stringToParse The string to parse.
     * @return The string parsed to a value of type T.
     */
    public T parse(@NonNull String stringToParse);

    /**
     * Parses a string to a type. If the type is a primitive or a boxed primitive,
     * then the valueOf(...) method of the type is used. If the type is a string,
     * then it is simply returned. If it is anything else, then the cast(...) method
     * of the type is used as a last desperate attempt to parse the string.
     *
     * @param <T>           The type to parse to.
     * @param stringToParse The string to parse.
     * @param type          The type to parse to.
     * @return The string parsed to a value of type V.
     */
    @SuppressWarnings("unchecked")
    public static <T> T defaultParse(@NonNull String stringToParse, @NonNull Class<T> type)
            throws NumberFormatException, IndexOutOfBoundsException, ClassCastException {

        if (type.equals(String.class)) {
            return (T) stringToParse;
        }

        if (type.equals(Integer.class) || type.equals(int.class)) {
            return (T) Integer.valueOf(stringToParse);
        }

        if (type.equals(Short.class) || type.equals(short.class)) {
            return (T) Short.valueOf(stringToParse);
        }

        if (type.equals(Long.class) || type.equals(long.class)) {
            return (T) Long.valueOf(stringToParse);
        }

        if (type.equals(Byte.class) || type.equals(byte.class)) {
            return (T) Byte.valueOf(stringToParse);
        }

        if (type.equals(Float.class) || type.equals(float.class)) {
            return (T) Float.valueOf(stringToParse);
        }

        if (type.equals(Double.class) || type.equals(double.class)) {
            return (T) Double.valueOf(stringToParse);
        }

        if (type.equals(Character.class) || type.equals(char.class)) {
            return (T) Character.valueOf(stringToParse.charAt(0));
        }

        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return (T) Boolean.valueOf(stringToParse);
        }
        return type.cast(stringToParse);
    }

}
