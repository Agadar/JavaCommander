package com.github.agadar.javacommander;

import com.github.agadar.javacommander.translator.NoTranslator;
import com.github.agadar.javacommander.translator.OptionTranslator;

/**
 * A command option parsed from an Option annotation.
 *
 * @author Agadar
 * @param <T> the type of this option's value
 */
public class JcOption<T> {

    public final String[] names;
    public final String description;
    public final boolean hasDefaultValue;
    public final Class<T> type;
    public final T defaultValue;
    public final Class<? extends OptionTranslator<T>> translator;

    public JcOption(String[] names, String description, boolean hasDefaultValue,
            Class<T> type, T defaultValue, Class<? extends OptionTranslator<T>> translator) {
        this.names = names;
        this.description = description;
        this.hasDefaultValue = hasDefaultValue;
        this.type = type;
        this.defaultValue = defaultValue;
        this.translator = translator;
    }

    /**
     * Convenience method for getting the primary name.
     *
     * @return this option's primary name
     */
    public final String getPrimaryName() {
        return names[0];
    }

    /**
     * Convenience method for checking whether this option has a description.
     *
     * @return whether this option has a description
     */
    public final boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * Convenience method for checking whether this option has a translator set.
     *
     * @return whether this option has a translator set
     */
    public final boolean hasTranslator() {
        return !translator.equals(NoTranslator.class);
    }
}
