package com.martin.javacommander.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for marking a method parameter as a command option.
 * 
 * @author marti
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandOption
{
    /**
     * Names of the option. The first entry is considered its primary name,
     * the other entries are considered synonyms.
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
     * Whether or not this option must always manually be given a value.
     * 
     * @return 
     */
    boolean mandatory() default false;
    /**
     * This option's default value. Ignored if this option is mandatory.
     * 
     * @return 
     */
    String[] defaultValue() default {};
}
