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
    private final String[] names;

    /**
     * This command's options, in order of the method's parameters
     */
    private final JcOption[] options;

    /**
     * This command's options, each option mapped to each of its names.
     */
    private final HashMap<String, JcOption> optionNamesToOptions = new HashMap<>();

    /**
     * A description of the command.
     */
    public final String description;

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
        this.options = (options == null) ? new JcOption[0] : options.toArray(new JcOption[options.size()]);
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
    public JcCommand(List<String> names, String description, List<JcOption> options, Method methodToInvoke, Object objectToInvokeOn) {
        this(names == null ? null : names.toArray(new String[names.size()]), description, options, methodToInvoke, objectToInvokeOn);
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
        return options.length > 0;
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

    /**
     * Returns the option at the index, where index is bound between 0 and the
     * number of names.
     *
     * @param index The index of the option to return.
     * @return The option at the index.
     */
    public final Optional<JcOption> getOptionByIndex(int index) {
        if (options.length < 1) {
            return Optional.empty();
        }
        return Optional.of(options[Math.max(Math.min(index, options.length - 1), 0)]);
    }

    /**
     * Gets the option that has the supplied name.
     *
     * @param optionName The name of the option to find.
     * @return The supplied option.
     */
    public final Optional<JcOption> getOptionByName(String optionName) {
        if (optionNamesToOptions.containsKey(optionName)) {
            return Optional.of(optionNamesToOptions.get(optionName));
        }
        return Optional.empty();
    }

    /**
     * Returns the number of names this instance has.
     *
     * @return The number of names this instance has.
     */
    public final int numberOfOptions() {
        return options.length;
    }

    /**
     * Gets the index of the supplied JcOption.
     *
     * @param option The option of which the index to get.
     * @return The index of the supplied JcOption, or -1 if it is not found or
     * null.
     */
    public final int indexOfOption(JcOption option) {
        if (option == null) {
            return -1;
        }
        for (int i = 0; i < options.length; i++) {
            if (option.equals(options[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.getPrimaryName().hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return (this.getPrimaryName().equals(((JcCommand) obj).getPrimaryName()));
    }

}
