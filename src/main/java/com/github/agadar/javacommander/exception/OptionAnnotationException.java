package com.github.agadar.javacommander.exception;

import java.lang.reflect.Method;

/**
 * Thrown when a parameter is not properly annotated with the @Option
 * annotation.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class OptionAnnotationException extends JavaCommanderException {

    private static final long serialVersionUID = 1L;
    
    /**
     * The method of which a parameter was not properly annotated.
     */
    public final Method method;

    /**
     * Constructor.
     *
     * @param method The method of which a parameter was not properly annotated.
     */
    public OptionAnnotationException(Method method) {
        super(String.format("Not all parameters of method '%s.%s' are properly annotated with @Option",
                method.getDeclaringClass().getSimpleName(), method.getName()));
        this.method = method;
    }
}
