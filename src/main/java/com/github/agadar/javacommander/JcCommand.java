package com.github.agadar.javacommander;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * A command parsed from a Command annotation.
 *
 * @author Agadar
 */
public final class JcCommand {

    /**
     * Names of the command. The first entry is the primary name. The other
     * entries are synonyms.
     */
    public final String[] names;

    /**
     * A description of the command.
     */
    public final String description;

    /**
     * This command's options, in order of the method's parameters
     */
    public final List<JcOption> options;

    /**
     * This command's options, each option mapped to each of its names.
     */
    public final HashMap<String, JcOption> optionNamesToOptions;

    /**
     * The method to invoke when this command is executed.
     */
    public final Method methodToInvoke;

    /**
     * The object to invoke the above method on.
     */
    public final Object objectToInvokeOn;

    /**
     * Constructor.
     *
     * @param names Names of the command. The first entry is the primary name.
     * The other entries are synonyms.
     * @param description A description of the command.
     * @param options This command's options, in order of the method's
     * parameters.
     * @param methodToInvoke The method to invoke when this command is executed.
     * @param objectToInvokeOn The object to invoke the above method on.
     */
    public JcCommand(String[] names, String description, List<JcOption> options,
            Method methodToInvoke, Object objectToInvokeOn) {
        this.names = names;
        this.description = description;
        this.options = options;
        this.methodToInvoke = methodToInvoke;
        this.objectToInvokeOn = objectToInvokeOn;
        this.optionNamesToOptions = new HashMap<>();

        // Map all options
        options.stream().forEach((option) -> {
            for (String name : option.names) {
                optionNamesToOptions.put(name, option);
            }
        });
    }

    /**
     * Convenience method for getting the primary name. If this command is the
     * master command (i.e. it has no names) then the value is empty.
     *
     * @return This command's primary name.
     */
    public final Optional<String> getPrimaryName() {
        if (names.length > 1) {
            return Optional.of(names[0]);
        }
        return Optional.empty();
    }

    /**
     * Convenience method for checking whether this command has any options.
     *
     * @return Whether this command has any options.
     */
    public final boolean hasOptions() {
        return options != null && options.size() > 0;
    }

    /**
     * Convenience method for checking whether this command has any synonyms.
     *
     * @return whether this command has any synonyms
     */
    public final boolean hasSynonyms() {
        return names != null && names.length > 1;
    }

    /**
     * Convenience method for checking whether this command has a description.
     *
     * @return Whether this command has a description.
     */
    public final boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * Returns whether this command has an option with the specified name.
     *
     * @param optionName The option name to check.
     * @return Whether this command has an option with the specified name.
     */
    public final boolean hasOption(String optionName) {
        return optionNamesToOptions.containsKey(optionName);
    }
}
