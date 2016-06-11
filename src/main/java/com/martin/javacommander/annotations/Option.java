package com.martin.javacommander.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for marking a method parameter as a command option.
 * Alternatively, these may be defined in the 'options' field of the Command
 * annotation instead, or even in conjunction.
 *
 * @author marti
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option
{
    /**
     * Names of the option. The first entry is considered its primary name, the
     * other entries are considered synonyms.
     *
     * @return
     */
    String[] names();

    /**
     * A brief description of the option.
     *
     * @return
     */
    String description() default "No description available.";

    /**
     * Whether or not this option has a default value. If it does not, then
     * a value MUST be supplied by the caller for this option, else it is optional.
     *
     * @return
     */
    boolean hasDefaultValue() default false;

    /**
     * This option's default value.
     *
     * @return
     */
    String[] defaultValue() default 
    {
    };
}
