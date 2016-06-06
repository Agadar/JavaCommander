package com.martin.javacommander;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A command with an action.
 *
 * @author Martin
 */
public class Command implements ICommandAction, Comparable<Command>
{
    private final ICommandAction action;
    public final Map<String, CommandOption> Options;
    public final String Name;
    public final String Description;

    public Command(ICommandAction action, String name, String description)
    {
        this.action = action;
        this.Name = name;
        this.Description = description;
        this.Options = new TreeMap<>();
    }
    
    public Command(ICommandAction action, String name, String description, List<CommandOption> options)
    {
        this(action, name, description);
        
        if (options != null)
            options.forEach((co) -> Options.put(co.Name, co));
    }
    
    private Command(ICommandAction action, String name, String description, Map<String, CommandOption> options)
    {
        this.action = action;
        this.Name = name;
        this.Description = description;
        this.Options = options;
    }
    
    /**
     * Returns the option with the given name.
     * 
     * @param optionName
     * @return 
     */
    public CommandOption getOption(String optionName)
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
     * Returns a command that has a different name and description,
     * but has the same functionality and options as this command.
     * 
     * @param commandName
     * @return 
     */
    public Command getSynonym(String commandName)
    {
        return new Command(this.action, commandName, String.format("Synonym for '%s'.", this.Name), this.Options);
    }
}
