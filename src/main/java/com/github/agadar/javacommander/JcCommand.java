package com.github.agadar.javacommander;

import java.lang.reflect.Method;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

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
    private final String[] names;

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
    public final HashMap<String, JcOption> optionNamesToOptions = new HashMap<>();

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
     * @throws IllegalArgumentException If one of the parameter values is
     * invalid.
     */
    public JcCommand(String[] names, String description, List<JcOption> options, Method methodToInvoke, Object objectToInvokeOn) {
        if (names == null || names.length < 1 || names[0] == null) {
            throw new IllegalArgumentException("'names' should not be null or empty, and its first value should not be null");
        }
        if (methodToInvoke == null) {
            throw new IllegalArgumentException("'methodToInvoke' should not be null");
        }
        if (objectToInvokeOn == null) {
            throw new IllegalArgumentException("'objectToInvokeOn' should not be null");
        }
        this.names = names;
        this.description = (description == null) ? "" : description;
        this.options = (options == null) ? new ArrayList<>() : options;
        this.methodToInvoke = methodToInvoke;
        this.objectToInvokeOn = objectToInvokeOn;

        // Map all options
        options.stream().forEach((option) -> {
            for (int i = 0; i < option.numberOfNames(); i++) {
                optionNamesToOptions.put(option.getNameByIndex(i), option);
            }
        });
    }

    /**
     * Returns the name at the index, where index is bound between 0 and the
     * number of names.
     *
     * @param index The index of the name to return.
     * @return The name at the index.
     */
    public final String getNameByIndex(int index) {
        return names[Math.max(Math.min(index, names.length - 1), 0)];
    }

    /**
     * Returns the number of names this instance has.
     *
     * @return The number of names this instance has.
     */
    public final int numberOfNames() {
        return names.length;
    }

    /**
     * Convenience method for getting the primary name.
     *
     * @return This command's primary name.
     */
    public final String getPrimaryName() {
        return names[0];
    }

    /**
     * Convenience method for checking whether this command has any options.
     *
     * @return Whether this command has any options.
     */
    public final boolean hasOptions() {
        return options.size() > 0;
    }

    /**
     * Convenience method for checking whether this command has any synonyms.
     *
     * @return whether this command has any synonyms
     */
    public final boolean hasSynonyms() {
        return names.length > 1;
    }

    /**
     * Convenience method for checking whether this command has a description.
     *
     * @return Whether this command has a description.
     */
    public final boolean hasDescription() {
        return !description.isEmpty();
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
