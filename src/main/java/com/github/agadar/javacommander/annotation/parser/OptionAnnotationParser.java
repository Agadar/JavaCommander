package com.github.agadar.javacommander.annotation.parser;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.github.agadar.javacommander.JcCommandOption;
import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;
import com.github.agadar.javacommander.exception.OptionAnnotationException;
import com.github.agadar.javacommander.exception.OptionValueParserException;

import lombok.NonNull;

/**
 * Parses {@link Option} annotations to {@link JcCommandOption} instances.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class OptionAnnotationParser {

    /**
     * Validates and parses all {@link Option} annotations found in the
     * {@link Command} annotation, or on the method parameters, to an array of
     * {@link JcCommandOption}'s.
     *
     * @param commandAnnotation The {@link Command} annotation of the method with
     *                          the {@link Option} annotations to parse.
     * @param method            The method with the {@link Option} annotations to
     *                          parse.
     * @return The parsed values.
     * @throws OptionValueParserException If an option value parser failed to parse
     *                                    a default value, or when the parser itself
     *                                    failed to be instantiated.
     * @throws OptionAnnotationException  If a parameter is not properly annotated
     *                                    with the {@link Option} annotation.
     */
    public Collection<JcCommandOption<?>> parseOptions(@NonNull Command commandAnnotation, @NonNull Method method)
            throws OptionAnnotationException, OptionValueParserException {

        var parameters = method.getParameters();

        if (numberOfOptionsInCommandEqualsNumberOfParams(commandAnnotation, parameters)) {
            return parseOptionsFromCommandAnnotation(commandAnnotation, parameters);

        } else if (noOptionsDefinedInCommand(commandAnnotation)) {
            return parseOptionsFromParamAnnotations(method, parameters);

        } else {
            // Not enough options defined in command.
            throw new OptionAnnotationException(method);
        }
    }

    private boolean numberOfOptionsInCommandEqualsNumberOfParams(Command commandAnnotation, Parameter[] parameters) {
        return commandAnnotation.options().length == parameters.length;
    }

    private Collection<JcCommandOption<?>> parseOptionsFromCommandAnnotation(Command commandAnnotation,
            Parameter[] parameters)
            throws OptionValueParserException {

        var jcOptions = new ArrayList<JcCommandOption<?>>();
        for (int i = 0; i < parameters.length; i++) {
            var parsedOption = parseOption(commandAnnotation.options()[i], parameters[i]);
            jcOptions.add(parsedOption);
        }
        return jcOptions;
    }

    private boolean noOptionsDefinedInCommand(Command commandAnnotation) {
        return commandAnnotation.options().length == 0;
    }

    private Collection<JcCommandOption<?>> parseOptionsFromParamAnnotations(Method method, Parameter[] parameters)
            throws OptionValueParserException, OptionAnnotationException {

        var jcOptions = new ArrayList<JcCommandOption<?>>();
        for (Parameter paramameter : parameters) {
            if (paramameter.isAnnotationPresent(Option.class)) {
                var parsedOption = parseOption(paramameter.getAnnotation(Option.class), paramameter);
                jcOptions.add(parsedOption);
            } else {
                throw new OptionAnnotationException(method);
            }
        }
        return jcOptions;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private JcCommandOption<?> parseOption(Option optionAnnotation, Parameter parameter)
            throws OptionValueParserException {
        var names = deriveOptionNames(optionAnnotation, parameter);
        String defaultValueStr = optionAnnotation.defaultValue();
        String description = optionAnnotation.description();
        var type = parameter.getType();
        var parserClass = optionAnnotation.valueParser();
        return new JcCommandOption(Arrays.asList(names), description, type, defaultValueStr, parserClass);
    }

    private String[] deriveOptionNames(Option optionAnnotation, Parameter parameter) {
        return optionAnnotation.names().length > 0 ? optionAnnotation.names()
                : new String[] { parameter.getName() };
    }
}
