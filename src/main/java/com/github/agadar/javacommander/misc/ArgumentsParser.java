package com.github.agadar.javacommander.misc;

import java.util.List;

import com.github.agadar.javacommander.JcCommand;
import com.github.agadar.javacommander.exception.NoValueForOptionException;
import com.github.agadar.javacommander.exception.OptionValueParserException;
import com.github.agadar.javacommander.exception.UnknownOptionException;

import lombok.NonNull;

/**
 * Parses arguments.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class ArgumentsParser {

    /**
     * Parses the argument list.
     * 
     * @param args    The argument tokens to parse.
     * @param command The command to parse the tokens for.
     * 
     * @throws UnknownOptionException     If an option was supplied for the command
     *                                    that it does not have.
     * @throws NoValueForOptionException  If an option without a default value was
     *                                    not supplied a value.
     * @throws OptionValueParserException If an argument token could not be parsed
     *                                    to its corresponding parameter type.
     */
    public Object[] parseArguments(@NonNull List<String> args, @NonNull JcCommand command)
            throws NoValueForOptionException, UnknownOptionException, OptionValueParserException {

        Object[] finalArgs = new Object[command.numberOfOptions()];

        // If there's arguments left to parse to options, let's parse them.
        if (args.size() > 1) {

            // If the first argument is a valid option, use explicit parsing.
            // Otherwise, use implicit parsing.
            if (command.hasOption(args.get(1))) {
                parseArgumentsExplicit(args, command, finalArgs);
            } else {
                parseArgumentsImplicit(args, command, finalArgs);
            }
        }

        // For each entry in finalArgs that is still null, check whether there
        // is a default value. If there is, use that. Else, throw an error.
        for (int i = 0; i < finalArgs.length; i++) {
            Object val = finalArgs[i];

            if (val == null) {
                var option = command.getOptionByIndex(i).get();

                if (option.isHasDefaultValue()) {
                    finalArgs[i] = option.getDefaultValue();
                } else {
                    throw new NoValueForOptionException(command, option);
                }
            }
        }
        return finalArgs;
    }

    private void parseArgumentsExplicit(List<String> args, JcCommand command, Object[] finalArgs)
            throws UnknownOptionException, OptionValueParserException, NoValueForOptionException {

        var currentOption = command.getOptionByName(args.get(1));
        boolean parsingOption = false;

        // Iterate over all the arguments.
        for (int i = 2; i < args.size(); i++) {

            // If we're not currently finding a value for an option, then
            // try find the option. Else, try to parse the value.
            if (parsingOption) {
                currentOption = command.getOptionByName(args.get(i));

                // If the option was not found, throw an exception.
                if (!currentOption.isPresent()) {
                    throw new UnknownOptionException(command, args.get(i));
                }
            } else {
                var parsedArg = currentOption.get().parseOptionValue(args.get(i));
                finalArgs[command.indexOfOption(currentOption.get())] = parsedArg;
                currentOption = null;
            }
            parsingOption = !parsingOption;
        }

        // If the last parameter was not given a value, throw an error.
        if (currentOption != null) {
            throw new NoValueForOptionException(command, currentOption.get());
        }
    }

    private void parseArgumentsImplicit(List<String> args, JcCommand command, Object[] finalArgs)
            throws OptionValueParserException {

        // If too many arguments were supplied, throw an error.
        if (args.size() - 1 > finalArgs.length) {
            throw new IllegalArgumentException("Too many arguments supplied for this command");
        }

        // Now simply iterate over the arguments, parsing them and placing
        // them in finalArgs as we go.
        for (int i = 1; i < args.size(); i++) {
            var currentOption = command.getOptionByIndex(i - 1).get();
            var parsedArg = currentOption.parseOptionValue(args.get(i));
            finalArgs[i - 1] = parsedArg;
        }
    }
}
