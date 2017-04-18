package com.github.agadar.javacommander;

/**
 * Thrown when an option was supplied for a command that does not have that
 * option.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class UnknownOptionException extends Exception {

    /**
     * The name of the command.
     */
    public final String commandName;

    /**
     * The name of the option that is not valid for the command.
     */
    public final String optionName;

    public UnknownOptionException(String commandName, String optionName) {
        super(String.format("'%s' is not a valid option for command '%s'", optionName, commandName));
        this.commandName = commandName;
        this.optionName = optionName;
    }
}
