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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Allows the registering and unregistering of objects containing functions
 * annotated with @command and @option.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class JcRegistry {

    /**
     * The parsed commands, each command mapped to its primary name, in
     * alphabetical order.
     */
    private final Map<String, JcCommand> commandsToPrimaryName = new TreeMap<>();

    /**
     * The parsed commands, each command mapped once for each of its names, in
     * alphabetical order.
     */
    private final Map<String, JcCommand> commandsToAllNames = new TreeMap<>();

    /**
     * Registers all commands found in the supplied Object. Any commands of
     * which the name is already registered will override the old values.
     *
     * @param obj the Object where commands are located within
     * @throws OptionAnnotationException
     * @throws OptionTranslatorException
     */
    public final void registerObject(Object obj) throws OptionAnnotationException, OptionTranslatorException {
        if (obj == null) {
            throw new IllegalArgumentException("'obj' may not be null");
        }

        // iterate through the obj's class' methods
        for (final Method method : obj.getClass().getMethods()) {
            // if we've found an annotated method, get to work.
            if (method.isAnnotationPresent(Command.class)) {
                // Obtain the command and derive the needed values
                Command commandAnnotation = ((Command) method.getAnnotation(
                        Command.class));
                String[] names = commandAnnotation.names();
                String description = commandAnnotation.description();
                List<JcOption> options = parseOptions(commandAnnotation, method);

                // if no names were given, simply use the name of the method
                if (names.length <= 0) {
                    names = new String[]{
                        method.getName()
                    };
                }

                // Create new PCommand object and register it.
                registerCommand(new JcCommand(names, description, options, method, obj));
            }
        }
    }

    /**
     * Unregisters all commands found in the supplied Object.
     *
     * @param obj the object whose commands to unregister
     */
    public final void unregisterObject(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("'obj' may not be null");
        }

        // Keys which are to be removed from the maps
        List<String> keysToRemove = new ArrayList<>();

        // Collect keys to remove from commandToPrimaryName
        commandsToPrimaryName.entrySet().stream().filter((entry) -> (entry.getValue().objectToInvokeOn.equals(obj))).forEach((entry)
                -> {
                    keysToRemove.add(entry.getKey());
                });

        // For each iteration, remove the key from commandToPrimaryName and use
        // its synonyms to remove keys from commandToAllNames
        keysToRemove.stream().map((s) -> commandsToPrimaryName.remove(s)).forEach((command)
                -> {
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
        return Optional.of(commandsToAllNames.get(commandName));
    }

    public final Collection<JcCommand> getParsedCommands() {
        return commandsToPrimaryName.values();
    }

    /**
     * Registers the given command.
     *
     * @param command
     */
    private void registerCommand(JcCommand command) {
        commandsToPrimaryName.put(command.names[0], command);

        for (String name : command.names) {
            commandsToAllNames.put(name, command);
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
    private List<JcOption> parseOptions(Command commandAnnotation, Method method)
            throws OptionAnnotationException, OptionTranslatorException {
        List<JcOption> poptions = new ArrayList<>();
        Parameter[] params = method.getParameters();

        // If the number of options defined in the command annotation is equal to
        // the number of parameters, then we use those.
        if (commandAnnotation.options().length == params.length) {
            // Parse all options
            for (int i = 0; i < params.length; i++) {
                poptions.add(parseOption(commandAnnotation.options()[i], params[i]));
            }
        } // Else, if there is not a single option defined in the command, we use
        // the options annotated on the parameters.
        else if (commandAnnotation.options().length == 0) {
            for (Parameter param : params) {
                // If the parameter is properly annotated, parse it.
                if (param.isAnnotationPresent(Option.class)) {
                    poptions.add(parseOption(param.getAnnotation(Option.class), param));
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
        return poptions;
    }

    /**
     * Validates and parses a single option annotation to a POption.
     *
     * @param optionAnnotation
     * @param param
     * @return
     */
    private JcOption parseOption(Option optionAnnotation, Parameter param) throws OptionTranslatorException {
        // Get the option names. If none is assigned, use the parameter name.
        String[] names = optionAnnotation.names();
        if (names.length <= 0) {
            names = new String[]{
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
        if (hasDefaultValue) {
            Object value = parseValue(defaultValueStr, translatorClass, param.getType());

            // Return parsed POption.
            return new JcOption(names, description, hasDefaultValue, type,
                    value, translatorClass);

        } // Else, just parse and return a new POption.
        else {
            return new JcOption(names, description, hasDefaultValue, type, null,
                    translatorClass);
        }
    }

    /**
     * Attempts to parse the given String value to the given type, using the
     * given translator type.
     *
     * @param <T>
     * @param value
     * @param translatorType
     * @param toType
     * @return
     * @throws
     * com.github.agadar.javacommander.exception.OptionTranslatorException
     * @throws com.github.agadar.javacommander.OptionTranslatorException
     */
    final protected <T> T parseValue(String value, Class<? extends OptionTranslator> translatorType, Class<T> toType)
            throws OptionTranslatorException {
        try {
            // If no translator is set, attempt a normal valueOf.
            if (translatorType.equals(NoTranslator.class)) {
                return OptionTranslator.parseString(value, toType);
            } // If one is set, then attempt using that.
            else {
                OptionTranslator translator = translatorType.newInstance();
                return (T) translator.translateString(value);
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new OptionTranslatorException("Failed to instantiate translator '" + translatorType.getSimpleName() + "'", ex);
        } catch (NumberFormatException | IndexOutOfBoundsException | ClassCastException ex) {
            throw new OptionTranslatorException("Failed to parse String '" + value
                    + "' to type '" + toType.getSimpleName() + "' using translator '"
                    + translatorType.getSimpleName() + "'", ex);
        }
    }
}
