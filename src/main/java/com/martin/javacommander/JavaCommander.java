package com.martin.javacommander;

import com.martin.javacommander.annotations.CommandFunction;
import com.martin.javacommander.annotations.CommandFunctionOption;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
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
    @Deprecated
    private final TreeMap<String, Command> Commands = new TreeMap<>();
    /**
     * Welcoming message.
     */
    public final String WelcomeMsg;

    /**
     * Unique command names with references to Methods to invoke and Objects to
     * invoke said Methods from.
     */
    private final TreeMap<String, Object> commandObjects = new TreeMap<>();
    private final TreeMap<String, Method> commandMethods = new TreeMap<>();

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
            CommandOption commandInfo = new CommandOption("-c", "Display a specific command's help.", "");
            Command helpCommand = new Command("help", "Display this help.", Arrays.asList(commandInfo), (options)
                    -> 
                    {
                        String commandName = (String) options.get("-c").Value;

                        if (commandName.isEmpty())
                        {
                            return this.usage();
                        }
                        return this.usage(commandName);
            });
            this.addCommand(helpCommand);
            this.addCommand(helpCommand.getSynonym("?"));

            // Quit command
            Command quitCommand = new Command("quit", "Quit the program.", (options)
                    -> 
                    {
                        System.exit(0);
                        return "Exiting...";
            });
            this.addCommand(quitCommand);
            this.addCommand(quitCommand.getSynonym("exit"));
        }
    }

    /**
     * Registers all commands found in the supplied object.
     *
     * @param o the object where the to-register commands are located within
     */
    public void registerCommands(Object o)
    {
        // the object class, with which we start out
        Class oClass = o.getClass();

        // iterate through the object's class' hierarchy
        while (oClass != Object.class)
        {
            // iterate through the current class' methods
            for (final Method method : new ArrayList<>(Arrays.asList(oClass.getDeclaredMethods())))
            {
                // if we've found an annotated method, add it.
                if (method.isAnnotationPresent(CommandFunction.class))
                {
                    CommandFunction annotation = method.getAnnotation(CommandFunction.class);
                    String primaryName = annotation.names()[0];
                    commandObjects.put(primaryName, o);
                    commandMethods.put(primaryName, method);
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            oClass = oClass.getSuperclass();
        }
    }

    /**
     * Parses a string to a command and options, and then executes that command.
     *
     * @param string
     */
    public void execute2(String string)
    {
        try
        {
            List<String> arguments = stringAsArgs(string);

            // Retrieve the relevant object and method
            String commandName = arguments.get(0);
            Object commandObj = commandObjects.get(commandName);
            Method commandMethod = commandMethods.get(commandName);

            // If the command was not found, print a warning message and return
            if (commandObj == null || commandMethod == null)
            {
                System.out.println(String.format("'%s' is not recognized as a command", commandName));
                return;
            }

            // Map the options supplied by the caller
            Map<String, String> suppliedOptions = new TreeMap<>();
            for (int i = 1; i < arguments.size(); i += 2)
            {
                // Prevent an IndexOutOfBoundsException by checking whether the
                // last supplied option is followed by a supplied value
                if (i + 1 >= arguments.size())
                {
                    System.out.println(String.format("No value found for option '%s'", arguments.get(i)));
                    return;
                }

                suppliedOptions.put(arguments.get(i), arguments.get(i + 1));
            }

            // List that will be used as arguments when invoking the method
            List<Object> finalArgs = new ArrayList<>();

            // Iterate over the method's parameters
            for (Parameter parameter : commandMethod.getParameters())
            {
                // Retrieve the @CommandFunctionOption annotation
                CommandFunctionOption annotation = parameter.getAnnotation(CommandFunctionOption.class);

                // If the annotation was not found, print a warning message and return
                if (annotation == null)
                {
                    System.out.println("Error while executing command: Not all parameters "
                            + "of the object method called by this command are properly annotated.");
                    return;
                }

                // Internal try-catch for translating the supplied value to the correct type
                try
                {
                    // Prepare property editor for translating
                    PropertyEditor editor = PropertyEditorManager.findEditor(parameter.getType());

                    // If the option value was supplied, use that.
                    if (suppliedOptions.containsKey(annotation.names()[0]))
                    {
                        // also remove the option from the supplied options so that we
                        // can later determine incorrect options that were given
                        editor.setAsText(suppliedOptions.remove(annotation.names()[0]));
                    } 
                    // Else, use the default value, but only if the option is not mandatory.
                    // If it is mandatory, then return and log a warning
                    else if (!annotation.mandatory())
                    {
                        editor.setAsText(annotation.defaultValue()[0]);
                    }
                    else
                    {
                        System.out.println(String.format("Option '%s' is required", annotation.names()[0]));
                        return;
                    }                    
                    
                    // Set the value and make sure it is not null
                    Object value = editor.getValue();                   
                    if (value == null)
                    {
                        System.out.println(String.format("Failed to identify value type for option '%s'", annotation.names()[0]));
                        return;
                    }
                    
                    // If the parsing went well, add the value to finalArgs
                    finalArgs.add(value);
                } 
                catch (Exception ex)
                {
                    System.out.println(String.format("Value for option '%s' must be of type '%s'", 
                            annotation.names()[0], parameter.getType().getSimpleName()));
                    return;
                }
            }

            // Ensure the supplied options have been exhausted, giving a warning
            // and returning if this isn't the case
            if (suppliedOptions.size() > 0)
            {
                System.out.println(String.format("'%s' is not recognized as an option for this command", suppliedOptions.keySet().toArray()[0]));
                return;
            }

            // Invoke the method on the object
            commandMethod.invoke(commandObj, finalArgs.toArray());

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            Logger.getLogger(JavaCommander.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds a new command.
     *
     * @param action action performed by the command
     * @param name name of the command, should be unique
     * @param description description of the command
     */
    @Deprecated
    public void addCommand(String name, String description, ICommandAction action)
    {
        this.addCommand(name, description, null, action);
    }

    /**
     * Adds a new command.
     *
     * @param action action performed by the command
     * @param name name of the command, should be unique
     * @param description description of the command
     * @param options options for the command
     */
    @Deprecated
    public void addCommand(String name, String description, List<CommandOption> options, ICommandAction action)
    {
        this.addCommand(new Command(name, description, options, action));
    }

    /**
     * Adds a new command.
     *
     * @param command the new command to add
     */
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
        String toString = "Displaying help. Use option '-c' to display a specific command's help.\n\n";
        toString += "List of available commands:";
        toString = Commands.entrySet().stream().map((entry) -> String.format("\n%s\t\t\t%s",
                entry.getValue().Name, entry.getValue().Description)).reduce(toString, String::concat);
        return toString;
    }

    /**
     * Gives the description and a list of all available options for the command
     * with the given command name. Called by the basic 'help' command when the
     * '-c' option is set.
     *
     * @param commandName the name of the command to return info of
     * @return description and a list of all available options for the command
     * with the given command name
     */
    public String usage(String commandName)
    {
        // Retrieve the command. If it does not exist, then return with an error message.
        Command command = this.Commands.get(commandName);

        if (command == null)
        {
            return String.format("'%s' is not recognized as a command", commandName);
        }

        // If the command does exist, then print its info.
        String toString = command.Description + "\n\n";

        // If there are options to list, then list them.
        if (command.Options.size() > 0)
        {
            toString += "List of available options:";
            toString = command.Options.entrySet().stream().map((entry) -> String.format("\n%s\t\t%s\t\t%s",
                    entry.getValue().Name, entry.getValue().getValueType().getSimpleName(), entry.getValue().Description)).reduce(toString, String::concat);
        } else
        {
            toString += "No options available for this command.";
        }
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
                System.out.println(System.lineSeparator() + result + System.lineSeparator());
            }
        } catch (IOException ex)
        {
            Logger.getLogger(JavaCommander.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
