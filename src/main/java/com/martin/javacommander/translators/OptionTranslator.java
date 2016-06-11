package com.martin.javacommander.translators;

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
     */
    public abstract T translateString(String s);

    /**
     * Attempts to parse the given string to the supplied class, assumed to be a
     * primitive type or a primitive wrapper type. If it is neither, then null
     * is returned, unless it is String, in which case the String is simply
     * returned. May throw native parsing exceptions.
     *
     * @param <V>         the type to parse to
     * @param s           the string to parse
     * @param wrapperType the type to parse to
     * @return the parsed string, or null
     */
    public static <V> V parseToPrimitive(String s, Class<V> wrapperType)
    {
        if (wrapperType.equals(String.class))
        {
            return (V) s;
        }

        if (wrapperType.equals(Integer.class) || wrapperType.equals(int.class))
        {
            return (V) Integer.valueOf(s);
        }

        if (wrapperType.equals(Short.class) || wrapperType.equals(short.class))
        {
            return (V) Short.valueOf(s);
        }

        if (wrapperType.equals(Long.class) || wrapperType.equals(long.class))
        {
            return (V) Long.valueOf(s);
        }

        if (wrapperType.equals(Byte.class) || wrapperType.equals(byte.class))
        {
            return (V) Byte.valueOf(s);
        }

        if (wrapperType.equals(Float.class) || wrapperType.equals(float.class))
        {
            return (V) Float.valueOf(s);
        }

        if (wrapperType.equals(Double.class) || wrapperType.equals(double.class))
        {
            return (V) Double.valueOf(s);
        }

        if (wrapperType.equals(Character.class) || wrapperType.equals(char.class))
        {
            return (V) Character.valueOf(s.charAt(0));
        }

        if (wrapperType.equals(Boolean.class) || wrapperType.equals(boolean.class))
        {
            return (V) Boolean.valueOf(s);
        }

        // If wrapperType is not a primitive or is void, then return null
        return null;
    }

}
