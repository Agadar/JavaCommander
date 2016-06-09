package com.martin.javacommander;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import com.martin.javacommander.annotations.Command;
import com.martin.javacommander.annotations.CommandOption;

/**
 * Manages an application's commands.
 *
 * @author Martin
 */
public class JavaCommander implements Runnable
{

    /**
     * Welcoming message.
     */
    public final String WelcomeMsg;
    /**
     * Objects to invoke Methods from, mapped to unique command names.
     */
    private final TreeMap<String, Object> commandObjects = new TreeMap<>();
    /**
     * Methods to invoke, mapped to unique command names.
     */
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
//            CommandOption commandInfo = new CommandOption("-c", "Display a specific command's help.", "");
//            Command helpCommand = new Command("help", "Display this help.", Arrays.asList(commandInfo), (options)
//                    -> 
//                    {
//                        String commandName = (String) options.get("-c").Value;
//
//                        if (commandName.isEmpty())
//                        {
//                            return this.usage();
//                        }
//                        return this.usage(commandName);
//            });
//            this.addCommand(helpCommand);
//            this.addCommand(helpCommand.getSynonym("?"));
//
//            // Quit command
//            Command quitCommand = new Command("quit", "Quit the program.", (options)
//                    -> 
//                    {
//                        System.exit(0);
//                        return "Exiting...";
//            });
//            this.addCommand(quitCommand);
//            this.addCommand(quitCommand.getSynonym("exit"));
        }
    }

    /**
     * Registers all commands found in the supplied object.
     *
     * @param o the object where commands are located within
     */
    public void registerObject(Object o)
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
                if (method.isAnnotationPresent(Command.class))
                {
                    Command annotation = method.getAnnotation(Command.class);
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
     * Parses a string to a list of argument tokens, and then attempts to find
     * and execute the command defined in it.
     *
     * @param string the string to parse
     */
    public void execute(String string)
    {
        this.execute(stringAsArgs(string));
    }

    /**
     * Attempts to find and execute the command defined in a list of argument
     * tokens.
     *
     * @param args
     *          the list of argument tokens
     */
    public void execute(List<String> args)
    {
        try
        {
            // Retrieve the relevant object and method
            String commandName = args.get(0);
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
            for (int i = 1; i < args.size(); i += 2)
            {
                // Prevent an IndexOutOfBoundsException by checking whether the
                // last supplied option is followed by a supplied value
                if (i + 1 >= args.size())
                {
                    System.out.println(String.format("No value found for option '%s'", args.get(i)));
                    return;
                }

                suppliedOptions.put(args.get(i), args.get(i + 1));
            }

            // List that will be used as arguments when invoking the method
            List<Object> finalArgs = new ArrayList<>();

            // Iterate over the method's parameters
            for (Parameter parameter : commandMethod.getParameters())
            {
                // Retrieve the @CommandOption annotation
                CommandOption annotation = parameter.getAnnotation(CommandOption.class);

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
                    } // Else, use the default value, but only if the option is not mandatory.
                    // If it is mandatory, then return and log a warning
                    else if (!annotation.mandatory())
                    {
                        editor.setAsText(annotation.defaultValue()[0]);
                    } else
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
                } catch (Exception ex)
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

            // Finally, invoke the method on the object
            commandMethod.invoke(commandObj, finalArgs.toArray());

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            Logger.getLogger(JavaCommander.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * Gives a list of all available commands. Called by the basic 'help'
     * command.
     *
     * @return a list of all available commands
     */
    public String usage()
    {
//        String toString = "Displaying help. Use option '-c' to display a specific command's help.\n\n";
//        toString += "List of available commands:";
//        toString = Commands.entrySet().stream().map((entry) -> String.format("\n%s\t\t\t%s",
//                entry.getValue().Name, entry.getValue().Description)).reduce(toString, String::concat);
//        return toString;
        throw new UnsupportedOperationException();
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
//        Command command = this.Commands.get(commandName);
//
//        if (command == null)
//        {
//            return String.format("'%s' is not recognized as a command", commandName);
//        }
//
//        // If the command does exist, then print its info.
//        String toString = command.Description + "\n\n";
//
//        // If there are options to list, then list them.
//        if (command.Options.size() > 0)
//        {
//            toString += "List of available options:";
//            toString = command.Options.entrySet().stream().map((entry) -> String.format("\n%s\t\t%s\t\t%s",
//                    entry.getValue().Name, entry.getValue().getValueType().getSimpleName(), entry.getValue().Description)).reduce(toString, String::concat);
//        } else
//        {
//            toString += "No options available for this command.";
//        }
//        return toString;
        throw new UnsupportedOperationException();
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
                execute(command);
                System.out.println();
            }
        } catch (IOException ex)
        {
            Logger.getLogger(JavaCommander.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
