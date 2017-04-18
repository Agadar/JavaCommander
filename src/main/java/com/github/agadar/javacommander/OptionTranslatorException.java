package com.github.agadar.javacommander;

/**
 * Thrown when an OptionTranslator failed to parse a string, or when the
 * translator itself failed to be instantiated.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class OptionTranslatorException extends Exception {

    public OptionTranslatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
