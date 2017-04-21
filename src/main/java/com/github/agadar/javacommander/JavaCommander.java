package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.OptionTranslatorException;
import com.github.agadar.javacommander.exception.OptionAnnotationException;
import com.github.agadar.javacommander.exception.CommandInvocationException;
import com.github.agadar.javacommander.exception.NoValueForOptionException;
import com.github.agadar.javacommander.exception.UnknownOptionException;
import com.github.agadar.javacommander.exception.UnknownCommandException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages an application's commands.
 *
 * @author Agadar
 */
public class JavaCommander implements Runnable {

    /**
     * The underlying command registry.
     */
    public final JcRegistry jcRegistry;

    /**
     * A special command name that can be used for a single command. If no known
     * command name can be found in a given string input, then an attempt to
     * call the master command is made, using said input.
     */
    public static final String EMPTY_COMMAND = "";

    public JavaCommander() {
        this(new JcRegistry());
    }

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
     * @param string the string to parse
     * @throws UnknownCommandException
     * @throws UnknownOptionException
     * @throws NoValueForOptionException
     * @throws CommandInvocationException
     * @throws OptionTranslatorException
     */
    public final void execute(String string)
            throws UnknownCommandException, UnknownOptionException, NoValueForOptionException,
            CommandInvocationException, OptionTranslatorException {
        if (string == null) {
            throw new IllegalArgumentException("'string' may not be null");
        }
        this.execute(stringAsArgs(string));
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
    public final void execute(List<String> args)
            throws UnknownCommandException, UnknownOptionException, NoValueForOptionException, CommandInvocationException, OptionTranslatorException {
        if (args == null || args.isEmpty()) {
            throw new IllegalArgumentException("'args' may not be null");
        }

        // What index in args to start reading parameters from. If this is a
        // normal command, then the parameters start at index 1, as index 0
        // is the command name. If this is the master command, then the
        // parameters start at index 0, as there is no command name.
        int paramsStartingIndex = 1;

        // Retrieve the command. If none was found, attempt to get the master command.
        Optional<JcCommand> command = jcRegistry.getCommand(args.get(0));

        if (!command.isPresent()) {
            // If the master command was also not found, then throw an error.
            command = jcRegistry.getCommand(EMPTY_COMMAND);

            if (!command.isPresent()) {
                throw new UnknownCommandException(args.get(0));
            }
            paramsStartingIndex = 0;
        }

        // Determine whether we're using explicit, or implicit, options for this command.
        // Only relevant if there are arguments given, so check args.size().
        JcOption currentOption = null;
        if (args.size() > paramsStartingIndex) {
            String arg = args.get(paramsStartingIndex);
            currentOption = command.get().optionNamesToOptions.get(arg);
        }

        // Arguments to be passed to the Method.invoke function.
        Object[] finalArgs = new Object[command.get().options.size()];

        // If the retrieved option is null, then use implicit options.
        if (currentOption == null) {
            // If too many arguments were supplied, throw an error.
            if (args.size() - paramsStartingIndex > finalArgs.length) {
                throw new IllegalArgumentException("Too many arguments supplied for this command");
            }

            // Now simply iterate over the arguments, parsing them and placing
            // them in finalArgs as we go.
            for (int i = paramsStartingIndex; i < args.size(); i++) {
                int iminus = i - paramsStartingIndex;
                currentOption = command.get().options.get(iminus);
                Object parsedArg = jcRegistry.parseString(args.get(i), currentOption.translator, currentOption.type);
                finalArgs[iminus] = parsedArg;
            }
        } // Else if the retrieved option is not null, then use explicit options.
        else {
            for (int i = paramsStartingIndex + 1; i < args.size(); i++) {
                String arg = args.get(i);

                // If we're not currently finding a value for an option, then
                // try find the option.
                if (currentOption == null) {
                    currentOption = command.get().optionNamesToOptions.get(arg);

                    // If the option is not recognized, assume the option name is
                    // implicit and try parse the value to the current parameter.
                    if (currentOption == null) {
                        throw new UnknownOptionException(args.get(0), arg);
                    }
                } // Else, try to parse the value.
                else {
                    Object parsedArg = jcRegistry.parseString(arg, currentOption.translator, currentOption.type);
                    finalArgs[command.get().options.indexOf(currentOption)] = parsedArg;
                    currentOption = null;
                }
            }

            // If the last parameter was not given a value, throw an error.
            if (currentOption != null) {
                throw new NoValueForOptionException(args.get(0), currentOption.getPrimaryName());
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
                    throw new NoValueForOptionException(args.get(0), option.getPrimaryName());
                }
            }
        }

        try {
            // Finally, invoke the method on the object
            command.get().methodToInvoke.invoke(command.get().objectToInvokeOn, finalArgs);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CommandInvocationException(args.get(0), ex);
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
     * Blocking method that continuously reads input from a BufferedReader.
     * JavaCommanderExceptions are printed, but don't stop the loop.
     */
    @Override
    public final void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Thread loop.
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String command = br.readLine();
                execute(command);
            } catch (IOException | UnknownCommandException | UnknownOptionException |
                    NoValueForOptionException | CommandInvocationException ex) {
                System.out.println(ex.getMessage());
            } catch (OptionTranslatorException ex) {
                Logger.getLogger(JavaCommander.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
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
    private ArrayList<String> stringAsArgs(String string) {
        string = string.trim();
        ArrayList<String> tokens = new ArrayList<>();        // the token list to be returned
        StringBuilder curToken = new StringBuilder();   // current token
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
