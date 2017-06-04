package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages an application's commands.
 *
 * @author Agadar
 */
public final class JavaCommander {

    /**
     * The underlying command registry.
     */
    public final JcRegistry jcRegistry;

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
     * @throws IllegalArgumentException If jcRegistry is null.
     */
    public JavaCommander(JcRegistry jcRegistry) {
        if (jcRegistry == null) {
            throw new IllegalArgumentException("'jcRegistry' may not be null");
        }
        this.jcRegistry = jcRegistry;
    }

    /**
     * Parses a string to a list of argument token lists, and then attempts to
     * find and execute the sequence of commands defined in it.
     *
     * @param string The string to parse and execute the corresponding commands
     * of.
     * @throws JavaCommanderException If something went wrong, containing a
     * cause.
     */
    public final void execute(String string) throws JavaCommanderException {
        this.executeSequence(stringAsArgs(string));
    }

    /**
     * Attempts to find and execute the sequence of commands defined in a list
     * of lists of argument tokens.
     *
     * @param args The list of argument token lists.
     * @throws JavaCommanderException If something went wrong, containing a
     * cause.
     */
    public final void executeSequence(List<List<String>> args) throws JavaCommanderException {

        // Verify argument list.
        if (args == null || args.isEmpty()) {
            throw new JavaCommanderException(new IllegalArgumentException("'args' should not be null or empty"));
        } else {
            // Execute each token list in sequence.
            for (List<String> tokens : args) {
                this.execute(tokens);
            }
        }
    }

    /**
     * Attempts to find and execute the command defined in a list of argument
     * tokens.
     *
     * @param args The list of argument tokens.
     * @throws JavaCommanderException If something went wrong, containing a
     * cause.
     */
    public final void execute(List<String> args) throws JavaCommanderException {
        try {
            // Verify argument list.
            if (args == null || args.isEmpty()) {
                throw new IllegalArgumentException("'args' should not be null or empty");
            }

            // Retrieve correct command.
            final Optional<JcCommand> commandOpt = jcRegistry.getCommand(args.get(0));
            if (!commandOpt.isPresent()) {
                throw new UnknownCommandException(args.get(0));
            }
            final JcCommand command = commandOpt.get();

            // Arguments to be passed to the Method.invoke function.
            final Object[] finalArgs = new Object[command.numberOfOptions()];

            // If there's arguments left to parse to options, let's parse them.
            if (args.size() > 1) {

                // If the first argument is a valid option, use explicit parsing.
                // Otherwise, use implicit parsing.
                if (command.hasOption(args.get(1))) {
                    parseArgumentsExplicit(finalArgs, args, command);
                } else {
                    parseArgumentsImplicit(finalArgs, args, command);
                }
            }

            // For each entry in finalArgs that is still null, check whether there
            // is a default value. If there is, use that. Else, throw an error.
            for (int i = 0; i < finalArgs.length; i++) {
                Object val = finalArgs[i];

                if (val == null) {
                    JcOption option = command.getOptionByIndex(i).get();

                    if (option.hasDefaultValue) {
                        finalArgs[i] = option.defaultValue;
                    } else {
                        throw new NoValueForOptionException(command, option);
                    }
                }
            }
            // Finally, invoke the method on the object
            command.invoke(finalArgs);
        } catch (UnknownCommandException | UnknownOptionException | OptionTranslatorException | NoValueForOptionException | CommandInvocationException | IllegalArgumentException ex) {
            throw new JavaCommanderException(ex);
        }
    }

    /**
     * Registers all annotated, public, non-static methods of the supplied
     * object.
     *
     * @param object The object containing annotated methods.
     * @throws JavaCommanderException If something went wrong, containing a
     * cause.
     */
    public final void registerObject(Object object) throws JavaCommanderException {
        try {
            jcRegistry.registerObject(object);
        } catch (OptionAnnotationException | OptionTranslatorException | IllegalArgumentException ex) {
            throw new JavaCommanderException(ex);
        }
    }

    /**
     * Registers all annotated, public, static methods of the supplied class.
     *
     * @param clazz The class containing annotated methods.
     * @throws JavaCommanderException If something went wrong, containing a
     * cause.
     */
    public final void registerClass(Class clazz) throws JavaCommanderException {
        try {
            jcRegistry.registerClass(clazz);
        } catch (OptionAnnotationException | OptionTranslatorException | IllegalArgumentException ex) {
            throw new JavaCommanderException(ex);
        }
    }

    /**
     * Unregisters all annotated, non-static methods of the supplied object.
     *
     * @param object The object whose annotated methods to unregister.
     */
    public final void unregisterObject(Object object) {
        jcRegistry.unregisterObject(object);
    }

    /**
     * Unregisters all annotated, static methods of the supplied class.
     *
     * @param clazz The class whose annotated methods to unregister.
     */
    public final void unregisterClass(Class clazz) {
        jcRegistry.unregisterClass(clazz);
    }

