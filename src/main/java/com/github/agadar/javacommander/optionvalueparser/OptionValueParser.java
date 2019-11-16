package com.github.agadar.javacommander.optionvalueparser;

import com.github.agadar.javacommander.exception.OptionValueParserException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract parent class that can be implemented to parse string inputs for a
 * command option to the type of the underlying method parameter.
 *
 * @author Agadar (https://github.com/Agadar/)
 * @param <T> The type to parse strings to.
 */
@Slf4j
public abstract class OptionValueParser<T> {

    /**
     * Parses the given string to a value of type T.
     *
     * @param stringToParse The string to parse.
     * @return The string parsed to a value of type T.
     * @throws OptionValueParserException If parsing failed.
     */
    public abstract T parse(@NonNull String stringToParse) throws OptionValueParserException;

    /**
     * Parses a string to a type. If type is String, it simply returns the input. If
     * type is char or Character, it returns the first character in the input. If
     * type is Void, it returns null. For any other type, it tries to find a static
     * valueOf function and invoke it with the input.
     *
     * @param <T>           The type to parse to.
     * @param stringToParse The string to parse.
     * @param type          The type to parse to.
     * @return The string parsed to a value of type T.
     * @throws OptionValueParserException If parsing failed.
     */
    public static <T> T parseToType(@NonNull String stringToParse, @NonNull Class<T> type)
            throws OptionValueParserException {

        try {
            if (type.equals(Void.class)) {
                return null;
            }
            if (type.equals(String.class)) {
                return type.cast(stringToParse);
            }
            if (type.isPrimitive()) {
                type = boxPrimitive(type);
            }
            if (type.equals(Character.class)) {
                return type.cast(Character.valueOf(stringToParse.charAt(0)));
            }
            var valueOfMethod = type.getMethod("valueOf", String.class);
            return type.cast(valueOfMethod.invoke(null, stringToParse));

        } catch (Exception ex) {
            String errorMsg = String.format("Failed to parse String '%s' to type '%s'", stringToParse,
                    type.getSimpleName());
            log.error(errorMsg, ex);
            throw new OptionValueParserException(errorMsg, ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> boxPrimitive(Class<T> type) {
        if (type.equals(int.class)) {
            return (Class<T>) Integer.class;
        }
        if (type.equals(short.class)) {
            return (Class<T>) Short.class;
        }
        if (type.equals(long.class)) {
            return (Class<T>) Long.class;
        }
        if (type.equals(byte.class)) {
            return (Class<T>) Byte.class;
        }
        if (type.equals(float.class)) {
            return (Class<T>) Float.class;
        }
        if (type.equals(double.class)) {
            return (Class<T>) Double.class;
        }
        if (type.equals(boolean.class)) {
            return (Class<T>) Boolean.class;
        }
        if (type.equals(char.class)) {
            return (Class<T>) Character.class;
        }
        return type;
    }
}
