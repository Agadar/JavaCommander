package com.github.agadar.javacommander;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A command parsed from a Command annotation.
 *
 * @author Agadar
 */
public class JcCommand {

    public final String[] names;
    public final String description;
    public final List<JcOption> options;              // list of the options, in order of the method's parameters
    public final Map<String, JcOption> optionNamesToOptions; // options mapped to option names, including synonyms
    public final Method methodToInvoke;
    public final Object objectToInvokeOn;

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
     * Convenience method for getting the primary name.
     *
     * @return this command's primary name
     */
    public final String getPrimaryName() {
        return names[0];
    }

    /**
     * Convenience method for checking whether this command has any options.
     *
     * @return whether this command has any options
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
     * @return whether this command has a description
     */
    public final boolean hasDescription() {
        return description != null && !description.isEmpty();
    }
}