    /**
     * Implicitly parses the argument tokens, i.e. no option names are given,
     * only option values. Example: 'commandName 5 true 120'.
     *
     * @param finalArgs The parsed list of arguments, to be filled by this
     * function.
     * @param args The argument tokens to parse.
     * @param command The command to parse the tokens for.
     * @throws OptionTranslatorException If an argument token could not be
     * parsed to its corresponding parameter type.
     * @throws IllegalArgumentException If args contains more argument tokens
     * than should be parsed.
     */
    private static void parseArgumentsImplicit(Object[] finalArgs, List<String> args,
            JcCommand command) throws OptionTranslatorException {

        // If too many arguments were supplied, throw an error.
        if (args.size() - 1 > finalArgs.length) {
            throw new IllegalArgumentException("Too many arguments supplied for this command");
        }

        // Now simply iterate over the arguments, parsing them and placing
        // them in finalArgs as we go.
        for (int i = 1; i < args.size(); i++) {
            final JcOption currentOption = command.getOptionByIndex(i - 1).get();
            final Object parsedArg = currentOption.translate(args.get(i));
            finalArgs[i - 1] = parsedArg;
        }
    }

    /**
     * Explicitly parses the argument list, i.e. option names are given.
     * Example: 'commandName -amount 5 -add true -times 120'.
     *
     * @param finalArgs The parsed list of arguments, to be filled by this
     * function.
     * @param args The argument tokens to parse.
     * @param command The command to parse the tokens for.
     * @throws UnknownOptionException If an option was supplied for the command
     * that it does not have.
     * @throws NoValueForOptionException If an option without a default value
     * was not supplied a value.
     * @throws OptionTranslatorException If an argument token could not be
     * parsed to its corresponding parameter type.
     */
    private static void parseArgumentsExplicit(Object[] finalArgs, List<String> args, JcCommand command)
            throws UnknownOptionException, OptionTranslatorException, NoValueForOptionException {
        Optional<JcOption> currentOption = command.getOptionByName(args.get(1));
        boolean parsingOption = false;

        // Iterate over all the arguments.
        for (int i = 2; i < args.size(); i++) {

            // If we're not currently finding a value for an option, then
            // try find the option. Else, try to parse the value.                
            if (parsingOption) {
                currentOption = command.getOptionByName(args.get(i));

                // If the option was not found, throw an exception.
                if (!currentOption.isPresent()) {
                    throw new UnknownOptionException(command, args.get(i));
                }
            } else {
                final Object parsedArg = currentOption.get().translate(args.get(i));
                finalArgs[command.indexOfOption(currentOption.get())] = parsedArg;
                currentOption = null;
            }
            parsingOption = !parsingOption;
        }

        // If the last parameter was not given a value, throw an error.
        if (currentOption != null) {
            throw new NoValueForOptionException(command, currentOption.get());
        }
    }

    /**
     * Parses a string to a list of argument tokens.
     *
     * @param string A string to parse to a list of argument tokens.
     * @return A list of argument tokens.
     */
    private static ArrayList<List<String>> stringAsArgs(String string) {
        final ArrayList<List<String>> tokenLists = new ArrayList<>();  // list of token lists

        // Validate string parameter.
        if (string == null) {
            return tokenLists;
        }
        string = string.trim();
        if (string.length() < 1) {
            return tokenLists;
        }

        ArrayList<String> curTokens = new ArrayList<>();        // current token list
        final StringBuilder lastToken = new StringBuilder();   // current token
        boolean insideQuote = false;    // are we currently within quotes?
        boolean escapeNextChar = false; // must we escape the current char?

        // Iterate over all chars in the string
        for (char c : string.toCharArray()) {
            // If we are to escape the next char, then append it to the token
            // and turn off the escape option.
            if (escapeNextChar) {
                lastToken.append(c);
                escapeNextChar = false;
            } // Else if the character is a quote mark, then toggle insideQuote.
            else if (c == '"' || c == '\'') {
                insideQuote = !insideQuote;
            } // Else if the character is the escape character, then set escapeNextChar on.
            else if (c == '\\') {
                escapeNextChar = true;
            } // Else if the character is a space, '=' or ':'...
            else if (c == ' ' || c == '=' || c == ':') {
                // ...and we're not in a quote...
                if (!insideQuote) {
                    // ...and the current token is at least 1 character long,
                    // then the current token is finished.
                    if (lastToken.length() > 0) {
                        curTokens.add(lastToken.toString());
                        lastToken.delete(0, lastToken.length());
                    }
                } // ...and we're in a quote, then append it to the token.
                else {
                    lastToken.append(c);
                }
            } // Else if the character is ';', then that means we're going for a new token list. 
            else if (c == ';') {
                if (lastToken.length() > 0) {
                    curTokens.add(lastToken.toString());
                    lastToken.delete(0, lastToken.length());
                }
                if (curTokens.size() > 0) {
                    tokenLists.add(curTokens);
                    curTokens = new ArrayList<>();
                }
            } // Else, append to the token.
            else {
                lastToken.append(c);
            }
        }
        // Add the last token to the list and then return the list.
        if (lastToken.length() > 0) {
            curTokens.add(lastToken.toString());
        }
        if (curTokens.size() > 0) {
            tokenLists.add(curTokens);
        }
        return tokenLists;
    }
}
