package com.github.agadar.javacommander.exception;

import com.github.agadar.javacommander.JcCommand;
import com.github.agadar.javacommander.JcCommandOption;

import lombok.Getter;

/**
 * Thrown when an option without a default value was not supplied a value.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Getter
public class NoValueForOptionException extends JavaCommanderException {

    private static final long serialVersionUID = 1L;

    /**
     * The command of which an option was not supplied a value.
     */
    private final JcCommand jcCommand;

    /**
     * The option that was not supplied with a value.
     */
    private final JcCommandOption<?> jcCommandOption;

    /**
     * Constructor.
     *
     * @param jcCommand The command of which an option was not supplied a value.
     * @param jcCommandOption  The option that was not supplied with a value.
     */
    public NoValueForOptionException(JcCommand jcCommand, JcCommandOption<?> jcOption) {
        super(String.format("Option '%s' of command '%s' was not supplied a value", jcOption.getPrimaryName(),
                jcCommand.getPrimaryName()));
        this.jcCommand = jcCommand;
        this.jcCommandOption = jcOption;
    }
}
