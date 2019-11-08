package com.github.agadar.javacommander;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.agadar.javacommander.exception.OptionTranslatorException;
import com.github.agadar.javacommander.translator.NoTranslator;
import com.github.agadar.javacommander.translator.OptionTranslator;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * A command option parsed from an Option annotation.
 *
 * @author Agadar
 * @param <T> The type of this option's underlying parameter.
 */
@Slf4j
public class JcOption<T> {

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
    public JcOption(@NonNull Collection<String> names, String description, boolean hasDefaultValue,
            @NonNull Class<T> parameterType, String defaultValue, Class<? extends OptionTranslator<T>> translatorType)
            throws OptionTranslatorException, IllegalArgumentException {

        this.names = names.stream().filter(name -> name != null && !name.isEmpty()).collect(Collectors.toList());

        if (this.names.isEmpty()) {
            throw new IllegalArgumentException("'names' should not be empty");
        }
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
    public String getNameByIndex(int index) {
        return names.get(Math.max(Math.min(index, names.size() - 1), 0));
    }

    /**
     * Returns the number of names this instance has.
     *
     * @return The number of names this instance has.
     */
    public int numberOfNames() {
        return names.size();
    }

    /**
     * Convenience method for getting the primary name.
     *
     * @return This option's primary name.
     */
    public String getPrimaryName() {
        return names.get(0);
    }

    /**
     * Convenience method for checking whether this option has a description set.
     *
     * @return Whether this option has a description set.
     */
    public boolean hasDescription() {
        return !description.isEmpty();
    }

    /**
     * Convenience method for checking whether this option has a translator set.
     *
     * @return Whether this option has a translator set.
     */
    public boolean hasTranslator() {
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
    public T translate(String stringToParse) throws OptionTranslatorException {
        try {
            if (translatorType == null || translatorType.equals(NoTranslator.class)) {
                return OptionTranslator.parseString(stringToParse, parameterType);
            } else {
                return translatorType.getDeclaredConstructor().newInstance().translateString(stringToParse);
            }

        } catch (NumberFormatException | IndexOutOfBoundsException | ClassCastException ex) {
            String errorMsg = String.format("Failed to parse String '%s' to type '%s'", stringToParse,
                    parameterType.getSimpleName());
            log.error(errorMsg, ex);
            throw new OptionTranslatorException(errorMsg, ex);

        } catch (Exception ex) {
            String errorMsg = String.format("Failed to instantiate translator '%s'", translatorType.getSimpleName());
            log.error(errorMsg, ex);
            throw new OptionTranslatorException(errorMsg, ex);
        }
    }
}
