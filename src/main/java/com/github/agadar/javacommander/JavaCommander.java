package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.OptionTranslatorException;
import com.github.agadar.javacommander.exception.OptionAnnotationException;
import com.github.agadar.javacommander.exception.CommandInvocationException;
import com.github.agadar.javacommander.exception.NoValueForOptionException;
import com.github.agadar.javacommander.exception.UnknownOptionException;
import com.github.agadar.javacommander.exception.UnknownCommandException;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages an application's commands.
 *
 * @author Agadar
 */
public class JavaCommander {

    /**
     * The underlying command registry.
     */
    public final JcRegistry jcRegistry;

    /**
     * A special command name that can be used for a single command. If no known
     * command name can be found in a given string input, then an attempt to
     * call the master command is made, using said input.
     */
    public static final String MASTER_COMMAND = "";

    /**
     * Constructor. Assigns a new JcRegistry to this.
     */
    public JavaCommander() {
        this(new JcRegistry());
    }

    /**
     * Constructor.
     *
     * @param jcRegistry The JcRegistry instance to use.
     */
    public JavaCommander(JcRegistry jcRegistry) {
        if (jcRegistry == null) {
            throw new IllegalArgumentException("'jcRegistry' may not be null");
        }
        this.jcRegistry = jcRegistry;
    }

    /**
     * Parses a string to a list of argument tokens, and then attempts to find
     * and execute the command defined in it.
     *
     * @param string The string to parse and execute the corresponding command
     * of.
     * @throws UnknownCommandException
     * @throws UnknownOptionException
     * @throws NoValueForOptionException
     * @throws CommandInvocationException
     * @throws OptionTranslatorException
     */
    public final void execute(String string) throws UnknownCommandException, UnknownOptionException,
            NoValueForOptionException, CommandInvocationException, OptionTranslatorException {
        this.execute(stringAsArgs(string != null ? string : ""));
    }

    /**
     * Attempts to find and execute the command defined in a list of argument
     * tokens.
     *
     * @param args the collection of argument tokens
     * @throws UnknownCommandException If the command was not found.
     * @throws UnknownOptionException
     * @throws NoValueForOptionException
     * @throws CommandInvocationException
     * @throws OptionTranslatorException
     */
    public final void execute(List<String> args) throws UnknownCommandException, UnknownOptionException,
            NoValueForOptionException, CommandInvocationException, OptionTranslatorException {
        if (args == null) {
            args = new ArrayList<>();
        }

        // What index in args to start reading parameters from. If this is a
        // normal command, then the parameters start at index 1, as index 0
        // is the command name. If this is the master command, then the
        // parameters start at index 0, as there is no command name.
        int paramsStartingIndex = 1;
        JcCommand command;

        // Determine and retrieve the command to use.
        if (args.size() > 0) {
            if (jcRegistry.hasCommand(args.get(0))) {
                command = jcRegistry.getCommand(args.get(0)).get();
            }
            if (jcRegistry.hasCommand(MASTER_COMMAND)) {
                command = jcRegistry.getCommand(MASTER_COMMAND).get();
                paramsStartingIndex = 0;
            } else {
                throw new UnknownCommandException(args.get(0));
            }
        } else if (jcRegistry.hasCommand(MASTER_COMMAND)) {
            command = jcRegistry.getCommand(MASTER_COMMAND).get();
            paramsStartingIndex = 0;
        } else {
            throw new UnknownCommandException(MASTER_COMMAND);
        }

        // Arguments to be passed to the Method.invoke function.
        final Object[] finalArgs = new Object[command.options.size()];

        // If there's arguments left to parse to options, let's parse them.
        if (args.size() > paramsStartingIndex) {

            // If the first argument is a valid option, use explicit parsing.
            // Otherwise, use implicit parsing.
            if (command.hasOption(args.get(paramsStartingIndex))) {
                parseArgumentsExplicit(finalArgs, args, command, paramsStartingIndex);
            } else {
                parseArgumentsImplicit(finalArgs, args, command, paramsStartingIndex);
            }
        }

        // For each entry in finalArgs that is still null, check whether there
        // is a default value. If there is, use that. Else, throw an error.
        for (int i = 0; i < finalArgs.length; i++) {
            Object val = finalArgs[i];

            if (val == null) {
                JcOption option = command.options.get(i);

                if (option.hasDefaultValue) {
                    finalArgs[i] = option.defaultValue;
                } else {
                    throw new NoValueForOptionException(command, option);
                }
            }
        }

        try {
            // Finally, invoke the method on the object
            command.methodToInvoke.invoke(command.objectToInvokeOn, finalArgs);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CommandInvocationException(command, ex);
        }

    }

