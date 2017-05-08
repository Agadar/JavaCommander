package com.github.agadar.javacommander.exception;

/**
 * Parent class for all custom exceptions thrown by JavaCommander.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class JavaCommanderException extends Exception {

    public JavaCommanderException() {
    }

    public JavaCommanderException(String message) {
        super(message);
    }

    public JavaCommanderException(String message, Throwable cause) {
        super(message, cause);
    }

    public JavaCommanderException(Throwable cause) {
        super(cause);
    }
}
