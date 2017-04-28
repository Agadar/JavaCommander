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
    private final String[] names;

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
     * @throws IllegalArgumentException If one of the parameter values is
     * invalid.
     */
    public JcOption(String[] names, String description, boolean hasDefaultValue,
            Class<T> type, T defaultValue, Class<? extends OptionTranslator<T>> translator) {
        if (names == null || names.length < 1 || names[0] == null) {
            throw new IllegalArgumentException("'names' should not be null or empty, and its first value should not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("'type' should not be null");
        }
        this.names = names;
        this.description = (description == null) ? "" : description;
        this.hasDefaultValue = hasDefaultValue;
        this.type = type;
        this.defaultValue = defaultValue;
        this.translator = translator;
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
     * @return This option's primary name.
     */
    public final String getPrimaryName() {
        return names[0];
    }

    /**
     * Convenience method for checking whether this option has a description
     * set.
     *
     * @return Whether this option has a description set.
     */
    public final boolean hasDescription() {
        return !description.isEmpty();
    }

    /**
     * Convenience method for checking whether this option has a translator set.
     *
     * @return Whether this option has a translator set.
     */
    public final boolean hasTranslator() {
        return (translator != null) ? (!translator.equals(NoTranslator.class)) : false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.getPrimaryName().hashCode();
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
        return this.getPrimaryName().equals(((JcOption) obj).getPrimaryName());
    }
}
