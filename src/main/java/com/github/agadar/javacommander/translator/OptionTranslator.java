package com.github.agadar.javacommander.translator;

/**
 * Interface that can be implemented to translate string inputs for a command
 * option to the type of the underlying method parameter.
 *
 * @author Agadar
 * @param <T> The type to parse strings to.
 */
public interface OptionTranslator<T> {

    /**
     * Translates the given string to a value of type T.
     *
     * @param stringToParse The string to parse.
     * @return The string parsed to a value of type T.
     */
    public T translateString(String stringToParse);

    /**
     * Parses a string to a type. If the type is a primitive or a boxed
     * primitive, then the valueOf(...) method of the type is used. If the type
     * is a string, then it is simply returned. If it is anything else, then the
     * cast(...) method of the type is used as a last desperate attempt to parse
     * the string.
     *
     * @param <V> The type to parse to.
     * @param stringToParse The string to parse.
     * @param type The type to parse to.
     * @return The string parsed to a value of type V.
     */
    public static <V> V parseString(String stringToParse, Class<V> type) throws NumberFormatException, IndexOutOfBoundsException, ClassCastException {
        if (type.equals(String.class)) {
            return (V) stringToParse;
        }

        if (type.equals(Integer.class) || type.equals(int.class)) {
            return (V) Integer.valueOf(stringToParse);
        }

        if (type.equals(Short.class) || type.equals(short.class)) {
            return (V) Short.valueOf(stringToParse);
        }

        if (type.equals(Long.class) || type.equals(long.class)) {
            return (V) Long.valueOf(stringToParse);
        }

        if (type.equals(Byte.class) || type.equals(byte.class)) {
            return (V) Byte.valueOf(stringToParse);
        }

        if (type.equals(Float.class) || type.equals(float.class)) {
            return (V) Float.valueOf(stringToParse);
        }

        if (type.equals(Double.class) || type.equals(double.class)) {
            return (V) Double.valueOf(stringToParse);
        }

        if (type.equals(Character.class) || type.equals(char.class)) {
            return (V) Character.valueOf(stringToParse.charAt(0));
        }

        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return (V) Boolean.valueOf(stringToParse);
        }
        return type.cast(stringToParse);
    }

}
