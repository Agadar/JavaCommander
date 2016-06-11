package com.martin.javacommander;

import com.martin.javacommander.annotations.Command;
import com.martin.javacommander.annotations.Option;
import com.martin.javacommander.translators.NoTranslator;
import com.martin.javacommander.translators.OptionTranslator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
     * Welcoming message printed when run() is called
     */
    public final String WelcomeMsg;
    /**
     * Methods to invoke, mapped to unique command names.
     */
    private final TreeMap<String, Method> commandMethods = new TreeMap<>();
    /**
     * Objects to invoke Methods from, mapped to unique command names.
     */
    private final TreeMap<String, Object> commandObjects = new TreeMap<>();

    public JavaCommander()
    {
        this(true);
    }

    /**
     * @param createBasicCommands whether or not to create basic utility
     *                            commands such as a 'help' command and a 'quit'
     *                            command
     */
    public JavaCommander(boolean createBasicCommands)
    {
        this(true, null);
    }

    /**
     * @param createBasicCommands whether or not to create basic utility
     *                            commands such as a 'help' command and a 'quit'
     *                            command
     * @param welcomeMsg          a welcoming message printed when run() is
     *                            called. Leave it null or empty to have no
     *                            message printed.
     */
    public JavaCommander(boolean createBasicCommands, String welcomeMsg)
    {
        this.WelcomeMsg = welcomeMsg;

        if (createBasicCommands)
        {
            this.registerObject(this);
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
     * @param args the list of argument tokens
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
                // last supplied option is followed by a supplied translatedValue
                if (i + 1 >= args.size())
                {
                    System.out.println(String.format("No value found for option '%s'", args.get(i)));
                    return;
                }

                suppliedOptions.put(args.get(i), args.get(i + 1));
            }

            // List that will be used as arguments when invoking the method
            List<Object> finalArgs = new ArrayList<>();
            Option[] optionsFromCommand = commandMethod.getAnnotation(Command.class).options();
            int indexInCommandOptions = 0;

            // Iterate over the method's parameters
            for (Parameter parameter : commandMethod.getParameters())
            {
                // First attempt to retrieve the Option from the parameter
                Option option = parameter.getAnnotation(Option.class);

                // If the option was not found, then try get the next Option from the Command
                if (option == null)
                {
                    if (indexInCommandOptions < optionsFromCommand.length)
                    {
                        option = optionsFromCommand[indexInCommandOptions];
                        indexInCommandOptions++;
                    }
                    // Failing that, print a warning message and return
                    else
                    {
                        System.out.println("Error while executing command: Not all parameters "
                                           + "of the object method called by this command are properly annotated.");
                        return;
                    }
                }

                String[] optionNames = option.names();

                // Make sure the option annotation has at least one name in it
                if (optionNames.length <= 0)
                {
                    optionNames = new String[]
                    {
                        parameter.getName()
                    };
                }

                // Check whether any of the supplied options is one of this 
                // parameter's annotation's names, and keep it on the side
                String suppliedOptionName = null;
                for (String optionName : optionNames)
                {
                    if (suppliedOptions.containsKey(optionName))
                    {
                        suppliedOptionName = optionName;
                        break;
                    }
                }

                // The option value, retrieved from either the supplied options
                // or the option's default value
                String optionValue;

                // If the option value was supplied, use that.
                if (suppliedOptionName != null)
                {
                    optionValue = suppliedOptions.remove(suppliedOptionName);
                }
                // Else, use the default value, but only if it has one assigned.
                else if (option.hasDefaultValue())
                {
                    optionValue = option.defaultValue();
                }
                // If it does not have one assigned, then we have a problem.
                else
                {
                    System.out.println(String.format("Option '%s' is required",
                                                     optionNames[0]));
                    return;
                }

                // The translated value
                Object translatedValue;

                // Retrieve the OptionTranslator.
                Class<? extends OptionTranslator> translatorType = option.translator();

                // If the translator is the default one, assume it's primitive
                if (translatorType.equals(NoTranslator.class))
                {
                    translatedValue = OptionTranslator.parseToPrimitive(optionValue, parameter.getType());
                }
                // Else, use the supplied translator.
                else
                {
                    try
                    {
                        OptionTranslator translator = translatorType.newInstance();
                        translatedValue = translator.translateString(optionValue);
                    }
                    catch (NumberFormatException | IndexOutOfBoundsException ex)
                    {
                        System.out.println(String.format("Value for option '%s' must be of type '%s'",
                                                         suppliedOptionName == null ? optionNames[0] : suppliedOptionName,
                                                         parameter.getType().getSimpleName()));
                        return;
                    }
                    catch (InstantiationException | IllegalAccessException ex)
                    {
                        throw ex;
                    }
                }

                // If the parsing went well, add the translatedValue to finalArgs
                finalArgs.add(translatedValue);
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

        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex)
        {
            Logger.getLogger(JavaCommander.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Calls System.Exit(0). Used for the basic exit command.
     */
    @Command(names =
    {
        "exit", "quit"
    }, description = "Exit the program.")
    public void exitProgram()
    {
        System.exit(0);
    }

    /**
     * Registers all commands found in the supplied Object. Any commands of
     * which the name is already registered will override the old values.
     *
     * @param obj the Object where commands are located within
     */
    public void registerObject(Object obj)
    {
        // iterate through the obj's class' methods
        for (final Method method : obj.getClass().getMethods())
        {
            // if we've found an annotated method, add it.
            if (method.isAnnotationPresent(Command.class))
            {
                String[] names = ((Command) method.getAnnotation(Command.class)).names();

                // if no names were given, simply use the name of the method
                if (names.length <= 0)
                {
                    names = new String[]
                    {
                        method.getName()
                    };
                }

                // for each command name, register a new command
                for (String name : names)
                {
                    putCommand(name, obj, method);
                }
            }
        }
    }

    /**
     * Blocking method that continuously reads input from a BufferedReader.
     */
    @Override
    public void run()
    {
        // Print welcoming message and instantiate BufferedReader.
        if (WelcomeMsg != null && !WelcomeMsg.isEmpty())
        {
            System.out.println(WelcomeMsg + System.lineSeparator());
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            // Thread loop.
            while (!Thread.currentThread().isInterrupted())
            {
                String command = br.readLine();
                System.out.println();
                execute(command);
                System.out.println();
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(JavaCommander.class
                    .getName()).log(Level.SEVERE, null, ex);
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
        string = string.trim();
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
                // ...and we're not in a quote...
                if (!insideQuote)
                {
                    // ...and the current token is at least 1 character long,
                    // then the current token is finished.
                    if (curToken.length() > 0)
                    {
                        tokens.add(curToken.toString());
                        curToken.delete(0, curToken.length());
                    }
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
     * Unregisters the command with the given name.
     *
     * @param commandName the name of the command to unregister
     */
    public void unregisterCommand(String commandName)
    {
        commandObjects.remove(commandName);
        commandMethods.remove(commandName);
    }

    /**
     * Unregisters all commands found in the supplied object.
     *
     * @param obj the object to find commands to unregister in
     */
    public void unregisterObject(Object obj)
    {
        // iterate through the object's class' methods
        for (final Method method : obj.getClass().getMethods())
        {
            // if we've found an annotated method, add it.
            if (method.isAnnotationPresent(Command.class))
            {
                for (String name : ((Command) method.getAnnotation(Command.class)).names())
                {
                    unregisterCommand(name);
                }
            }
        }
    }

    /**
     * Prints a list of all available commands. Called by the basic 'help'
     * command. If the given commandName is not null or empty, then the help of
     * the given command is given, listing its options.
     *
     * @param commandName
     */
    @Command(
            names =
            {
                "help", "usage", "?"
            },
            description = "Display the help.",
            options = @Option(names = "-c", description = "Display a specific command's help.", hasDefaultValue = true)
    )
    public void usage(String commandName)
    {
        // List available commands
        if (commandName == null || commandName.isEmpty())
        {
            String toString = "Displaying help. Use option '-c' to display a specific command's help.\n\n";
            toString += "List of available commands:";

            // Iterate over the Methods to find the descriptions
            for (Map.Entry<String, Method> entry : commandMethods.entrySet())
            {
                String description = ((Command) entry.getValue().
                                      getAnnotation(Command.class
                                      )).description();
                toString += String.format("\n%s\t\t\t%s", entry.getKey(), description);
            }

            // Print help
            System.out.println(toString);
        } // List specific command's options
        else
        {
            // Retrieve the command. If it does not exist, then return with an error message.
            Method method = commandMethods.get(commandName);
            if (method == null)
            {
                System.out.println(String.format("'%s' is not recognized as a command", commandName));
                return;

            }

            // Add description
            String toString = "";
            String description = ((Command) method.getAnnotation(Command.class)).description();
            if (description != null && !description.isEmpty())
            {
                toString += description + "\n\n";
            }

            // Retrieve parameters
            Parameter[] params = method.getParameters();

            // If there are options to list, then list them.
            if (params.length > 0)
            {
                toString += "List of available options:";
                Option[] optionsFromCommand = method.getAnnotation(Command.class).options();
                int indexInCommandOptions = 0;

                for (Parameter param : params)
                {
                    // First attempt to retrieve the Option from the parameter
                    Option option = param.getAnnotation(Option.class);

                    // If the option was not found, then try get the next Option from the Command
                    if (option == null)
                    {
                        if (indexInCommandOptions < optionsFromCommand.length)
                        {
                            option = optionsFromCommand[indexInCommandOptions];
                            indexInCommandOptions++;
                        }
                        // Failing that, print a warning message and return
                        else
                        {
                            System.out.println("Error while executing command: Not all parameters "
                                               + "of the object method called by this command are properly annotated.");
                            return;
                        }
                    }

                    String[] optionNames = option.names();

                    // Make sure the option annotation has at least one name in it
                    if (optionNames.length <= 0)
                    {
                        optionNames = new String[]
                        {
                            param.getName()
                        };
                    }

                    // Append the data to the string
                    String optionsList = optionNames[0];
                    for (int i = 1; i < optionNames.length; i++)
                    {
                        optionsList += ", " + optionNames[i];
                    }

                    toString += String.format("\n%s\t\t%s\t\t%s", optionsList,
                                              param.getType().getSimpleName(), option.description());
                }
            }
            else
            {
                toString += "No options available for this command.";
            }

            // Print help
            System.out.println(toString);
        }
    }

    /**
     * Maps the given Object and Method to the given command name.
     *
     * @param name   name of the command
     * @param object object to invoke the method from when the command is called
     * @param method method to invoke when the command is called
     */
    private void putCommand(String name, Object object, Method method)
    {
        commandObjects.put(name, object);
        commandMethods.put(name, method);
    }

}
