package com.github.agadar.javacommander;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.github.agadar.javacommander.annotation.parser.CommandAnnotationParser;
import com.github.agadar.javacommander.exception.OptionAnnotationException;
import com.github.agadar.javacommander.exception.OptionValueParserException;

import lombok.NonNull;

/**
 * Allows the registering and unregistering of objects containing functions
 * annotated with @Command and with parameters annotated with @Option.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class JcRegistry {

    /**
     * The parsed commands, each command mapped to its primary name, in alphabetical
     * order.
     */
    private final Map<String, JcCommand> primaryNamesToCommands = new TreeMap<>();

    /**
     * The parsed commands, each command mapped to each of its names, in
     * alphabetical order.
     */
    private final Map<String, JcCommand> allNamesToCommands = new TreeMap<>();
    private final CommandAnnotationParser commandAnnotationParser;

    public JcRegistry() {
        this(new CommandAnnotationParser());
    }

    public JcRegistry(@NonNull CommandAnnotationParser commandAnnotationParser) {
        this.commandAnnotationParser = commandAnnotationParser;
    }

    /**
     * Registers all annotated, public, non-static methods of the supplied object.
     *
     * @param object The object containing annotated methods.
     * @throws OptionAnnotationException  If a method's parameter is not properly
     *                                    annotated with the @Option annotation.
     * @throws OptionValueParserException If an option value parser failed to parse
     *                                    a default value, or when the parser itself
     *                                    failed to be instantiated.
     */
    public void registerFromObject(@NonNull Object object)
            throws OptionAnnotationException, OptionValueParserException {
        var parsedCommands = commandAnnotationParser.parseFromObject(object);
        registerDirectly(parsedCommands);
    }

    /**
     * Registers all annotated, public, static methods of the supplied class.
     *
     * @param clazz The class containing annotated methods.
     * @throws OptionAnnotationException  If a method's parameter is not properly
     *                                    annotated with the @Option annotation.
     * @throws OptionValueParserException If an option value parser failed to parse
     *                                    a default value, or when the parser itself
     *                                    failed to be instantiated.
     */
    public void registerFromClass(@NonNull Class<?> clazz)
            throws OptionAnnotationException, OptionValueParserException {
        var parsedCommands = commandAnnotationParser.parseFromClass(clazz);
        registerDirectly(parsedCommands);
    }

    /**
     * Registers commands directly instead of reading them from annotated objects or
     * classes.
     * 
     * @param jcCommands The commands to register directly.
     */
    public void registerDirectly(@NonNull Collection<JcCommand> jcCommands) {
        for (var jcCommand : jcCommands) {
            for (String name : jcCommand.getNames()) {
                allNamesToCommands.put(name, jcCommand);
            }
            primaryNamesToCommands.put(jcCommand.getPrimaryName(), jcCommand);
        }
    }

    /**
     * Unregisters all annotated, non-static methods of the supplied object.
     *
     * @param object The object whose annotated methods to unregister.
     */
    public void unregisterFromObject(@NonNull Object object) {
        var commandsToRemove = primaryNamesToCommands.values().stream()
                .filter((command) -> command.isMyObject(object))
                .collect(Collectors.toList());
        commandsToRemove.forEach(command -> unregisterDirectly(command));
    }

    /**
     * Unregisters all annotated, static methods of the supplied class.
     *
     * @param clazz The class whose annotated methods to unregister.
     */
    public void unregisterFromClass(@NonNull Class<?> clazz) {
        unregisterFromObject(clazz);
    }

    /**
     * Unregisters a command directly instead of from an object or class.
     * 
     * @param command The command to unregister directly.
     */
    public void unregisterDirectly(@NonNull JcCommand command) {
        primaryNamesToCommands.remove(command.getPrimaryName());
        for (String name : command.getNames()) {
            allNamesToCommands.remove(name);
        }
    }

    /**
     * Returns the command mapped to the given command name.
     *
     * @param commandName The name of the command to find.
     * @return An Optional containing the command - or not.
     */
    public Optional<JcCommand> getCommand(@NonNull String commandName) {
        var jcCommand = allNamesToCommands.get(commandName);
        return jcCommand != null ? Optional.of(jcCommand) : Optional.empty();
    }

    /**
     * Gets all registered JcCommands.
     *
     * @return All registered JcCommands.
     */
    public Collection<JcCommand> getCommands() {
        return Collections.unmodifiableCollection(primaryNamesToCommands.values());
    }

    /**
     * Returns whether or not a command name is known by this registry.
     *
     * @param commandName The command name to check.
     * @return Whether the command name is known by this registry.
     */
    public boolean hasCommand(@NonNull String commandName) {
        return allNamesToCommands.containsKey(commandName);
    }

}
