package com.github.agadar.javacommander.misc;

import java.util.List;

import com.github.agadar.javacommander.JcCommand;
import com.github.agadar.javacommander.JcCommandOption;
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
                var defaultValue = option.getDefaultValue();

                if (defaultValue != null) {
                    finalArgs[i] = defaultValue;
                } else {
                    throw new NoValueForOptionException(command, option);
                }
            }
        }
        return finalArgs;
    }

    private void parseArgumentsExplicit(List<String> args, JcCommand command, Object[] finalArgs)
            throws UnknownOptionException, OptionValueParserException, NoValueForOptionException {

        String currentArg = args.get(1);
        var currentOption = tryGetOption(command, currentArg);
        int indexOfOption = command.indexOfOption(currentOption);
        boolean findingValueForOption = true;

        for (int argsIndex = 2; argsIndex < args.size(); argsIndex++) {
            currentArg = args.get(argsIndex);

            if (findingValueForOption) {
                var parsedArg = currentOption.parseOptionValue(currentArg);
                finalArgs[indexOfOption] = parsedArg;
                findingValueForOption = false;

            } else {
                currentOption = tryGetOption(command, currentArg);
                indexOfOption = command.indexOfOption(currentOption);

                if (useFlagValueForOption(args, command, currentOption, argsIndex)) {
                    finalArgs[indexOfOption] = currentOption.getFlagValue();
                    findingValueForOption = false;

                } else {
                    findingValueForOption = true;
                }
            }
        }

        // If the last parameter was not given a value, throw an error.
        if (findingValueForOption) {
            throw new NoValueForOptionException(command, currentOption);
        }
    }

    private JcCommandOption<?> tryGetOption(JcCommand command, String currentArg) throws UnknownOptionException {
        return command.getOptionByName(currentArg).orElseThrow(() -> new UnknownOptionException(command, currentArg));
    }

    private boolean useFlagValueForOption(List<String> args, JcCommand command, JcCommandOption<?> currentOption,
            int argsIndex) {
        int nextArgsIndex = argsIndex + 1;
        boolean hasFlagValue = currentOption.getFlagValue() != null;
        boolean nextArgIsOptionName = nextArgsIndex < args.size() && command.hasOption(args.get(nextArgsIndex));
        return hasFlagValue && (nextArgIsOptionName || nextArgsIndex >= args.size());
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
            var currentArg = args.get(i);

            if (currentOption.getFlagValue() != null && currentOption.getNames().contains(currentArg)) {
                finalArgs[i - 1] = currentOption.getFlagValue();

            } else {
                var parsedArg = currentOption.parseOptionValue(currentArg);
                finalArgs[i - 1] = parsedArg;
            }
        }
    }
}
