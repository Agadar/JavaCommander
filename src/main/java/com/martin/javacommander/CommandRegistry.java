package com.martin.javacommander;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Manages this application's commands.
 *
 * @author Martin
 */
public class CommandRegistry
{
    /**
     * All the registered commands.
     */
    private final TreeMap<String, Command> Commands = new TreeMap<>();

    public CommandRegistry()
    {
        this(true);
    }
    
    /**
     * Constructor. If createBasicCommands is set to true, then some basic
     * commands such as a 'help' command and a 'quit' command are created.
     * 
     * @param createBasicCommands
     */
    public CommandRegistry(boolean createBasicCommands)
    {
        if (createBasicCommands)
        {
            // Help command
            Command helpCommand = new Command((options) -> { return this.helpMessage(); },
                    "help", "Display this help.");
            this.put(helpCommand);
            this.put(helpCommand.getSynonym("?"));
            
            // Quit command
            Command quitCommand = new Command((options) -> { System.exit(0); return "Exiting..."; },
                    "quit", "Quit the program.");
            this.put(quitCommand);
            this.put(quitCommand.getSynonym("exit"));
        }
    }
    
    /**
     * Registers a new command.
     *
     * @param action
     * @param name
     * @param description
     */
    public void put(ICommandAction action, String name, String description)
    {
        this.put(action, name, description, null);
    }

    /**
     * Registers a new command.
     *
     * @param action
     * @param name
     * @param description
     * @param options
     */
    public void put(ICommandAction action, String name, String description, List<CommandOption> options)
    {
        this.put(new Command(action, name, description, options));
    }

    /**
     * Registers a new command.
     *
     * @param command
     */
    public final void put(Command command)
    {
        Commands.put(command.Name, command);
    }

    /**
     * Attempts to parse the line to a command and execute it.
     *
     * @param line
     * @return Human-readable feedback.
     */
    public String parseAndExecute(String line)
    {
        // Validate input line
        if (line == null)
        {
            return "";
        }

        line = line.trim();

        if (line.isEmpty())
        {
            return "";
        }

        // Validate command
        String[] broken = line.split(" ");
        Command command = Commands.get(broken[0]);

        if (command == null)
        {
            return String.format("'%s' is not recognized as a command", broken[0]);
        }
        
        // Options to be inserted into the command.
        Map<String, CommandOption> options = new TreeMap<>();

        // Validate command parameters       
        for (int i = 1; i < broken.length; i += 2)
        {
            CommandOption option = command.getOption(broken[i]);

            if (option == null)
            {
                return String.format("'%s' is not recognized as an option for this command", broken[i]);
            }

            if (i + 1 >= broken.length)
            {
                return String.format("No value found for option '%s'", broken[i]);
            }

            // Option value retrieved from the input text
            Object value;
            
            try
            {
                PropertyEditor editor = PropertyEditorManager.findEditor(option.getValueType());
                editor.setAsText(broken[i + 1]);
                value = editor.getValue();
                
                if (value == null)
                    throw new IllegalArgumentException();
                
            } catch (Exception ex)
            {
                return String.format("Value for option '%s' must be of type '%s'", broken[i], option.getValueType().getSimpleName());
            }
            
            // Create CommandOption for the command's execution.
            options.put(option.Name, option.copyWithValue(value));
        }
        
        // For each option that wasn't set, use the default value.
        // Throw a warning if a required option was not set.
        for (CommandOption co : command.Options.values())
        {
            if (!options.containsKey(co.Name))
            {
                if (!co.IsRequired)
                    options.put(co.Name, co);
                else
                   return String.format("Option '%s' is required", co.Name); 
            }
        }

        // Finally, execute the function.
        return command.execute(options);
    }
    
    /**
     * Gives a list of all available commands.
     * 
     * @return 
     */
    public String helpMessage()
    {
        String toString = "List of all commands:";
        toString = Commands.entrySet().stream().map((entry) -> String.format("\n%s\t\t\t%s", 
                entry.getValue().Name, entry.getValue().Description)).reduce(toString, String::concat);
        return toString;
    }
}
