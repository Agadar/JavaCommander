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
import java.util.Optional;

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
     * @throws UnknownCommandException
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
        Optional<JcCommand> command;
        String commandName;

        // Retrieve the command. If no arguments were provided, attempt to get the master command.
        if (args.size() > 0) {
            command = jcRegistry.getCommand(commandName = args.get(0));
        } else {
            command = jcRegistry.getCommand(commandName = MASTER_COMMAND);
            paramsStartingIndex = 0;
        }

        // If the command was not found, throw an exception.
        if (!command.isPresent()) {
            throw new UnknownCommandException(commandName);
        }

        // Determine whether we're using explicit, or implicit, options for this command.
        // Only relevant if there are arguments given, so check args.size().
        JcOption currentOption = null;
        if (args.size() > paramsStartingIndex) {
            final String arg = args.get(paramsStartingIndex);
            currentOption = command.get().optionNamesToOptions.get(arg);
        }

        // Arguments to be passed to the Method.invoke function.
        Object[] finalArgs = new Object[command.get().options.size()];

        // If the retrieved option is null, then use implicit options.
        if (currentOption == null) {
            finalArgs = parseArgumentsImplicit(args, command.get(), paramsStartingIndex);
        } // Else if the retrieved option is not null, then use explicit options.
        else {//////////////////////////////////////////////////////////////////////////////////// <<<<<<<<<<<<<<<<<< Last time.
            for (int i = paramsStartingIndex + 1; i < args.size(); i++) {
                String arg = args.get(i);

                // If we're not currently finding a value for an option, then
                // try find the option.
                if (currentOption == null) {
                    currentOption = command.get().optionNamesToOptions.get(arg);

                    // If the option is not recognized, assume the option name is
                    // implicit and try parse the value to the current parameter.
                    if (currentOption == null) {
                        throw new UnknownOptionException(command.get(), arg);
                    }
                } // Else, try to parse the value.
                else {
                    Object parsedArg = JcRegistry.parseString(arg, currentOption.translator, currentOption.type);
                    finalArgs[command.get().options.indexOf(currentOption)] = parsedArg;
                    currentOption = null;
                }
            }

            // If the last parameter was not given a value, throw an error.
            if (currentOption != null) {
                throw new NoValueForOptionException(command.get(), currentOption);
            }
        }

        // For each entry in finalArgs that is still null, check whether there
        // is a default value. If there is, use that. Else, throw an error.
        for (int i = 0; i < finalArgs.length; i++) {
            Object val = finalArgs[i];

            if (val == null) {
                JcOption option = command.get().options.get(i);

                if (option.hasDefaultValue) {
                    finalArgs[i] = option.defaultValue;
                } else {
                    throw new NoValueForOptionException(command.get(), option);
                }
            }
        }

        try {
            // Finally, invoke the method on the object
            command.get().methodToInvoke.invoke(command.get().objectToInvokeOn, finalArgs);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CommandInvocationException(command.get(), ex);
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
     * option values.
     *
     * @param args
     * @param command
     * @param paramsStartingIndex
     * @return
     * @throws OptionTranslatorException
     */
    private static Object[] parseArgumentsImplicit(List<String> args, JcCommand command, int paramsStartingIndex)
            throws OptionTranslatorException {
        final Object[] finalArgs = new Object[command.options.size()];

        // If too many arguments were supplied, throw an error.
        if (args.size() - paramsStartingIndex > finalArgs.length) {
            throw new IllegalArgumentException("Too many arguments supplied for this command");
        }

        // Now simply iterate over the arguments, parsing them and placing
        // them in finalArgs as we go.
        for (int i = paramsStartingIndex; i < args.size(); i++) {
            final int iminus = i - paramsStartingIndex;
            final JcOption currentOption = command.options.get(iminus);
            final Object parsedArg = JcRegistry.parseString(args.get(i), currentOption.translator, currentOption.type);
            finalArgs[iminus] = parsedArg;
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
