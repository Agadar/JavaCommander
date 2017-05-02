package com.github.agadar.javacommander.exception;

import com.github.agadar.javacommander.JcCommand;

/**
 * Thrown when an option was supplied for a command that does not have that
 * option.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class UnknownOptionException extends JavaCommanderException {

    /**
     * The command of which an option was supplied that it does not have.
     */
    public final JcCommand jcCommand;

    /**
     * The name of the option that is not valid for the command.
     */
    public final String optionName;

    /**
     * Constructor.
     *
     * @param jcCommand The command of which an option was supplied that it does
     * not have.
     * @param optionName The name of the option that is not valid for the
     * command.
     */
    public UnknownOptionException(JcCommand jcCommand, String optionName) {
        super(String.format("'%s' is not a valid option for command '%s'", optionName, jcCommand.getPrimaryName()));
        this.jcCommand = jcCommand;
        this.optionName = optionName;
    }
}
