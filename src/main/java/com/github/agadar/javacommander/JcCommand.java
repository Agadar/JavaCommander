package com.github.agadar.javacommander;

import com.github.agadar.javacommander.exception.CommandInvocationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final List<String> names;

    /**
     * This command's options, in order of the method's parameters
     */
    private final List<JcOption> options;

    /**
     * This command's options, each option mapped to each of its names.
     */
    private final HashMap<String, JcOption> optionNamesToOptions = new HashMap<>();

    /**
     * The method to invoke when this command is executed.
     */
    private final Method methodToInvoke;

    /**
     * The object to invoke the above method on.
     */
    private final Object objectToInvokeOn;

    /**
     * A description of the command.
     */
    public final String description;

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
    public JcCommand(List<String> names, String description, List<JcOption> options, Method methodToInvoke, Object objectToInvokeOn)
        throws IllegalArgumentException {

        // Make sure names is not null.
        if (names == null) {
            throw new IllegalArgumentException("'names' should not be null");
        }

        // Filter null values from names.
        names = names.stream().filter(name -> name != null).collect(Collectors.toList());

        // Make sure after filtering, names is now not empty.
        if (names.isEmpty()) {
            throw new IllegalArgumentException("'names' should not be empty");
        }

        // Make sure methodToInvoke and objectToInvokeOn aren't null.
        if (methodToInvoke == null) {
            throw new IllegalArgumentException("'methodToInvoke' should not be null");
        }
        if (objectToInvokeOn == null) {
            throw new IllegalArgumentException("'objectToInvokeOn' should not be null");
        }

        // Assign values to fields.
        this.names = names;
        this.description = (description == null) ? "" : description;
        this.options = (options == null) ? new ArrayList<>() : options.stream().filter(option -> option != null).collect(Collectors.toList());
        this.methodToInvoke = methodToInvoke;
        this.objectToInvokeOn = objectToInvokeOn;

        // Map all options
        this.options.stream().forEach((option) -> {
            for (int j = 0; j < option.numberOfNames(); j++) {
                optionNamesToOptions.put(option.getNameByIndex(j), option);
            }
        });
    }

    /**
     * Returns the name at the index, where index is bound between 0 and the
     * number of names minus 1.
     *
     * @param index The index of the name to return.
     * @return The name at the index.
     */
    public final String getNameByIndex(int index) {
        return names.get(Math.max(Math.min(index, names.size() - 1), 0));
    }

    /**
     * Returns the number of names this instance has.
     *
     * @return The number of names this instance has.
     */
    public final int numberOfNames() {
        return names.size();
    }

    /**
     * Convenience method for getting the primary name.
     *
     * @return This command's primary name.
     */
    public final String getPrimaryName() {
        return names.get(0);
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
        return names.size() > 1;
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
     * number of options minus 1.
     *
     * @param index The index of the option to return.
     * @return The option at the bound index, or empty if this has no options.
     */
    public final Optional<JcOption> getOptionByIndex(int index) {
        if (options.size() < 1) {
            return Optional.empty();
        }
        return Optional.of(options.get(Math.max(Math.min(index, options.size() - 1), 0)));
    }

    /**
     * Gets the option that has the supplied name.
     *
     * @param optionName The name of the option to find.
     * @return The supplied option, or empty if this has no option with that
     * name.
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
        return options.size();
    }

    /**
     * Gets the index of the supplied JcOption.
     *
     * @param option The option of which the index to get.
     * @return The index of the supplied JcOption, or -1 if it is not found or
     * null.
     */
    public final int indexOfOption(JcOption option) {
        return options.indexOf(option);
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

    /**
     * Invokes this command, using the supplied array of arguments.
     *
     * @param args The arguments.
     * @throws CommandInvocationException If invoking the command failed.
     */
    public final void invoke(Object... args) throws CommandInvocationException {
        try {
            methodToInvoke.invoke(objectToInvokeOn, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CommandInvocationException(this, ex);
        }
    }

    /**
     * Returns whether the supplied object is the same object this command will
     * invoke a method on when invoked.
     *
     * @param object The object to check.
     * @return True if the above is the case, otherwise false.
     */
    public final boolean isMyObject(Object object) {
        return this.objectToInvokeOn == object;
    }
}
