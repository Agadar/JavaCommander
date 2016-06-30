package com.github.agadar.javacommander.annotation;

import com.github.agadar.javacommander.translator.NoTranslator;
import com.github.agadar.javacommander.translator.OptionTranslator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for marking a method parameter as a command option.
 * Alternatively, these may be defined in the 'options' field of the Command
 * annotation instead, or even in conjunction.
 *
 * @author Agadar
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option
{
    /**
     * Names of the option. The first entry is considered its primary name, the
     * other entries are considered synonyms. If no name is supplied, then the
     * name of the option will be 'arg{#}' where {#} is the parameter's index.
     * (e.g. 'arg0', 'arg1', 'arg2', etc.)
     *
     * @return names this option is known by
     */
    String[] names() default {};

    /**
     * A brief description of the option.
     *
     * @return description of this option
     */
    String description() default "";

    /**
     * Whether or not this option has a default value. If it does not, then a
     * value MUST be supplied by the caller for this option, else it is
     * optional.
     *
     * @return whether or not this option has a default value
     */
    boolean hasDefaultValue() default false;

    /**
     * This option's default value.
     *
     * @return this option's default value
     */
    String defaultValue() default "";

    /**
     * The translator for this option. If this is not set, then
     * OptionTranslator.parseToPrimitive is used for translating the string
     * value supplied by this option to the method parameter it corresponds to.
     * As this only works on primitives and Strings, users must set this manually
     * to home-brewed implementations of OptionTranslator for more complex types
     * and collections.
     *
     * @return this option's translator type
     */
    Class<? extends OptionTranslator> translator() default NoTranslator.class;

}
