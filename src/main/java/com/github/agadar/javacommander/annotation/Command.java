package com.github.agadar.javacommander.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for marking a method as being available as a command.
 *
 * @author Agadar
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command
{
    /**
     * Names of the command. The first entry is considered its primary name, the
     * other entries are considered synonyms. If no name is supplied, then the
     * name of the annotated method will be used for the name of the command.
     *
     * @return names this command is known by
     */
    String[] names() default {};

    /**
     * A brief description of the command.
     *
     * @return description of this command
     */
    String description() default "";

    /**
     * This command's options. Alternatively, these may be defined as parameter
     * annotations on the method's parameters instead, or even in conjunction.
     *
     * @return options of this command
     */
    Option[] options() default 
    {
    };
}
