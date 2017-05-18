package com.github.agadar.javacommander.exception;

/**
 * Parent class for all custom exceptions thrown by JavaCommander.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class JavaCommanderException extends Exception {

    /**
     * Constructor.
     */
    public JavaCommanderException() {
    }

    /**
     * Constructor.
     * @param message An informative message.
     */
    public JavaCommanderException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message An informative message.
     * @param cause The underlying exception.
     */
    public JavaCommanderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * @param cause The underlying exception.
     */
    public JavaCommanderException(Throwable cause) {
        super(cause);
    }
}
