package com.github.agadar.javacommander;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

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

    public JcRegistry(CommandAnnotationParser commandAnnotationParser) {
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
    public void registerObject(@NonNull Object object) throws OptionAnnotationException, OptionValueParserException {
        var parsedCommands = commandAnnotationParser.parseFromObject(object);
        registerCommands(parsedCommands);
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
    public void registerClass(@NonNull Class<?> clazz) throws OptionAnnotationException, OptionValueParserException {
        var parsedCommands = commandAnnotationParser.parseFromClass(clazz);
        registerCommands(parsedCommands);
    }

    /**
     * Unregisters all annotated, non-static methods of the supplied object.
     *
     * @param object The object whose annotated methods to unregister.
     */
    public void unregisterObject(Object object) {
        if (object == null) {
            return;
        }

        // Keys which are to be removed from the maps
        var keysToRemove = new ArrayList<String>();

        // Collect keys to remove from commandToPrimaryName
        primaryNamesToCommands.entrySet().stream()
                .filter((entry) -> (entry.getValue().isMyObject(object)))
                .forEach((entry) -> {
                    keysToRemove.add(entry.getKey());
                });

        // For each iteration, remove the key from commandToPrimaryName and use
        // its synonyms to remove keys from commandToAllNames
        keysToRemove.stream()
                .map((s) -> primaryNamesToCommands.remove(s))
                .forEach((command) -> {
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
    public void unregisterClass(Class<?> clazz) {
        unregisterObject(clazz);
    }

    /**
     * Returns the command mapped to the given command name.
     *
     * @param commandName The name of the command to find.
     * @return An Optional containing the command - or not.
     */
    public Optional<JcCommand> getCommand(String commandName) {
        var jcCommand = allNamesToCommands.get(commandName);
        return jcCommand != null ? Optional.of(jcCommand) : Optional.empty();
    }

    /**
     * Gives all registered JcCommands.
     *
     * @return All registered JcCommands.
     */
    public Collection<JcCommand> getParsedCommands() {
        return Collections.unmodifiableCollection(primaryNamesToCommands.values());
    }

    /**
     * Returns whether or not a command name is known by this registry.
     *
     * @param commandName The command name to check.
     * @return Whether the command name is known by this registry.
     */
    public boolean hasCommand(String commandName) {
        return allNamesToCommands.containsKey(commandName);
    }

    /**
     * Registers the given commands.
     *
     * @param jcCommands The commands to register.
     */
    private void registerCommands(Collection<JcCommand> jcCommands) {
        for (var jcCommand : jcCommands) {
            for (int i = 0; i < jcCommand.numberOfNames(); i++) {
                allNamesToCommands.put(jcCommand.getNameByIndex(i), jcCommand);
            }
            primaryNamesToCommands.put(jcCommand.getPrimaryName(), jcCommand);
        }
    }
}
