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
import java.util.Map.Entry;
import java.util.TreeMap;

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
    protected final String WelcomeMsg;
    /**
     * The parsed commands, each command mapped to its primary name, in
     * alphabetical order.
     */
    protected final Map<String, PCommand> commandToPrimaryName = new TreeMap<>();
    /**
     * The parsed commands, each command mapped once for each of its names, in
     * alphabetical order.
     */
    protected final Map<String, PCommand> commandToAllNames = new TreeMap<>();

    public JavaCommander() throws JavaCommanderException
    {
        this(true);
    }

    /**
     * @param createBasicCommands whether or not to create basic utility
     * commands such as a 'help' command and a 'quit' command
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public JavaCommander(boolean createBasicCommands) throws
            JavaCommanderException
    {
        this(true, null);
    }

    /**
     * @param createBasicCommands whether or not to create basic utility
     * commands such as a 'help' command and a 'quit' command
     * @param welcomeMsg a welcoming message printed when run() is called. Leave
     * it null or empty to have no message printed.
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public JavaCommander(boolean createBasicCommands, String welcomeMsg) throws
            JavaCommanderException
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
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public final void execute(String string) throws JavaCommanderException
    {
        this.execute(stringAsArgs(string));
    }

    /**
     * Attempts to find and execute the command defined in a list of argument
     * tokens.
     *
     * @param args the list of argument tokens
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public final void execute(List<String> args) throws JavaCommanderException
    {
        // If no args are given, just return and do nothing.
        if (args == null || args.isEmpty())
        {
            return;
        }

        // Retrieve the command. If none was found, throw an error.
        PCommand command = commandToAllNames.get(args.get(0));
        if (command == null)
        {
            throw new JavaCommanderException(String.format(
                    "'%s' is not recognized as a command", args.get(
                            0)));
        }
        
        // Determine whether we're using explicit, or implicit, options for this command.
        // Only relevant if there are arguments given, so check args.size().
        POption currentOption = null;
        if (args.size() > 1)
        {
            String arg = args.get(1);
            currentOption = command.OptionsMapped.get(arg);
        }
        
        // Arguments to be passed to the Method.invoke function.
        Object[] finalArgs = new Object[command.Options.size()];
        
        // If the retrieved option is null, then use implicit options.
        if (currentOption == null)
        {
            // If too many arguments were supplied, throw an error.
            if (args.size() - 1 > finalArgs.length)
            {
                throw new JavaCommanderException(String.format("Too many arguments "
                        + "supplied for command '%s'", args.get(0)));
            }
            
            // Now simply iterate over the arguments, parsing them and placing
            // them in finalArgs as we go.
            for (int i = 1; i < args.size(); i++)
            {
                int iminus = i - 1;
                currentOption = command.Options.get(iminus);
                Object parsedArg = parseValue(args.get(i), currentOption.Translator,
                            currentOption.Type);
                finalArgs[iminus] = parsedArg;
            }
        }
        // Else if the retrieved option is not null, then use explicit options.
        else
        {
            for (int i = 2; i < args.size(); i++)
            {
                String arg = args.get(i);

                // If we're not currently finding a value for an option, then
                // try find the option.
                if (currentOption == null)
                {
                    currentOption = command.OptionsMapped.get(arg);

                    // If the option is not recognized, assume the option name is
                    // implicit and try parse the value to the current parameter.
                    if (currentOption == null)
                    {
                        throw new JavaCommanderException(String.format("'%s' is not recognized as "
                                + "an option for command '%s'", arg, args.get(0)));
                    }
                } // Else, try to parse the value.
                else
                {
                    Object parsedArg = parseValue(arg, currentOption.Translator,
                            currentOption.Type);
                    finalArgs[command.Options.indexOf(currentOption)] = parsedArg;
                    currentOption = null;
                }
            }

            // If the last parameter was not given a value, throw an error.
            if (currentOption != null)
            {
                throw new JavaCommanderException(String.format(
                        "No value found for option '%s'", currentOption.getPrimaryName()));
            }
        }

        // For each entry in finalArgs that is still null, check whether there
        // is a default value. If there is, use that. Else, throw an error.
        for (int i = 0; i < finalArgs.length; i++)
        {
            Object val = finalArgs[i];

            if (val == null)
            {
                POption option = command.Options.get(i);

                if (option.HasDefaultValue)
                {
                    finalArgs[i] = option.DefaultValue;
                } else
                {
                    throw new JavaCommanderException(String.format("Option '%s' is required",
                            option.getPrimaryName()));
                }
            }
        }

        try
        {
            // Finally, invoke the method on the object
            command.ToInvoke.invoke(command.ToInvokeOn, finalArgs);
        } catch (IllegalAccessException | IllegalArgumentException |
                InvocationTargetException ex)
        {
            throw new JavaCommanderException(String.format(
                    "Failed to invoke method behind command '%s'",
                    args.get(0)), ex);
        }

    }
    
    /**
     * Registers all commands found in the supplied Object. Any commands of
     * which the name is already registered will override the old values.
     *
     * @param obj the Object where commands are located within
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public final void registerObject(Object obj) throws JavaCommanderException
    {
        // iterate through the obj's class' methods
        for (final Method method : obj.getClass().getMethods())
        {
            // if we've found an annotated method, get to work.
            if (method.isAnnotationPresent(Command.class))
            {
                // Obtain the command and derive the needed values
                Command commandAnnotation = ((Command) method.getAnnotation(
                        Command.class));
                String[] names = commandAnnotation.names();
                String description = commandAnnotation.description();
                List<POption> options = parseOptions(commandAnnotation, method);

                // if no names were given, simply use the name of the method
                if (names.length <= 0)
                {
                    names = new String[]
                    {
                        method.getName()
                    };
                }

                // Create new PCommand object and register it.
                registerCommand(new PCommand(names, description, options, method, obj));
            }
        }
    }

    /**
     * Unregisters all commands found in the supplied Object.
     *
     * @param obj
     */
    public final void unregisterObject(Object obj)
    {
        // Keys which are to be removed from the maps
        List<String> keysToRemove = new ArrayList<>();
        
        // Collect keys to remove from commandToPrimaryName
        for (Entry<String, PCommand> entry : commandToPrimaryName.entrySet())
        {
            if (entry.getValue().ToInvokeOn.equals(obj))
            {
                keysToRemove.add(entry.getKey());
            }
        }

        // For each iteration, remove the key from commandToPrimaryName and use
        // its synonyms to remove keys from commandToAllNames
        for (String s : keysToRemove)
        {
            PCommand command = commandToPrimaryName.remove(s);
            
            for (String ss : command.Names)
            {
                commandToAllNames.remove(ss);
            }
        }
    }

    /**
     * Blocking method that continuously reads input from a BufferedReader.
     * JavaCommanderExceptions are printed, but don't stop the loop.
     */
    @Override
    public final void run()
    {
        // Print welcoming message and instantiate BufferedReader.
        if (WelcomeMsg != null && !WelcomeMsg.isEmpty())
        {
            System.out.println(WelcomeMsg + System.lineSeparator());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Thread loop.
        while (!Thread.currentThread().isInterrupted())
        {
            try
            {
                String command = br.readLine();
                System.out.println("--------------------");
                execute(command);
            } catch (IOException | JavaCommanderException ex)
            {
                System.out.println(ex.getMessage());
            }
            finally
            {
                System.out.println("--------------------");
                System.out.println();
            }
        }
    }

    /**
     * Parses a string to a list of argument tokens.
     *
     * @param string a string to parse to a list of argument tokens
     * @return a list of argument tokens
     */
    public final List<String> stringAsArgs(String string)
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
     * Returns a list of all parsed commands. The returned list should not be
     * altered. Could for example be used to create a custom help command.
     * 
     * @return a list of all parsed commands
     */
    public final List<PCommand> getParsedCommands()
    {
        return new ArrayList<>(commandToPrimaryName.values());
    }
    
    /**
     * Prints a list of all available commands. Called by the basic 'help'
     * command. If the given commandName is not null or empty, then the help of
     * the given command is given, listing its options. May be overridden to
     * implement custom help message.
     *
     * @param commandName
     */
    @Command(names = { "help", "usage", "?" }, description = "Display the help.",
            options = @Option(names = "-command", description
                    = "Display a specific command's help.",
                    hasDefaultValue = true))
    public void usage(String commandName)
    {
        String toString;

        // If no command name given, then list general info of all commands
        if (commandName == null || commandName.isEmpty())
        {
            toString = "Displaying help. Use option '-command' to display a specific "
                    + "command's help.\n\nAvailable commands:";

            // Iterate over the commands to find the info
            for (PCommand command : getParsedCommands())
            {
                toString += "\n" + command.Names[0];
                for (int i = 1; i < command.Names.length; i++)
                {
                    toString += ", " + command.Names[i];
                }
                toString += "\t\t" + (command.hasDescription() ? command.Description : "No description available.");
            }
        } // Else if a command name is given, then list info specific to that command
        else
        {
            // Retrieve the command. If it does not exist, then return with an error message.
            PCommand command = commandToAllNames.get(commandName);
            if (command == null)
            {
                System.out.println(String.format(
                        "'%s' is not recognized as a command", commandName));
                return;
            }

            // Build string
            toString = "Description:\n" + (command.hasDescription() ? command.Description : "No description available.") + "\n\n";

            // If there are synonyms, then list them.
            toString += "Synonyms:\n" + command.Names[0];
            for (int i = 1; i < command.Names.length; i++)
            {
                toString += ", " + command.Names[i];
            }
            toString += "\n\n";

            // If there are options, then list them.
            toString += "Available options:";
            if (command.hasOptions())
            {
                for (POption option : command.Options)
                {
                    toString += "\n" + option.Names[0];
                    for (int i = 1; i < option.Names.length; i++)
                    {
                        toString += ", " + option.Names[i];
                    }
                    toString += "\t\t" + option.Description;
                }
            } // Otherwise, inform the user there are no options.
            else
            {
                toString += "\nNo options available.";
            }
        }
        // Print help
        System.out.println(toString);
    }

    /**
     * Calls System.Exit(0). Used for the basic exit command.
     */
    @Command(names = { "exit", "quit" }, description = "Exit the program.")
    public void exitProgram()
    {
        System.exit(0);
    }
    
    /**
     * Registers the given command.
     *
     * @param command
     */
    private void registerCommand(PCommand command)
    {
        commandToPrimaryName.put(command.Names[0], command);

        for (String name : command.Names)
        {
            commandToAllNames.put(name, command);
        }
    }
    
    /**
     * Validates and parses all option annotations found in the command
     * annotation or on the method parameters to an array of POptions.
     *
     * @param commandAnnotation
     * @param method
     * @return
     */
    private List<POption> parseOptions(Command commandAnnotation, Method method)
            throws JavaCommanderException
    {
        List<POption> poptions = new ArrayList<>();
        Parameter[] params = method.getParameters();

        // If the number of options defined in the command annotation is equal to
        // the number of parameters, then we use those.
        if (commandAnnotation.options().length == params.length)
        {
            // Parse all options
            for (int i = 0; i < params.length; i++)
            {
                poptions.add(parseOption(commandAnnotation.options()[i],
                        params[i]));
            }
        } // Else, if there is not a single option defined in the command, we use
        // the options annotated on the parameters.
        else if (commandAnnotation.options().length == 0)
        {
            for (Parameter param : params)
            {
                // If the parameter is properly annotated, parse it.
                if (param.isAnnotationPresent(Option.class))
                {
                    poptions.add(parseOption(param.getAnnotation(Option.class),
                            param));
                } // If any parameter at all is not properly annotated, throw an exception.
                else
                {
                    throw new JavaCommanderException("Not all parameters of "
                            + method.
                            getDeclaringClass().getSimpleName() + "." + method.
                            getName() + "(...) are properly annotated with @Option!");
                }
            }
        } // Else, if there are options defined in the command, but not enough to
        // cover all parameters, then the annotations are wrong. Throw an error.
        else
        {
            throw new JavaCommanderException("Not all parameters of " + method.
                    getDeclaringClass().getSimpleName() + "." + method.getName()
                    + " are properly annotated with @Option!");
        }

        // Return the parsed poptions.
        return poptions;
    }

    /**
     * Validates and parses a single option annotation to a POption.
     *
     * @param optionAnnotation
     * @param param
     * @return
     */
    private POption parseOption(Option optionAnnotation, Parameter param) throws
            JavaCommanderException
    {
        // Get the option names. If none is assigned, use the parameter name.
        String[] names = optionAnnotation.names();
        if (names.length <= 0)
        {
            names = new String[]
            {
                param.getName()
            };
        }
        // Get other fields
        boolean hasDefaultValue = optionAnnotation.hasDefaultValue();
        String defaultValueStr = optionAnnotation.defaultValue();
        String description = optionAnnotation.description();
        Class type = param.getType();
        Class<? extends OptionTranslator> translatorClass = optionAnnotation.
                translator();

        // If there is a default value, then try to parse it.
        if (hasDefaultValue)
        {
            Object value = parseValue(defaultValueStr, translatorClass, param.
                    getType());
            
            // Return parsed POption.
            return new POption(names, description, hasDefaultValue, type,
                    value, translatorClass);

        } // Else, just parse and return a new POption.
        else
        {
            return new POption(names, description, hasDefaultValue, type, null,
                    translatorClass);
        }
    }

    /**
     * Attempts to parse the given String value to the given type, using the
     * given translator type.
     *
     * @param value
     * @param translatorType
     * @param toType
     * @return
     * @throws JavaCommanderException
     */
    private <T> T parseValue(String value,
            Class<? extends OptionTranslator> translatorType, Class<T> toType)
            throws JavaCommanderException
    {
        try
        {
            // If no translator is set, attempt a normal valueOf.
            if (translatorType.equals(NoTranslator.class))
            {
                return OptionTranslator.parseToPrimitive(value, toType);
            } // If one is set, then attempt using that.
            else
            {
                OptionTranslator translator = translatorType.newInstance();
                return (T) translator.translateString(value);
            }
        } catch (InstantiationException | IllegalAccessException ex)
        {
            throw new JavaCommanderException(
                    "Failed to instantiate translator '" + translatorType.
                    getSimpleName() + "'!", ex);
        } catch (NumberFormatException | IndexOutOfBoundsException ex)
        {
            throw new JavaCommanderException(
                    "Failed to parse String '" + value + "' to type " + toType.getSimpleName() 
                            + " using translator '" + translatorType.getSimpleName() 
                            + "'!", ex);
        }
    }

}
