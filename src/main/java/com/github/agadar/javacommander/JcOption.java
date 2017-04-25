package com.github.agadar.javacommander;

import com.github.agadar.javacommander.translator.NoTranslator;
import com.github.agadar.javacommander.translator.OptionTranslator;

/**
 * A command option parsed from an Option annotation.
 *
 * @author Agadar
 * @param <T> The type of this option's underlying parameter.
 */
public final class JcOption<T> {

    /**
     * Names of the option. The first entry is its primary name. The other
     * entries are synonyms.
     */
    public final String[] names;

    /**
     * A description of the option.
     */
    public final String description;

    /**
     * Whether or not this option has a default value.
     */
    public final boolean hasDefaultValue;

    /**
     * The type of this option's underlying parameter.
     */
    public final Class<T> type;

    /**
     * This option's default value. If 'hasDefaultValue' is set to false, then
     * this value is ignored.
     */
    public final T defaultValue;

    /**
     * The translator used to parse a string to the option's type.
     */
    public final Class<? extends OptionTranslator<T>> translator;

    /**
     * Constructor.
     *
     * @param names Names of the option. The first entry is its primary name.
     * The other entries are synonyms.
     * @param description A description of the option.
     * @param hasDefaultValue Whether or not this option has a default value.
     * @param type The type of this option's underlying parameter.
     * @param defaultValue This option's default value. If 'hasDefaultValue' is
     * set to false, then this value is ignored.
     * @param translator The translator used to parse a string to the option's
     * type.
     */
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
     * @return This option's primary name.
     */
    public final String getPrimaryName() {
        return names[0];
    }

    /**
     * Convenience method for checking whether this option has a description.
     *
     * @return Whether this option has a description.
     */
    public final boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * Convenience method for checking whether this option has a translator set.
     *
     * @return Whether this option has a translator set.
     */
    public final boolean hasTranslator() {
        return !translator.equals(NoTranslator.class);
    }
}
