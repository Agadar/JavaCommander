package com.github.agadar.javacommander.exception;

/**
 * Thrown when an underlying exception caused a command invocation to fail.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class CommandInvocationException extends Exception {

    /**
     * The name of the command.
     */
    public final String commandName;

    public CommandInvocationException(String commandName, Throwable cause) {
        super(String.format("Failed to invoke command '%s'", commandName), cause);
        this.commandName = commandName;
    }
}
