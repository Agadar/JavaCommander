package com.martin.javacommander;

import com.martin.javacommander.annotations.Command;
import com.martin.javacommander.annotations.Option;
import com.martin.javacommander.translators.NoTranslator;
import com.martin.javacommander.translators.OptionTranslator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
     * PCommands, mapped to unique command names. Will replace commandMethods
     * and commandObjects.
     */
    private final TreeMap<String, PCommand> commands = new TreeMap<>();

    public JavaCommander() throws JavaCommanderException
    {
        this(true);
    }

    /**
     * @param createBasicCommands whether or not to create basic utility
     *                            commands such as a 'help' command and a 'quit'
     *                            command
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public JavaCommander(boolean createBasicCommands) throws
            JavaCommanderException
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
    public void execute(String string) throws JavaCommanderException
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
    public void execute(List<String> args) throws JavaCommanderException
    {
        // If no args are given, just return and do nothing.
        if (args == null || args.isEmpty())
        {
            return;
        }

        // Retrieve the command. If none was found, throw an error.
        PCommand command = commands.get(args.get(0));
        if (command == null)
        {
            System.out.println((String.format(
                                "'%s' is not recognized as a command", args.get(
                                0))));
            return;
        }

        // Arguments to be passed to the Method.invoke function.
        Object[] finalArgs = new Object[command.Options.size()];
        int curIndexInFinalArgs = 0;
        POption currentOption = null;
        for (String arg : args)
        {
            // If we're not currently finding a value for an option, then
            // try find the option.
            if (currentOption == null)
            {
                currentOption = command.OptionsMapped.get(arg);

                // If the option is not recognized, throw an error.
                if (currentOption == null)
                {
                    System.out.println(String.format(
                            "'%s' is not recognized as an option for this command",
                            args));
                    return;
                }
            }
            // Else, try to parse the value.
            else
            {
                Object parsedArg = parseValue(arg, currentOption.Translator, 
                    command.ToInvoke.getParameters()[command.Options.indexOf(currentOption)].getClass());
                finalArgs[curIndexInFinalArgs++] = parsedArg;
                currentOption = null;
            }
        }
        
        // If the last parameter was not given a value, throw an error.
        if (currentOption != null)
        {
            System.out.println(String.format(
                        "No value found for option '%s'", currentOption.Names[0]));
                return;
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
                    
                }
                else
                {
                    // WE WUZ HERE N SHEET
                }
            }
        }

        
        
        
        
        
        
        // Map the options supplied by the caller
        Map<String, String> suppliedOptions = new TreeMap<>();
        for (int i = 1; i < args.size(); i += 2)
        {
            // Prevent an IndexOutOfBoundsException by checking whether the
            // last supplied option is followed by a supplied translatedValue
            if (i + 1 >= args.size())
            {
                System.out.println(String.format(
                        "No value found for option '%s'", args.get(i)));
                return;
            }

            suppliedOptions.put(args.get(i), args.get(i + 1));
        }

        // List that will be used as arguments when invoking the method
        List<Object> finalArgs = new ArrayList<>();
        Option[] optionsFromCommand = commandMethod.getAnnotation(
                Command.class).options();
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
                    System.out.println(
                            "Error while executing command: Not all parameters "
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
            Class<? extends OptionTranslator> translatorType = option.
                    translator();

            // If the translator is the default one, assume it's primitive
            if (translatorType.equals(NoTranslator.class))
            {
                translatedValue = OptionTranslator.parseToPrimitive(
                        optionValue, parameter.getType());
            }
            // Else, use the supplied translator.
            else
            {
                try
                {
                    OptionTranslator translator = translatorType.
                            newInstance();
                    translatedValue = translator.
                            translateString(optionValue);
                }
                catch (NumberFormatException | IndexOutOfBoundsException ex)
                {
                    System.out.println(String.format(
                            "Value for option '%s' must be of type '%s'",
                            suppliedOptionName == null ? optionNames[0]
                                    : suppliedOptionName,
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
            System.out.println(String.format(
                    "'%s' is not recognized as an option for this command",
                    suppliedOptions.keySet().toArray()[0]));
            return;
        }

        // Finally, invoke the method on the object
        commandMethod.invoke(commandObj, finalArgs.toArray());

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
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public void registerObject(Object obj) throws JavaCommanderException
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

                // Create new PCommand object that is the basis, and add it to the commands with its primary name
                PCommand pcommand = new PCommand(names, description, options,
                                                 method, obj);
                commands.put(pcommand.Names[0], pcommand);

                // For each synonym, add another entry.
                String synonymDescr = "Synonym of " + pcommand.Names[0];
                for (int i = 1; i < pcommand.Names.length; i++)
                {
                    commands.put(pcommand.Names[i], new PCommand(names,
                                                                 synonymDescr,
                                                                 options, method,
                                                                 obj));
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

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                System.in)))
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
        catch (IOException | JavaCommanderException ex)
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
            options = @Option(names = "-c", description
                                                    = "Display a specific command's help.",
                    hasDefaultValue = true)
    )
    public void usage(String commandName)
    {
        String toString;

        // If no command name given, then list general info of all commands
        if (commandName == null || commandName.isEmpty())
        {
            toString = "Displaying help. Use option '-c' to display a specific "
                               + "command's help.\n\nList of available commands:";

            // Iterate over the Methods to find the descriptions
            for (Map.Entry<String, PCommand> entry : commands.entrySet())
            {
                toString += String.format("\n%s\t\t\t%s", entry.getKey(),
                                          entry.getValue().Description);
            }
        }
        // Else if a command name is given, then list info specific to that command
        else
        {
            // Retrieve the command. If it does not exist, then return with an error message.
            PCommand command = commands.get(commandName);
            if (command == null)
            {
                System.out.println(String.format(
                        "'%s' is not recognized as a command", commandName));
                return;
            }

            // Build string
            toString = command.Description + "\n\n";

            // If there are options, then list them.
            if (command.Options.size() > 0)
            {
                toString += "List of available options:";

                for (POption option : command.Options)
                {
                    toString += "";

                    // Append the data to the string
                    toString += "\n" + option.Names[0];
                    for (int i = 1; i < option.Names.length; i++)
                    {
                        toString += ", " + option.Names[i];
                    }
                    toString += "\t\t" + option.Description;
                }
            }
            // Otherwise, inform the user there are no options.
            else
            {
                toString += "No options available for this command.";
            }
        }
        // Print help
        System.out.println(toString);
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
        }
        // Else, if there is not a single option defined in the command, we use
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
                }
                // If any parameter at all is not properly annotated, throw an exception.
                else
                {
                    throw new JavaCommanderException("Not all parameters of "
                                                             + method.
                            getDeclaringClass().getSimpleName() + "." + method.
                            getName() + " are properly annotated with @Option!");
                }
            }
        }
        // Else, if there are options defined in the command, but not enough to
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
        Class<? extends OptionTranslator> translatorClass = optionAnnotation.
                translator();

        // If there is a default value, then try to parse it.
        if (hasDefaultValue)
        {
            Object value = parseValue(defaultValueStr, translatorClass, param.
                                      getType());
            // Return parsed POption.
            return new POption(names, description, hasDefaultValue,
                               value, translatorClass);

        }
        // Else, just parse and return a new POption.
        else
        {
            return new POption(names, description, hasDefaultValue, null,
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
            }
            // If one is set, then attempt using that.
            else
            {
                OptionTranslator translator = translatorType.newInstance();
                return (T) translator.translateString(value);
            }
        }
        catch (InstantiationException | IllegalAccessException ex)
        {
            throw new JavaCommanderException(
                    "Failed to instantiate translator '" + translatorType.
                    getSimpleName() + "'!");
        }
    }

}
