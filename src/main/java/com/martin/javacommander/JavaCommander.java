package com.martin.javacommander;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages an application's commands.
 *
 * @author Martin
 */
public class JavaCommander implements Runnable
{

    /**
     * All the registered commands.
     */
    private final TreeMap<String, Command> Commands = new TreeMap<>();
    /**
     * Welcoming message.
     */
    public final String WelcomeMsg;

    /**
     * @param welcomeMsg the welcoming message printed when run() is called
     */
    public JavaCommander(String welcomeMsg)
    {
        this(welcomeMsg, true);
    }

    /**
     * @param welcomeMsg the welcoming message printed when run() is called
     * @param createBasicCommands whether or not to create basic utility
     * commands such as a 'help' command and a 'quit' command
     */
    public JavaCommander(String welcomeMsg, boolean createBasicCommands)
    {
        this.WelcomeMsg = welcomeMsg;

        if (createBasicCommands)
        {
            // Help command
            Command helpCommand = new Command((options)
                    -> 
                    {
                        return this.usage();
            },
                    "help", "Display this help.");
            this.addCommand(helpCommand);
            this.addCommand(helpCommand.getSynonym("?"));

            // Quit command
            Command quitCommand = new Command((options)
                    -> 
                    {
                        System.exit(0);
                        return "Exiting...";
            },
                    "quit", "Quit the program.");
            this.addCommand(quitCommand);
            this.addCommand(quitCommand.getSynonym("exit"));
        }
    }

    /**
     * Adds a new command.
     *
     * @param action action performed by the command
     * @param name name of the command, should be unique
     * @param description description of the command
     */
    public void addCommand(ICommandAction action, String name, String description)
    {
        this.addCommand(action, name, description, null);
    }

    /**
     * Adds a new command.
     *
     * @param action action performed by the command
     * @param name name of the command, should be unique
     * @param description description of the command
     * @param options options for the command
     */
    public void addCommand(ICommandAction action, String name, String description, List<CommandOption> options)
    {
        this.addCommand(new Command(action, name, description, options));
    }

    /**
     * Adds a new command.
     *
     * @param command the new command to add
     */
    public final void addCommand(Command command)
    {
        Commands.put(command.Name, command);
    }

    /**
     * Parses a string to a list of argument tokens.
     *
     * @param string a string to parse to a list of argument tokens
     * @return a list of argument tokens
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
     * Parses and executes a string, and then returns any result or feedback
     * represented in a string
     *
     * @param string the string to parse and then execute
     * @return any result or feedback represented in a string
     */
    public String execute(String string)
    {
        return this.execute(stringAsArgs(string));
    }

    /**
     * Parses and executes a list of argument tokens, and then returns any
     * result or feedback represented in a string
     *
     * @param args the list of argument tokens to parse and then execute
     * @return any result or feedback represented in a string
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
            CommandOption option = command.findOption(args.get(i));

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
                    return String.format("Failed to identify value type for option '%s'", args.get(i));
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
                if (!co.Mandatory)
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
     * Gives a list of all available commands. Called by the basic 'help'
     * command.
     *
     * @return a list of all available commands
     */
    public String usage()
    {
        String toString = "List of all commands:";
        toString = Commands.entrySet().stream().map((entry) -> String.format("\n%s\t\t\t%s",
                entry.getValue().Name, entry.getValue().Description)).reduce(toString, String::concat);
        return toString;
    }

    /**
     * Blocking method that continuously reads input from a BufferedReader.
     */
    @Override
    public void run()
    {
        // Print welcoming message and instantiate BufferedReader.
        System.out.println(WelcomeMsg + System.lineSeparator());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            // Thread loop.
            while (!Thread.currentThread().isInterrupted())
            {
                String command = br.readLine();
                String result = execute(command);
                System.out.println(result + System.lineSeparator());
            }
        } catch (IOException ex)
        {
            Logger.getLogger(JavaCommander.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
