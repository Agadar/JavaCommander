package com.github.agadar.javacommander.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for marking a method as a command.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * Names of the command. The first entry is the primary name. The other entries
     * are synonyms. If no name is supplied, then the name of the command is set to
     * the name of the annotated method.
     *
     * @return The names of this command.
     */
    String[] names() default {};

    /**
     * A description of the command.
     *
     * @return A description of the command.
     */
    String description() default "";

    /**
     * This command's options. Alternatively, these may be defined as parameter
     * annotations on the method's parameters instead.
     *
     * @return This command's options.
     */
    Option[] options() default {};
}
