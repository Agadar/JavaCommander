package com.github.agadar.javacommander.exception;

import com.github.agadar.javacommander.JcCommand;

import lombok.Getter;

/**
 * Thrown when an underlying exception caused a command invocation to fail.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class CommandInvocationException extends JavaCommanderException {

    private static final long serialVersionUID = 1L;

    /**
     * The command that failed to be invoked.
     */
    @Getter
    private final JcCommand jcCommand;

    /**
     * Constructor.
     *
     * @param jcCommand The command that failed to be invoked.
     * @param cause     The exception that caused the failure.
     */
    public CommandInvocationException(JcCommand jcCommand, Throwable cause) {
        super(String.format("Failed to invoke command '%s'", jcCommand.getPrimaryName()), cause);
        this.jcCommand = jcCommand;
    }
}
