package com.github.agadar.javacommander.annotation.parser;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.github.agadar.javacommander.JcCommand;
import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.exception.OptionAnnotationException;
import com.github.agadar.javacommander.exception.OptionValueParserException;

import lombok.NonNull;

/**
 * Parses {@link Command} annotations to {@link JcCommand} instances.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class CommandAnnotationParser {

    private final OptionAnnotationParser optionAnnotationParser;

    public CommandAnnotationParser() {
        this(new OptionAnnotationParser());
    }

    public CommandAnnotationParser(@NonNull OptionAnnotationParser optionAnnotationParser) {
        this.optionAnnotationParser = optionAnnotationParser;
    }

    /**
     * Parses all annotated, public, non-static methods of the supplied object.
     *
     * @param object The object containing annotated methods.
     * @throws OptionAnnotationException  If a method's parameter is not properly
     *                                    annotated with the @Option annotation.
     * @throws OptionValueParserException If an option value parser failed to parse
     *                                    a default value, or when the parser itself
     *                                    failed to be instantiated.
     */
    public Collection<JcCommand> parseFromObject(@NonNull Object object)
            throws OptionAnnotationException, OptionValueParserException {

        var parsedCommands = new ArrayList<JcCommand>();
        for (var method : object.getClass().getMethods()) {
            if (method.isAnnotationPresent(Command.class) && !Modifier.isStatic(method.getModifiers())) {
                parse(object, parsedCommands, method);
            }
        }
        return parsedCommands;
    }

    /**
     * Registers all annotated, public, static methods of the supplied class.
     *
     * @param clazz The class containing annotated methods.
     * @throws OptionAnnotationException  If a method's parameter is not properly
     *                                    annotated with the @Option annotation.
     * @throws OptionValueParserException If an option value parser failed to parse
     *                                    a default value, or when the parser itself
     *                                    failed to be instantiated.
     */
    public Collection<JcCommand> parseFromClass(@NonNull Class<?> clazz)
            throws OptionAnnotationException, OptionValueParserException {

        var parsedCommands = new ArrayList<JcCommand>();
        for (var method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Command.class) && Modifier.isStatic(method.getModifiers())) {
                parse(clazz, parsedCommands, method);
            }
        }
        return parsedCommands;
    }

    private void parse(Object source, ArrayList<JcCommand> parsedCommands, Method method)
            throws OptionAnnotationException, OptionValueParserException {
        var commandAnnotation = ((Command) method.getAnnotation(Command.class));
        String[] names = commandAnnotation.names().length > 0 ? commandAnnotation.names()
                : new String[] { method.getName() };
        String description = commandAnnotation.description();
        var options = optionAnnotationParser.parseOptions(commandAnnotation, method);
        var command = new JcCommand(Arrays.asList(names), description, options, method, source);
        parsedCommands.add(command);
    }
}
