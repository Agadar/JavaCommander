package com.github.agadar.javacommander.exception;

/**
 * Thrown when an OptionValueParser failed to parse a string, or when the parser
 * itself failed to be instantiated.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class OptionValueParserException extends JavaCommanderException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param message A message elaborating what went wrong.
     * @param cause   The underlying cause.
     */
    public OptionValueParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
