package com.martin.javacommander.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for marking a parameter as a command option.
 * 
 * @author marti
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandOption
{
    String[] names();
    String description();
    boolean mandatory() default false;
    String[] defaultValue() default {};
}
