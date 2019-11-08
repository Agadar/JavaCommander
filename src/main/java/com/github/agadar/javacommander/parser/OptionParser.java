package com.github.agadar.javacommander.parser;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.github.agadar.javacommander.JcOption;
import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;
import com.github.agadar.javacommander.exception.OptionAnnotationException;
import com.github.agadar.javacommander.exception.OptionTranslatorException;

/**
 * Parses {@link Option} annotations to {@link JcOption} instances.
 * 
 * @author adm
 *
 */
public class OptionParser {

    /**
     * Validates and parses all {@link Option} annotations found in the
     * {@link Command} annotation, or on the method parameters, to an array of
     * {@link JcOption}'s.
     *
     * @param commandAnnotation The {@link Command} annotation of the method with
     *                          the {@link Option} annotations to parse.
     * @param method            The method with the {@link Option} annotations to
     *                          parse.
     * @return The parsed values.
     * @throws OptionTranslatorException If an option translator failed to parse a
     *                                   default value, or when the translator
     *                                   itself failed to be instantiated.
     * @throws OptionAnnotationException If a parameter is not properly annotated
     *                                   with the {@link Option} annotation.
     */
    public Collection<JcOption<?>> parseOptions(Command commandAnnotation, Method method)
            throws OptionAnnotationException, OptionTranslatorException {

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

    private Collection<JcOption<?>> parseOptionsFromCommandAnnotation(Command commandAnnotation, Parameter[] parameters)
            throws OptionTranslatorException {

        var jcOptions = new ArrayList<JcOption<?>>();
        for (int i = 0; i < parameters.length; i++) {
            var parsedOption = parseOption(commandAnnotation.options()[i], parameters[i]);
            jcOptions.add(parsedOption);
        }
        return jcOptions;
    }

    private boolean noOptionsDefinedInCommand(Command commandAnnotation) {
        return commandAnnotation.options().length == 0;
    }

    private Collection<JcOption<?>> parseOptionsFromParamAnnotations(Method method, Parameter[] parameters)
            throws OptionTranslatorException, OptionAnnotationException {

        var jcOptions = new ArrayList<JcOption<?>>();
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
    private JcOption<?> parseOption(Option optionAnnotation, Parameter parameter) throws OptionTranslatorException {
        var names = deriveOptionNames(optionAnnotation, parameter);
        boolean hasDefaultValue = optionAnnotation.hasDefaultValue();
        String defaultValueStr = optionAnnotation.defaultValue();
        String description = optionAnnotation.description();
        var type = parameter.getType();
        var translatorClass = optionAnnotation.translator();
        return new JcOption(Arrays.asList(names), description, hasDefaultValue, type, defaultValueStr, translatorClass);
    }

    private String[] deriveOptionNames(Option optionAnnotation, Parameter parameter) {
        return optionAnnotation.names().length > 0 ? optionAnnotation.names()
                : new String[] { parameter.getName() };
    }
}
