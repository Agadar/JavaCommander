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
public class PCommand {

    public final String[] Names;
    public final String Description;
    public final List<POption> Options;              // list of the options, in order of the method's parameters
    public final Map<String, POption> OptionsMapped; // options mapped to option names, including synonyms
    public final Method ToInvoke;
    public final Object ToInvokeOn;

    public PCommand(String[] Names, String Description, List<POption> Options,
            Method ToInvoke, Object ToInvokeOn) {
        this.Names = Names;
        this.Description = Description;
        this.Options = Options;
        this.ToInvoke = ToInvoke;
        this.ToInvokeOn = ToInvokeOn;
        this.OptionsMapped = new HashMap<>();

        // Map all options
        Options.stream().forEach((option)
                -> {
                    for (String name : option.Names) {
                        OptionsMapped.put(name, option);
                    }
                });
    }

    /**
     * Convenience method for getting the primary name.
     *
     * @return this command's primary name
     */
    public final String getPrimaryName() {
        return Names[0];
    }

    /**
     * Convenience method for checking whether this command has any options.
     *
     * @return whether this command has any options
     */
    public final boolean hasOptions() {
        return Options != null && Options.size() > 0;
    }

    /**
     * Convenience method for checking whether this command has any synonyms.
     *
     * @return whether this command has any synonyms
     */
    public final boolean hasSynonyms() {
        return Names != null && Names.length > 1;
    }

    /**
     * Convenience method for checking whether this command has a description.
     *
     * @return whether this command has a description
     */
    public final boolean hasDescription() {
        return Description != null && !Description.isEmpty();
    }
}
