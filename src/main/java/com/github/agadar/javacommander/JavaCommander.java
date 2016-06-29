package com.github.agadar.javacommander;

import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;
import com.github.agadar.javacommander.translator.NoTranslator;
import com.github.agadar.javacommander.translator.OptionTranslator;
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

/**
 * Manages an application's commands.
 *
 * @author Agadar
 */
public class JavaCommander implements Runnable
{

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

    /**
     * A special command name that can be used for a single command. If no known
     * command name can be found in a given string input, then an attempt to
     * call the master command is made, using said input.
     */
    public static final String MASTER_COMMAND = "_MASTER";

    /**
     * Parses a string to a list of argument tokens, and then attempts to find
     * and execute the command defined in it.
     *
     * @param string the string to parse
     * @throws JavaCommanderException
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
     * @throws JavaCommanderException
     */
    public final void execute(List<String> args) throws JavaCommanderException
    {
        // If no args are given, just return and do nothing.
        if (args == null || args.isEmpty())
        {
            return;
        }

        // What index in args to start reading parameters from. If this is a
        // normal command, then the parameters start at index 1, as index 0
        // is the command name. If this is the master command, then the
        // parameters start at index 0, as there is no command name.
        int paramsStartingIndex = 1;

        // Retrieve the command. If none was found, attempt to get the master command.
        PCommand command = commandToAllNames.get(args.get(0));
        if (command == null)
        {
            // If the master command was also not found, then throw an error.
            command = commandToPrimaryName.get(MASTER_COMMAND);

            if (command == null)
            {
                throw new JavaCommanderException(String.format(
                        "'%s' is not recognized as a command", args.get(
                                0)));
            }
            paramsStartingIndex = 0;
        }

        // Determine whether we're using explicit, or implicit, options for this command.
        // Only relevant if there are arguments given, so check args.size().
        POption currentOption = null;
        if (args.size() > paramsStartingIndex)
        {
            String arg = args.get(paramsStartingIndex);
            currentOption = command.OptionsMapped.get(arg);
        }

        // Arguments to be passed to the Method.invoke function.
        Object[] finalArgs = new Object[command.Options.size()];

        // If the retrieved option is null, then use implicit options.
        if (currentOption == null)
        {
            // If too many arguments were supplied, throw an error.
            if (args.size() - paramsStartingIndex > finalArgs.length)
            {
                throw new JavaCommanderException("Too many arguments supplied for this command");
            }

            // Now simply iterate over the arguments, parsing them and placing
            // them in finalArgs as we go.
            for (int i = paramsStartingIndex; i < args.size(); i++)
            {
                int iminus = i - paramsStartingIndex;
                currentOption = command.Options.get(iminus);
                Object parsedArg = parseValue(args.get(i), currentOption.Translator,
                        currentOption.Type);
                finalArgs[iminus] = parsedArg;
            }
        } // Else if the retrieved option is not null, then use explicit options.
        else
        {
            for (int i = paramsStartingIndex + 1; i < args.size(); i++)
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
                        throw new JavaCommanderException("'%s' is not recognized as an option for this command");
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
            throw new JavaCommanderException("Failed to invoke method behind this command", ex);
        }

    }

    /**
     * Creates the basic 'help' and 'exit' commands.
     *
     * @return this JavaCommander instance
     * @throws JavaCommanderException
     */
    public final JavaCommander createBasicCommands() throws JavaCommanderException
    {
        this.registerObject(this);
        return this;
    }

    /**
     * Registers all commands found in the supplied Object. Any commands of
     * which the name is already registered will override the old values.
     *
     * @param obj the Object where commands are located within
     * @throws JavaCommanderException
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
        commandToPrimaryName.entrySet().stream().filter((entry) -> (entry.getValue().ToInvokeOn.equals(obj))).forEach((entry)
                -> 
                {
                    keysToRemove.add(entry.getKey());
        });

        // For each iteration, remove the key from commandToPrimaryName and use
        // its synonyms to remove keys from commandToAllNames
        keysToRemove.stream().map((s) -> commandToPrimaryName.remove(s)).forEach((command)
                -> 
                {
                    for (String ss : command.Names)
                    {
                        commandToAllNames.remove(ss);
                    }
        });
    }

    /**
     * Blocking method that continuously reads input from a BufferedReader.
     * JavaCommanderExceptions are printed, but don't stop the loop.
     */
    @Override
    public final void run()
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Thread loop.
        while (!Thread.currentThread().isInterrupted())
        {
            try
            {
                String command = br.readLine();
                execute(command);
            } catch (IOException | JavaCommanderException ex)
            {
                System.out.println(ex.getMessage());
            } finally
            {
                System.out.println();   // always print a newline after a command
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
    @Command(names =
    {
        "help", "usage", "?"
    }, description = "Display the help.",
            options = @Option(names = "-command", description
                    = "Display a specific command's help.",
                    hasDefaultValue = true))
    public void usage(String commandName)
    {
        String toString = "--------------------\n";

        // If no command name given, then list general info of all commands
        if (commandName == null || commandName.isEmpty())
        {
            toString += "Displaying help. Use option '-command' to display a specific "
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
            toString += "Description:\n" + (command.hasDescription() ? command.Description : "No description available.") + "\n\n";

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
        System.out.println(toString + "\n--------------------");
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
                            getName() + " are properly annotated with @Option!");
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
