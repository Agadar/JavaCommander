package com.github.agadar.javacommander.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.agadar.javacommander.optionvalueparser.NoOpOptionValueParser;
import com.github.agadar.javacommander.optionvalueparser.OptionValueParser;

/**
 * Annotation used for marking a method parameter as a command option.
 * Alternatively, these may be defined in the 'options' field of the Command
 * annotation instead.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {

    /**
     * Names of the option. The first entry is its primary name. The other entries
     * are synonyms. If no name is supplied, then the name of the option is set to
     * 'arg[x]' where '[x]' is the parameter's index. (e.g. 'arg0', 'arg1', 'arg2',
     * etc.)
     *
     * @return Names of the option.
     */
    String[] names() default {};

    /**
     * A description of the option.
     *
     * @return A description of the option.
     */
    String description() default "";

    /**
     * Whether or not this option has a default value.
     *
     * @return Whether or not this option has a default value.
     */
    boolean hasDefaultValue() default false;

    /**
     * This option's default value. If 'hasDefaultValue' is set to false, then this
     * value is ignored.
     *
     * @return This option's default value.
     */
    String defaultValue() default "";

    /**
     * The parser used to parse a string to the option's type. If the option's type
     * is a primitive, a boxed primitive, or a string, then no parser needs to be
     * supplied.
     *
     * @return this option's value parser type
     */
    Class<? extends OptionValueParser<?>> valueParser() default NoOpOptionValueParser.class;

}
