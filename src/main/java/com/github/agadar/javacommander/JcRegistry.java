package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.OptionTranslatorException;
import com.github.agadar.javacommander.exception.OptionAnnotationException;
import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;
import com.github.agadar.javacommander.translator.NoTranslator;
import com.github.agadar.javacommander.translator.OptionTranslator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Allows the registering and unregistering of objects containing functions
 * annotated with @Command and with parameters annotated with @Option.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class JcRegistry {

    /**
     * The parsed commands, each command mapped to its primary name, in
     * alphabetical order.
     */
    private final TreeMap<String, JcCommand> commandsToPrimaryNames = new TreeMap<>();

    /**
     * The parsed commands, each command mapped to each of its names, in
     * alphabetical order.
     */
    private final TreeMap<String, JcCommand> commandsToAllNames = new TreeMap<>();

    /**
     * Registers all commands found in the supplied object. Any commands of
     * which the name is already registered will override the old values.
     *
     * @param object The object containing annotated methods.
     * @throws OptionAnnotationException If a method's parameter is not properly
     * annotated with the @Option annotation.
     * @throws OptionTranslatorException If an option translator failed to parse
     * a default value, or when the translator itself failed to be instantiated.
     */
    public final void registerObject(Object object) throws OptionAnnotationException, OptionTranslatorException {
        if (object == null) {
            throw new IllegalArgumentException("'obj' may not be null");
        }

        // Iterate through the object's methods.
        for (final Method method : object.getClass().getMethods()) {
            // If we've found an annotated method, get to work.
            if (method.isAnnotationPresent(Command.class)) {
                // Obtain the command and derive the required values.
                final Command commandAnnotation = ((Command) method.getAnnotation(Command.class));
                final String[] names = commandAnnotation.names().length > 0 ? commandAnnotation.names() : new String[]{method.getName()};
                final String description = commandAnnotation.description();
                final ArrayList<JcOption> options = parseOptions(commandAnnotation, method);

                // Create new JcCommand object and register it.
                registerCommand(new JcCommand(names, description, options, method, object));
            }
        }
    }

    /**
     * Unregisters all commands found in the supplied object.
     *
     * @param object The object whose commands to unregister.
     */
    public final void unregisterObject(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("'obj' may not be null");
        }

        // Keys which are to be removed from the maps
        final ArrayList<String> keysToRemove = new ArrayList<>();

        // Collect keys to remove from commandToPrimaryName
        commandsToPrimaryNames.entrySet().stream().filter((entry) -> (entry.getValue().objectToInvokeOn.equals(object))).forEach((entry) -> {
            keysToRemove.add(entry.getKey());
        });

        // For each iteration, remove the key from commandToPrimaryName and use
        // its synonyms to remove keys from commandToAllNames
        keysToRemove.stream().map((s) -> commandsToPrimaryNames.remove(s)).forEach((command) -> {
            for (String ss : command.names) {
                commandsToAllNames.remove(ss);
            }
        });
    }

    /**
     * Returns the command mapped to the given command name.
     *
     * @param commandName
     * @return An Optional containing the command - or not.
     */
    public final Optional<JcCommand> getCommand(String commandName) {
        final JcCommand jcCommand = commandsToAllNames.get(commandName);
        return jcCommand != null ? Optional.of(jcCommand) : Optional.empty();
    }

    /**
     * Gives all registered JcCommands.
     *
     * @return All registered JcCommands.
     */
    public final Collection<JcCommand> getParsedCommands() {
        return commandsToPrimaryNames.values();
    }

    /**
     * Registers the given command.
     *
     * @param jcCommand The command to register.
     */
    private void registerCommand(JcCommand jcCommand) {
        for (String name : jcCommand.names) {
            commandsToAllNames.put(name, jcCommand);
        }
        commandsToPrimaryNames.put(jcCommand.names[0], jcCommand);
    }

    /**
     * Validates and parses all @Option annotations found in the @Command
     * annotation, or on the method parameters, to an array of JcOptions.
     *
     * @param commandAnnotation The @Command annotation of the method with the
     * @Option annotations to parse.
     * @param method The method with the @Option annotations to parse.
     * @return The parsed values.
     * @throws OptionTranslatorException If an option translator failed to parse
     * a default value, or when the translator itself failed to be instantiated.
     * @throws OptionAnnotationException If a parameter is not properly
     * annotated with the @Option annotation.
     */
    private static ArrayList<JcOption> parseOptions(Command commandAnnotation, Method method)
            throws OptionAnnotationException, OptionTranslatorException {
        final ArrayList<JcOption> jcOptions = new ArrayList<>();
        final Parameter[] parameters = method.getParameters();

        // If the number of options defined in the command annotation is equal to
        // the number of parameters, then we use those.
        if (commandAnnotation.options().length == parameters.length) {
            for (int i = 0; i < parameters.length; i++) {
                jcOptions.add(parseOption(commandAnnotation.options()[i], parameters[i]));
            }
        } // Else, if there is not a single option defined in the command, we use
        // the options annotated on the parameters.
        else if (commandAnnotation.options().length == 0) {
            for (Parameter paramameter : parameters) {
                // If the parameter is properly annotated, parse it.
                if (paramameter.isAnnotationPresent(Option.class)) {
                    jcOptions.add(parseOption(paramameter.getAnnotation(Option.class), paramameter));
                } // If any parameter at all is not properly annotated, throw an exception.
                else {
                    throw new OptionAnnotationException(method);
                }
            }
        } // Else, if there are options defined in the command, but not enough to
        // cover all parameters, then the annotations are wrong. Throw an error.
        else {
            throw new OptionAnnotationException(method);
        }

        // Return the parsed poptions.
        return jcOptions;
    }

    /**
     * Validates and parses a single option annotation to a JcOption.
     *
     * @param optionAnnotation The @Option annotation to parse.
     * @param parameter The annotated or corresponding parameter.
     * @return The parsing result.
     * @throws OptionTranslatorException If the supplied translator failed to
     * parse the default value if it has one, or when the translator itself
     * failed to be instantiated.
     */
    private static JcOption parseOption(Option optionAnnotation, Parameter parameter) throws OptionTranslatorException {
        // Get the option names. If none is assigned, use the parameter name.
        final String[] names = optionAnnotation.names().length > 0 ? optionAnnotation.names() : new String[]{parameter.getName()};

        // Get other fields
        final boolean hasDefaultValue = optionAnnotation.hasDefaultValue();
        final String defaultValueStr = optionAnnotation.defaultValue();
        final String description = optionAnnotation.description();
        final Class type = parameter.getType();
        final Class<? extends OptionTranslator> translatorClass = optionAnnotation.translator();

        // If there is a default value, then try to parse it.
        if (hasDefaultValue) {
            final Object value = parseString(defaultValueStr, translatorClass, parameter.getType());
            return new JcOption(names, description, hasDefaultValue, type, value, translatorClass); // Return parsed POption.

        } // Else, just parse and return a new JcOption.
        else {
            return new JcOption(names, description, hasDefaultValue, type, null, translatorClass);
        }
    }

    /**
     * Parses a string to a type using a translator.
     *
     * @param <T> The type to parse to.
     * @param stringToParse The string to parse.
     * @param translatorType The type of the translator to use for parsing.
     * @param toType The type to parse to.
     * @return The parsed value.
     * @throws OptionTranslatorException If the supplied translator failed to
     * parse the default value, or when the translator itself failed to be
     * instantiated.
     */
    public static <T> T parseString(String stringToParse, Class<? extends OptionTranslator> translatorType, Class<T> toType)
            throws OptionTranslatorException {
        try {
            // If no translator is set, attempt a normal valueOf.
            if (translatorType.equals(NoTranslator.class)) {
                return OptionTranslator.parseString(stringToParse, toType);
            } // If one is set, then attempt using that.
            else {
                return (T) translatorType.newInstance().translateString(stringToParse);
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new OptionTranslatorException("Failed to instantiate translator '" + translatorType.getSimpleName() + "'", ex);
        } catch (NumberFormatException | IndexOutOfBoundsException | ClassCastException ex) {
            throw new OptionTranslatorException("Failed to parse String '" + stringToParse
                    + "' to type '" + toType.getSimpleName() + "' using translator '"
                    + translatorType.getSimpleName() + "'", ex);
        }
    }
}
