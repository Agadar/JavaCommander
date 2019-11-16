package com.github.agadar.javacommander;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.agadar.javacommander.exception.OptionValueParserException;
import com.github.agadar.javacommander.optionvalueparser.NullOptionValueParser;
import com.github.agadar.javacommander.optionvalueparser.OptionValueParser;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * A command option parsed from an Option annotation.
 *
 * @author Agadar (https://github.com/Agadar/)
 * @param <T> The type of this option's underlying parameter.
 */
@Slf4j
public class JcCommandOption<T> {

    /**
     * Names of the option. The first entry is its primary name. The other entries
     * are synonyms.
     */
    @Getter
    private final List<String> names;

    /**
     * The parser used to parse a string to the option's type.
     */
    private final Class<? extends OptionValueParser<T>> valueParserType;

    /**
     * The type of this option's underlying parameter.
     */
    private final Class<T> parameterType;

    /**
     * A description of the option.
     */
    @Getter
    private final String description;

    /**
     * This option's default value. If none was defined, then this is null.
     */
    @Getter
    private final T defaultValue;

    /**
     * Constructor.
     *
     * @param names           Names of the option. The first entry is its primary
     *                        name. The other entries are synonyms.
     * @param description     A description of the option.
     * @param parameterType   The type of this option's underlying parameter.
     * @param defaultValue    This option's default value. It is parsed to the
     *                        correct type using this instance's own parse method,
     *                        unless it equals the empty string.
     * @param valueParserType The parser type used to parse a string to the option's
     *                        type.
     * @throws IllegalArgumentException   If one of the parameter values is invalid.
     * @throws OptionValueParserException If the option parser failed to parse the
     *                                    default value if it has one, or when the
     *                                    parser itself failed to be instantiated.
     */
    public JcCommandOption(@NonNull Collection<String> names, String description, @NonNull Class<T> parameterType,
            String defaultValue, Class<? extends OptionValueParser<T>> valueParserType)
            throws OptionValueParserException, IllegalArgumentException {

        this.names = names.stream().filter(name -> name != null && !name.isEmpty()).collect(Collectors.toList());

        if (this.names.isEmpty()) {
            throw new IllegalArgumentException("'names' should not be empty");
        }
        this.description = (description == null) ? "" : description;
        this.parameterType = parameterType;
        this.valueParserType = valueParserType;
        this.defaultValue = determineDefaultValue(defaultValue);
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
     * Convenience method for checking whether this option has a parser set.
     *
     * @return Whether this option has a parser set.
     */
    public boolean hasValueParser() {
        return (valueParserType != null) ? (!valueParserType.equals(NullOptionValueParser.class)) : false;
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
        return this.getPrimaryName().equals(((JcCommandOption<?>) obj).getPrimaryName());
    }

    /**
     * Parses a string to a type using this option's value parser.
     *
     * @param stringToParse The string to parse.
     * @return The parsed value.
     * @throws OptionValueParserException If the supplied parser failed to parse the
     *                                    default value, or when the parser itself
     *                                    failed to be instantiated.
     */
    public T parseOptionValue(@NonNull String stringToParse) throws OptionValueParserException {
        if (valueParserType == null || valueParserType.equals(NullOptionValueParser.class)) {
            return OptionValueParser.parseToType(stringToParse, parameterType);
        }
        var parser = instantiateOptionValueParser();
        return parser.parse(stringToParse);
    }

    private OptionValueParser<T> instantiateOptionValueParser() throws OptionValueParserException {
        try {
            return valueParserType.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            String errorMsg = String.format("Failed to instantiate option value parser '%s'",
                    valueParserType.getSimpleName());
            log.error(errorMsg, ex);
            throw new OptionValueParserException(errorMsg, ex);
        }
    }

    private T determineDefaultValue(String defaultValue) throws OptionValueParserException {
        if (defaultValue == null || defaultValue.isEmpty()) {
            return null;
        }
        return parseOptionValue(defaultValue);
    }
}
