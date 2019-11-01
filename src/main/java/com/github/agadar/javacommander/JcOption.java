package com.github.agadar.javacommander;

import java.util.List;
import java.util.stream.Collectors;

import com.github.agadar.javacommander.exception.OptionTranslatorException;
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
     * Names of the option. The first entry is its primary name. The other entries
     * are synonyms.
     */
    private final List<String> names;

    /**
     * The translator used to parse a string to the option's type.
     */
    private final Class<? extends OptionTranslator<T>> translatorType;

    /**
     * The type of this option's underlying parameter.
     */
    private final Class<T> parameterType;

    /**
     * A description of the option.
     */
    public final String description;

    /**
     * Whether or not this option has a default value.
     */
    public final boolean hasDefaultValue;

    /**
     * This option's default value. If 'hasDefaultValue' is set to false, then this
     * value is ignored and set to null.
     */
    public final T defaultValue;

    /**
     * Constructor.
     *
     * @param names           Names of the option. The first entry is its primary
     *                        name. The other entries are synonyms.
     * @param description     A description of the option.
     * @param hasDefaultValue Whether or not this option has a default value.
     * @param parameterType   The type of this option's underlying parameter.
     * @param defaultValue    This option's default value. If 'hasDefaultValue' is
     *                        set to false, then this value is ignored. It is
     *                        translated to the correct type using this instance's
     *                        own translate method.
     * @param translatorType  The translator type used to parse a string to the
     *                        option's type.
     * @throws IllegalArgumentException  If one of the parameter values is invalid.
     * @throws OptionTranslatorException If the option translator failed to parse
     *                                   the default value if it has one, or when
     *                                   the translator itself failed to be
     *                                   instantiated.
     */
    public JcOption(List<String> names, String description, boolean hasDefaultValue,
            Class<T> parameterType, String defaultValue, Class<? extends OptionTranslator<T>> translatorType)
            throws OptionTranslatorException, IllegalArgumentException {

        // Make sure names is not null.
        if (names == null) {
            throw new IllegalArgumentException("'names' should not be null");
        }

        // Filter null values from names.
        names = names.stream().filter(name -> name != null && !name.isEmpty()).collect(Collectors.toList());

        // Make sure after filtering, names is now not empty.
        if (names.isEmpty()) {
            throw new IllegalArgumentException("'names' should not be empty");
        }

        // Make sure type is not null.
        if (parameterType == null) {
            throw new IllegalArgumentException("'parameterType' should not be null");
        }

        // Assign values to fields.
        this.names = names;
        this.description = (description == null) ? "" : description;
        this.hasDefaultValue = hasDefaultValue;
        this.parameterType = parameterType;
        this.translatorType = translatorType;
        this.defaultValue = this.hasDefaultValue ? translate(defaultValue) : null;
    }

    /**
     * Returns the name at the index, where index is bound between 0 and the number
     * of names minus 1.
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
     * @return This option's primary name.
     */
    public final String getPrimaryName() {
        return names.get(0);
    }

    /**
     * Convenience method for checking whether this option has a description set.
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
        return (translatorType != null) ? (!translatorType.equals(NoTranslator.class)) : false;
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
        return this.getPrimaryName().equals(((JcOption<?>) obj).getPrimaryName());
    }

    /**
     * Parses a string to a type using this option's translator.
     *
     * @param stringToParse The string to parse.
     * @return The parsed value.
     * @throws OptionTranslatorException If the supplied translator failed to parse
     *                                   the default value, or when the translator
     *                                   itself failed to be instantiated.
     */
    public final T translate(String stringToParse) throws OptionTranslatorException {
        try {
            // If no translator is set, attempt a normal valueOf.
            if (translatorType == null || translatorType.equals(NoTranslator.class)) {
                return OptionTranslator.parseString(stringToParse, parameterType);
            } // If one is set, then attempt using that.
            else {
                return translatorType.getDeclaredConstructor().newInstance().translateString(stringToParse);
            }
        } catch (NumberFormatException | IndexOutOfBoundsException | ClassCastException ex) {
            throw new OptionTranslatorException("Failed to parse String '" + stringToParse
                    + "' to type '" + parameterType.getSimpleName() + "'", ex);
        } catch (Exception ex) {
            throw new OptionTranslatorException(
                    "Failed to instantiate translator '" + translatorType.getSimpleName() + "'", ex);
        }
    }
}
