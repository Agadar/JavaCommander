package com.github.agadar.javacommander.exception;

/**
 * Thrown when an option without a default value was not supplied a value.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NoValueForOptionException extends Exception {

    /**
     * The name of the command.
     */
    public final String commandName;

    /**
     * The name of the option that was not supplied a value.
     */
    public final String optionName;

    public NoValueForOptionException(String commandName, String optionName) {
        super(String.format("Option '%s' of command '%s' was not supplied a value", optionName, commandName));
        this.commandName = commandName;
        this.optionName = optionName;
    }
}
