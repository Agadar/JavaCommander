package com.martin.javacommander;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.ArrayList;
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
            Command helpCommand = new Command((options)
                    -> 
                    {
                        return this.helpMessage();
            },
                    "help", "Display this help.");
            this.put(helpCommand);
            this.put(helpCommand.getSynonym("?"));

            // Quit command
            Command quitCommand = new Command((options)
                    -> 
                    {
                        System.exit(0);
                        return "Exiting...";
            },
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
     * Splits a String to a String List of argument tokens.
     *
     * @param string
     * @return
     */
    public List<String> stringAsArgs(String string)
    {
        List<String> tokens = new ArrayList<>();        // the token list to be returned
        StringBuilder curToken = new StringBuilder();   // current token
        boolean insideQuote = false;    // are we currently within quotes?
        boolean escapeNextChar = false; // must we escape the current char?

        // Iterate over all chars in the string
        for (char c : string.toCharArray())
        {
            // If we are to escape the next char, then append it to the token
            // and turn off the escape option.
            if (escapeNextChar)
            {
                curToken.append(c);
                escapeNextChar = false;
            } // Else if the character is a quote mark, then toggle insideQuote.
            else if (c == '"' || c == '\'')
            {
                insideQuote = !insideQuote;
            } // Else if the character is the escape character, then set escapeNextChar on.
            else if (c == '\\')
            {
                escapeNextChar = true;
            } // Else if the character is a space...
            else if (c == ' ')
            {
                // ...and we're not in a quote, then the current token is finished.
                if (!insideQuote)
                {
                    tokens.add(curToken.toString());
                    curToken.delete(0, curToken.length());
                } // ...and we're in a quote, then append it to the token.
                else
                {
                    curToken.append(c);
                }
            } // Else, append to the token.
            else
            {
                curToken.append(c);
            }
        }
        // Add the last token to the list and then return the list.
        tokens.add(curToken.toString());
        return tokens;
    }

    /**
     * Parses and executes a String, returning its reply.
     *
     * @param string
     * @return
     */
    public String execute(String string)
    {
        return this.execute(stringAsArgs(string));
    }

    /**
     * Parses and executes a String List of argument tokens, returning its
     * reply.
     *
     * @param args
     * @return
     */
    public String execute(List<String> args)
    {
        Command command = Commands.get(args.get(0));

        if (command == null)
        {
            return String.format("'%s' is not recognized as a command", args.get(0));
        }

        // Options to be inserted into the command.
        Map<String, CommandOption> options = new TreeMap<>();

        // Validate command parameters       
        for (int i = 1; i < args.size(); i += 2)
        {
            CommandOption option = command.getOption(args.get(i));

            if (option == null)
            {
                return String.format("'%s' is not recognized as an option for this command", args.get(i));
            }

            if (i + 1 >= args.size())
            {
                return String.format("No value found for option '%s'", args.get(i));
            }

            // Option value retrieved from the input text
            Object value;

            try
            {
                PropertyEditor editor = PropertyEditorManager.findEditor(option.getValueType());
                editor.setAsText(args.get(i + 1));
                value = editor.getValue();

                if (value == null)
                {
                    throw new IllegalArgumentException();
                }

            } catch (Exception ex)
            {
                return String.format("Value for option '%s' must be of type '%s'", args.get(i), option.getValueType().getSimpleName());
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
                {
                    options.put(co.Name, co);
                } else
                {
                    return String.format("Option '%s' is required", co.Name);
                }
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
