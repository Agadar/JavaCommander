package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.OptionTranslatorException;
import com.github.agadar.javacommander.exception.OptionAnnotationException;
import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;
import com.github.agadar.javacommander.translator.OptionTranslator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
    private final TreeMap<String, JcCommand> primaryNamesToCommands = new TreeMap<>();

    /**
     * The parsed commands, each command mapped to each of its names, in
     * alphabetical order.
     */
    private final TreeMap<String, JcCommand> allNamesToCommands = new TreeMap<>();

    /**
     * Registers all annotated, public, non-static methods of the supplied
     * object.
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
            // If we've found an annotated, non-static method, get to work.
            if (method.isAnnotationPresent(Command.class) && !Modifier.isStatic(method.getModifiers())) {
                // Obtain the command and derive the required values.
                final Command commandAnnotation = ((Command) method.getAnnotation(Command.class));
                final String[] names = commandAnnotation.names().length > 0 ? commandAnnotation.names() : new String[]{method.getName()};
                final String description = commandAnnotation.description();
                final ArrayList<JcOption> options = parseOptions(commandAnnotation, method);

                // Create new JcCommand object and register it.
                registerCommand(new JcCommand(Arrays.asList(names), description, options, method, object));
            }
        }
    }

    /**
     * Registers all annotated, public, static methods of the supplied class.
     *
     * @param clazz The class containing annotated methods.
     * @throws OptionAnnotationException If a method's parameter is not properly
     * annotated with the @Option annotation.
     * @throws OptionTranslatorException If an option translator failed to parse
     * a default value, or when the translator itself failed to be instantiated.
     */
    public final void registerClass(Class clazz) throws OptionAnnotationException, OptionTranslatorException {
        if (clazz == null) {
            throw new IllegalArgumentException("'clazz' may not be null");
        }

        // Iterate through the object's methods.
        for (final Method method : clazz.getMethods()) {
            // If we've found an annotated, static method, then get to work.
            if (method.isAnnotationPresent(Command.class) && Modifier.isStatic(method.getModifiers())) {
                // Obtain the command and derive the required values.
                final Command commandAnnotation = ((Command) method.getAnnotation(Command.class));
                final String[] names = commandAnnotation.names().length > 0 ? commandAnnotation.names() : new String[]{method.getName()};
                final String description = commandAnnotation.description();
                final ArrayList<JcOption> options = parseOptions(commandAnnotation, method);

                // Create new JcCommand object and register it.
                registerCommand(new JcCommand(Arrays.asList(names), description, options, method, clazz));
            }
        }
    }

    /**
     * Unregisters all annotated, non-static methods of the supplied object.
     *
     * @param object The object whose annotated methods to unregister.
     */
    public final void unregisterObject(Object object) {
        if (object == null) {
            return;
        }

        // Keys which are to be removed from the maps
        final ArrayList<String> keysToRemove = new ArrayList<>();

        // Collect keys to remove from commandToPrimaryName
        primaryNamesToCommands.entrySet().stream().filter((entry) -> (entry.getValue().isMyObject(object))).forEach((entry) -> {
            keysToRemove.add(entry.getKey());
        });

        // For each iteration, remove the key from commandToPrimaryName and use
        // its synonyms to remove keys from commandToAllNames
        keysToRemove.stream().map((s) -> primaryNamesToCommands.remove(s)).forEach((command) -> {
            for (int i = 0; i < command.numberOfNames(); i++) {
                allNamesToCommands.remove(command.getNameByIndex(i));
            }
        });
    }

    /**
     * Unregisters all annotated, static methods of the supplied class.
     *
     * @param clazz The class whose annotated methods to unregister.
     */
    public final void unregisterClass(Class clazz) {
        this.unregisterObject(clazz);
    }

    /**
     * Returns the command mapped to the given command name.
     *
     * @param commandName The name of the command to find.
     * @return An Optional containing the command - or not.
     */
    public final Optional<JcCommand> getCommand(String commandName) {
        final JcCommand jcCommand = allNamesToCommands.get(commandName);
        return jcCommand != null ? Optional.of(jcCommand) : Optional.empty();
    }

    /**
     * Gives all registered JcCommands.
     *
     * @return All registered JcCommands.
     */
    public final Collection<JcCommand> getParsedCommands() {
        return Collections.unmodifiableCollection(primaryNamesToCommands.values());
    }

    /**
     * Returns whether or not a command name is known by this registry.
     *
     * @param commandName The command name to check.
     * @return Whether the command name is known by this registry.
     */
    public final boolean hasCommand(String commandName) {
        return allNamesToCommands.containsKey(commandName);
    }

    /**
     * Registers the given command.
     *
     * @param jcCommand The command to register.
     */
    private void registerCommand(JcCommand jcCommand) {
        for (int i = 0; i < jcCommand.numberOfNames(); i++) {
            allNamesToCommands.put(jcCommand.getNameByIndex(i), jcCommand);
        }
        primaryNamesToCommands.put(jcCommand.getPrimaryName(), jcCommand);
    }

    /**
     * Validates and parses all @Option annotations found in the @Command
     * annotation, or on the method parameters, to an array of JcOptions.
     *
     * @param commandAnnotation The @Command annotation of the method with the
     * \@Option annotations to parse.
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
     * @throws OptionTranslatorException If the option translator failed to
     * parse the default value if it has one, or when the translator itself
     * failed to be instantiated.
     */
    private static JcOption parseOption(Option optionAnnotation, Parameter parameter)
            throws OptionTranslatorException {
        // Get the option names. If none is assigned, use the parameter name.
        final String[] names = optionAnnotation.names().length > 0 ? optionAnnotation.names() : new String[]{parameter.getName()};

        // Get other fields.
        final boolean hasDefaultValue = optionAnnotation.hasDefaultValue();
        final String defaultValueStr = optionAnnotation.defaultValue();
        final String description = optionAnnotation.description();
        final Class type = parameter.getType();
        final Class<? extends OptionTranslator> translatorClass = optionAnnotation.translator();
        return new JcOption(Arrays.asList(names), description, hasDefaultValue, type, defaultValueStr, translatorClass);
    }
}
