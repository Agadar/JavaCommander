package com.martin.javacommander;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The definition of a command.
 *
 * @author Martin
 */
public class Command implements ICommandAction, Comparable<Command>
{

    /**
     * action performed by this command
     */
    private final ICommandAction action;
    /**
     * options for this command
     */
    public final Map<String, CommandOption> Options;
    /**
     * name of this command, should be unique
     */
    public final String Name;
    /**
     * description of this command
     */
    public final String Description;

    /**
     * @param action action performed by this command
     * @param name name of this command, should be unique
     * @param description description of this command
     */
    public Command(String name, String description, ICommandAction action)
    {
        this.action = action;
        this.Name = name;
        this.Description = description;
        this.Options = new TreeMap<>();
    }

    /**
     * @param action action performed by this command
     * @param name name of this command, should be unique
     * @param description description of this command
     * @param options options for this command
     */
    public Command(String name, String description, List<CommandOption> options, ICommandAction action)
    {
        this(name, description, action);
        options.forEach((co) -> Options.put(co.Name, co));
    }

    /**
     * @param action action performed by this command
     * @param name name of this command, should be unique
     * @param description description of this command
     * @param options options for this command
     */
    private Command(String name, String description, Map<String, CommandOption> options, ICommandAction action)
    {
        this.action = action;
        this.Name = name;
        this.Description = description;
        this.Options = options;
    }

    /**
     * Finds the option with the given name.
     *
     * @param optionName name of the option to find
     * @return the found option, or null if none was found
     */
    public CommandOption findOption(String optionName)
    {
        return Options.get(optionName);
    }

    @Override
    public int compareTo(Command t)
    {
        return this.Name.compareTo(t.Name);
    }

    @Override
    public String execute(Map<String, CommandOption> options)
    {
        return this.action.execute(options);
    }

    /**
     * Returns a command that is identical to this command but with a different
     * name (supplied by the caller) and a description that says it's synonymous
     * with this command.
     *
     * @param commandName the name for the synonymous command
     * @return a command synonymous to this
     */
    public Command getSynonym(String commandName)
    {
        return new Command(commandName, String.format("Synonym for '%s'.", this.Name), this.Options, this.action);
    }
}
