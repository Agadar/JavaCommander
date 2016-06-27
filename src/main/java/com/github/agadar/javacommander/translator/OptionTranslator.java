package com.github.agadar.javacommander.translator;

import com.github.agadar.javacommander.JavaCommanderException;

/**
 * Interface that can be used to translate the user's String input for a command
 * Option to the type of the method parameter it is to be passed to.
 *
 * @author Martin
 * @param <T> the type to convert to
 */
public interface OptionTranslator<T>
{
    /**
     * Translates the given string to a value of type T.
     *
     * @param s the string to translate
     * @return a value of type T
     * @throws com.github.agadar.javacommander.JavaCommanderException
     */
    public abstract T translateString(String s) throws JavaCommanderException;

    /**
     * Attempts to parse the given string to the supplied class. If the supplied
     * class is not a primitive type, a wrapper type for primitives, or
     * String, then a JavaCommanderException is thrown. May also throw other
     * types of exceptions if the parsing from String to primitive failed.
     *
     * @param <V>  the type to parse to
     * @param s    the string to parse
     * @param type the type to parse to
     * @return the parsed string
     * @throws com.github.agadar.javacommander.JavaCommanderException
     */
    public static <V> V parseToPrimitive(String s, Class<V> type) throws JavaCommanderException, 
                                                                         NumberFormatException,
                                                                         IndexOutOfBoundsException
    {
        if (type.equals(String.class))
        {
            return (V) s;
        }

        if (type.equals(Integer.class) || type.equals(int.class))
        {
            return (V) Integer.valueOf(s);
        }

        if (type.equals(Short.class) || type.equals(short.class))
        {
            return (V) Short.valueOf(s);
        }

        if (type.equals(Long.class) || type.equals(long.class))
        {
            return (V) Long.valueOf(s);
        }

        if (type.equals(Byte.class) || type.equals(byte.class))
        {
            return (V) Byte.valueOf(s);
        }

        if (type.equals(Float.class) || type.equals(float.class))
        {
            return (V) Float.valueOf(s);
        }

        if (type.equals(Double.class) || type.equals(double.class))
        {
            return (V) Double.valueOf(s);
        }

        if (type.equals(Character.class) || type.equals(char.class))
        {
            return (V) Character.valueOf(s.charAt(0));
        }

        if (type.equals(Boolean.class) || type.equals(boolean.class))
        {
            return (V) Boolean.valueOf(s);
        }

        throw new JavaCommanderException(
                "Could not convert String value: type '" + type.getSimpleName()
                + "' is not a primitive type, a primitive wrapper type, or String!");
    }

}
