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
     * @param options
     * @return 
     */
    public String execute(Map<String, CommandOption> options);
}
