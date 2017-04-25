package com.github.agadar.javacommander.exception;

import com.github.agadar.javacommander.JcCommand;

/**
 * Thrown when an underlying exception caused a command invocation to fail.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class CommandInvocationException extends RuntimeException {

    /**
     * The command that failed to be invoked.
     */
    public final JcCommand jcCommand;

    /**
     * Constructor.
     *
     * @param jcCommand The command that failed to be invoked.
     * @param cause The exception that caused the failure.
     */
    public CommandInvocationException(JcCommand jcCommand, Throwable cause) {
        super(String.format("Failed to invoke command '%s'", jcCommand.names[0]), cause);
        this.jcCommand = jcCommand;
    }
}