    /**
     * Registers all commands found in the supplied Object. Any commands of
     * which the name is already registered will override the old values.
     *
     * @param obj the Object where commands are located within
     * @throws OptionAnnotationException
     * @throws OptionTranslatorException
     */
    public final void registerObject(Object obj) throws OptionAnnotationException, OptionTranslatorException {
        jcRegistry.registerObject(obj);
    }

    /**
     * Unregisters all commands found in the supplied Object.
     *
     * @param obj the object whose commands to unregister
     */
    public final void unregisterObject(Object obj) {
        jcRegistry.unregisterObject(obj);
    }

    /**
     * Implicitly parses the argument list, i.e. no option names are given, only
     * option values. Example: 'commandName 5 true 120'.
     *
     * @param args
     * @param command
     * @param paramsStartingIndex
     * @return
     * @throws OptionTranslatorException
     * @throws IllegalArgumentException
     */
    private static Object[] parseArgumentsImplicit(Object[] finalArgs, List<String> args, JcCommand command, int paramsStartingIndex)
            throws OptionTranslatorException {

        // If too many arguments were supplied, throw an error.
        if (args.size() - paramsStartingIndex > finalArgs.length) {
            throw new IllegalArgumentException("Too many arguments supplied for this command");
        }

        // Now simply iterate over the arguments, parsing them and placing
        // them in finalArgs as we go.
        if (paramsStartingIndex == 1) {
            for (int i = 1; i < args.size(); i++) {
                final JcOption currentOption = command.options.get(i - 1);
                final Object parsedArg = JcRegistry.parseString(args.get(i), currentOption.translator, currentOption.type);
                finalArgs[i - 1] = parsedArg;
            }
        } else {
            for (int i = 0; i < args.size(); i++) {
                final JcOption currentOption = command.options.get(i);
                final Object parsedArg = JcRegistry.parseString(args.get(i), currentOption.translator, currentOption.type);
                finalArgs[i] = parsedArg;
            }
        }
        return finalArgs;
    }

    /**
     * Explicitly parses the argument list, i.e. option names are given.
     * Example: 'commandName -amount 5 -add true -times 120'.
     *
     * @param args
     * @param command
     * @param paramsStartingIndex
     * @return
     */
    private static Object[] parseArgumentsExplicit(Object[] finalArgs, List<String> args, JcCommand command, int paramsStartingIndex) {
        JcOption currentOption = command.optionNamesToOptions.get(args.get(paramsStartingIndex));
        boolean parsingOption = false;

        // Iterate over all the arguments.
        for (int i = paramsStartingIndex + 1; i < args.size(); i++) {

            // If we're not currently finding a value for an option, then
            // try find the option. Else, try to parse the value.                
            if (parsingOption) {
                currentOption = command.optionNamesToOptions.get(args.get(i));

                // If the option was not found, throw an exception.
                if (currentOption == null) {
                    throw new UnknownOptionException(command, args.get(i));
                }
            } else {
                final Object parsedArg = JcRegistry.parseString(args.get(i), currentOption.translator, currentOption.type);
                finalArgs[command.options.indexOf(currentOption)] = parsedArg;
                currentOption = null;
            }
            parsingOption = !parsingOption;
        }

        // If the last parameter was not given a value, throw an error.
        if (currentOption != null) {
            throw new NoValueForOptionException(command, currentOption);
        }
        return finalArgs;
    }

    /**
     * Parses a string to a list of argument tokens.
     *
     * @param string a string to parse to a list of argument tokens
     * @return a list of argument tokens
     */
    private static ArrayList<String> stringAsArgs(String string) {
        string = string.trim();
        final ArrayList<String> tokens = new ArrayList<>();        // the token list to be returned
        final StringBuilder curToken = new StringBuilder();   // current token
        boolean insideQuote = false;    // are we currently within quotes?
        boolean escapeNextChar = false; // must we escape the current char?

        // Iterate over all chars in the string
        for (char c : string.toCharArray()) {
            // If we are to escape the next char, then append it to the token
            // and turn off the escape option.
            if (escapeNextChar) {
                curToken.append(c);
                escapeNextChar = false;
            } // Else if the character is a quote mark, then toggle insideQuote.
            else if (c == '"' || c == '\'') {
                insideQuote = !insideQuote;
            } // Else if the character is the escape character, then set escapeNextChar on.
            else if (c == '\\') {
                escapeNextChar = true;
            } // Else if the character is a space...
            else if (c == ' ') {
                // ...and we're not in a quote...
                if (!insideQuote) {
                    // ...and the current token is at least 1 character long,
                    // then the current token is finished.
                    if (curToken.length() > 0) {
                        tokens.add(curToken.toString());
                        curToken.delete(0, curToken.length());
                    }
                } // ...and we're in a quote, then append it to the token.
                else {
                    curToken.append(c);
                }
            } // Else, append to the token.
            else {
                curToken.append(c);
            }
        }
        // Add the last token to the list and then return the list.
        tokens.add(curToken.toString());
        return tokens;
    }

}
