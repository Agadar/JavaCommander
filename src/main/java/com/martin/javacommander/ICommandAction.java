package com.martin.javacommander;

import java.util.Map;

/**
 * Functional interface for command actions.
 *
 * @author Martin
 */
@FunctionalInterface
public interface ICommandAction
{

    /**
     * Executes the command action.
     *
     * @param options a collection of command options mapped to command option
     * names
     * @return any result or feedback represented in a string
     */
    public String execute(Map<String, CommandOption> options);
}
