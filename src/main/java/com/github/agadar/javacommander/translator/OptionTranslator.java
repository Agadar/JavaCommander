package com.github.agadar.javacommander.translator;

/**
 * Interface that can be used to translate the user's String input for a command
 * Option to the type of the method parameter it is to be passed to.
 *
 * @author Agadar
 * @param <T> the type to convert to
 */
public interface OptionTranslator<T> {

    /**
     * Translates the given string to a value of type T.
     *
     * @param s the string to translate
     * @return a value of type T
     */
    public abstract T translateString(String s);

    /**
     * Attempts to parse the given string to the supplied type. If the type is
     * a primitive or a boxed primitive, then the valueOf(...) method of the
     * given type is used. If the type is anything else, then the cast(...)
     * method of the type is used as a last desperate attempt to parse the string.
     *
     * @param <V> the type to parse to
     * @param s the string to parse
     * @param type the type to parse to
     * @return the parsed string
     */
    public static <V> V parseToPrimitive(String s, Class<V> type) 
            throws NumberFormatException, IndexOutOfBoundsException, ClassCastException {
        if (type.equals(String.class)) {
            return (V) s;
        }

        if (type.equals(Integer.class) || type.equals(int.class)) {
            return (V) Integer.valueOf(s);
        }

        if (type.equals(Short.class) || type.equals(short.class)) {
            return (V) Short.valueOf(s);
        }

        if (type.equals(Long.class) || type.equals(long.class)) {
            return (V) Long.valueOf(s);
        }

        if (type.equals(Byte.class) || type.equals(byte.class)) {
            return (V) Byte.valueOf(s);
        }

        if (type.equals(Float.class) || type.equals(float.class)) {
            return (V) Float.valueOf(s);
        }

        if (type.equals(Double.class) || type.equals(double.class)) {
            return (V) Double.valueOf(s);
        }

        if (type.equals(Character.class) || type.equals(char.class)) {
            return (V) Character.valueOf(s.charAt(0));
        }

        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return (V) Boolean.valueOf(s);
        }
        return type.cast(s);
    }

}
