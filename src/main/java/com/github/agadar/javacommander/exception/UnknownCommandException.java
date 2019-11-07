package com.github.agadar.javacommander.exception;

import lombok.Getter;

/**
 * Thrown when a command was not found.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class UnknownCommandException extends JavaCommanderException {

    private static final long serialVersionUID = 1L;

    /**
     * The name of the command that was not found.
     */
    @Getter
    private final String commandName;

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
