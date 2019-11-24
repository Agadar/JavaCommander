package com.github.agadar.javacommander;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.github.agadar.javacommander.exception.JavaCommanderException;
import com.github.agadar.javacommander.exception.UnknownCommandException;
import com.github.agadar.javacommander.misc.ArgumentsParser;
import com.github.agadar.javacommander.misc.Tokenizer;

import lombok.NonNull;

/**
 * Manages an application's commands.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class JavaCommander {

    private final JcRegistry jcRegistry;
    private final ArgumentsParser argumentsParser;
    private final Tokenizer tokenizer;

    public JavaCommander() {
        this(new JcRegistry(), new ArgumentsParser(), new Tokenizer());
    }

    public JavaCommander(@NonNull JcRegistry jcRegistry, @NonNull ArgumentsParser argumentsParser,
            @NonNull Tokenizer tokenizer) {
        this.jcRegistry = jcRegistry;
        this.argumentsParser = argumentsParser;
        this.tokenizer = tokenizer;
    }

    /**
     * Parses a string array to a collection of argument token lists, and then
     * attempts to find and execute the sequence of commands defined in it.
     *
     * @param input The string array to parse and execute the corresponding commands
     *              of.
     * @throws JavaCommanderException If something went wrong, containing a cause.
     */
    public void execute(@NonNull String[] input) throws JavaCommanderException {
        String joinedInput = String.join(" ", input);
        execute(joinedInput);
    }

    /**
     * Parses a string to a collection of argument token lists, and then attempts to
     * find and execute the sequence of commands defined in it.
     *
     * @param input The string to parse and execute the corresponding commands of.
     * @throws JavaCommanderException If something went wrong, containing a cause.
     */
    public void execute(@NonNull String input) throws JavaCommanderException {
        var argumentTokensLists = tokenizer.tokenize(input);
        execute(argumentTokensLists);
    }

    /**
     * Attempts to find and execute the sequence of commands defined in a collection
     * of lists of argument tokens.
     *
     * @param argumentTokensLists The collection of argument token lists.
     * @throws JavaCommanderException If something went wrong, containing a cause.
     */
    public void execute(@NonNull Collection<List<String>> argumentTokensLists) throws JavaCommanderException {
        for (var argumentTokens : argumentTokensLists) {
            execute(argumentTokens);
        }
    }

    /**
     * Attempts to find and execute the command defined in a list of argument
     * tokens.
     *
     * @param argumentTokens The list of argument tokens.
     * @throws JavaCommanderException If something went wrong, containing a cause.
     */
    public void execute(@NonNull List<String> argumentTokens) throws JavaCommanderException {
        if (argumentTokens.isEmpty()) {
            throw new IllegalArgumentException("'args' should not be null or empty");
        }
        var commandOpt = jcRegistry.getCommand(argumentTokens.get(0));
        if (!commandOpt.isPresent()) {
            throw new UnknownCommandException(argumentTokens.get(0));
        }
        var command = commandOpt.get();
        var finalArgs = argumentsParser.parseArguments(argumentTokens, command);
        command.invoke(finalArgs);
    }

    /**
     * Registers all annotated, public, non-static methods of the supplied object.
     *
     * @param object The object containing annotated methods.
     * @throws JavaCommanderException If something went wrong, containing a cause.
     */
    public void registerFromObject(@NonNull Object object) throws JavaCommanderException {
        jcRegistry.registerFromObject(object);
    }

    /**
     * Registers all annotated, public, static methods of the supplied class.
     *
     * @param clazz The class containing annotated methods.
     * @throws JavaCommanderException If something went wrong, containing a cause.
     */
    public void registerFromClass(@NonNull Class<?> clazz) throws JavaCommanderException {
        jcRegistry.registerFromClass(clazz);
    }

    /**
     * Registers commands directly instead of reading them from annotated objects or
     * classes.
     * 
     * @param jcCommands The commands to register directly.
     */
    public void registerDirectly(@NonNull Collection<JcCommand> jcCommands) {
        jcRegistry.registerDirectly(jcCommands);
    }

    /**
     * Unregisters all annotated, non-static methods of the supplied object.
     *
     * @param object The object whose annotated methods to unregister.
     */
    public void unregisterFromObject(@NonNull Object object) {
        jcRegistry.unregisterFromObject(object);
    }

    /**
     * Unregisters all annotated, static methods of the supplied class.
     *
     * @param clazz The class whose annotated methods to unregister.
     */
    public void unregisterFromClass(@NonNull Class<?> clazz) {
        jcRegistry.unregisterFromClass(clazz);
    }

    /**
     * Unregisters a command directly instead of from an object or class.
     * 
     * @param command The command to unregister directly.
     */
    public void unregisterDirectly(@NonNull JcCommand command) {
        jcRegistry.unregisterDirectly(command);
    }

    /**
     * Returns the command mapped to the given command name.
     *
     * @param commandName The name of the command to find.
     * @return An Optional containing the command - or not.
     */
    public Optional<JcCommand> getCommand(@NonNull String commandName) {
        return jcRegistry.getCommand(commandName);
    }

    /**
     * Gets all registered JcCommands.
     *
     * @return All registered JcCommands.
     */
    public Collection<JcCommand> getCommands() {
        return jcRegistry.getCommands();
    }
}
