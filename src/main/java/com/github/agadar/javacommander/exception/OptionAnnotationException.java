package com.github.agadar.javacommander.exception;

import java.lang.reflect.Method;

/**
 * Thrown when a method is not properly annotated with the Option annotation.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class OptionAnnotationException extends Exception {

    /**
     * The method of which a parameter was not properly annotated.
     */
    public final Method method;

    public OptionAnnotationException(Method method) {
        super(String.format("Not all parameters of method '%s.%s' are annotated with @Option",
                method.getDeclaringClass().getSimpleName(), method.getName()));
        this.method = method;
    }
}
