package com.github.agadar.javacommander.exception;

/**
 * Thrown when an OptionTranslator failed to parse a string, or when the
 * translator itself failed to be instantiated.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class OptionTranslatorException extends JavaCommanderException {

    /**
     * Constructor.
     *
     * @param message A message elaborating what went wrong.
     * @param cause The underlying cause.
     */
    public OptionTranslatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
