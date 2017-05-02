package com.github.agadar.javacommander.exception;

/**
 * Thrown when a command was not found.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class UnknownCommandException extends JavaCommanderException {

    /**
     * The name of the command that was not found.
     */
    public final String commandName;

    /**
     * Constructor.
     *
     * @param commandName The name of the command that was not found.
     */
    public UnknownCommandException(String commandName) {
        super(String.format("Command '%s' was not found", commandName));
        this.commandName = commandName;
    }
}
