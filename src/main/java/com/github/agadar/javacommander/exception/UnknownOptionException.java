package com.github.agadar.javacommander.exception;

import com.github.agadar.javacommander.JcCommand;

import lombok.Getter;

/**
 * Thrown when an option was supplied for a command that does not have that
 * option.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Getter
public class UnknownOptionException extends JavaCommanderException {

    private static final long serialVersionUID = 1L;

    /**
     * The command of which an option was supplied that it does not have.
     */
    private final JcCommand jcCommand;

    /**
     * The name of the option that is not valid for the command.
     */
    private final String optionName;

    /**
     * Constructor.
     *
     * @param jcCommand  The command of which an option was supplied that it does
     *                   not have.
     * @param optionName The name of the option that is not valid for the command.
     */
    public UnknownOptionException(JcCommand jcCommand, String optionName) {
        super(String.format("'%s' is not a valid option for command '%s'", optionName, jcCommand.getPrimaryName()));
        this.jcCommand = jcCommand;
        this.optionName = optionName;
    }
}
