package com.github.agadar.javacommander.example;

import com.github.agadar.javacommander.JavaCommander;
import com.github.agadar.javacommander.JcCommand;
import com.github.agadar.javacommander.JcOption;
import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;

/**
 * Defines some custom commands that can be used, such as a 'help' command and
 * an 'exit' command.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class DefaultCommands {

    /**
     * Reference to a JavaCommander instance.
     */
    private final JavaCommander javaCommander;

    /**
     * Constructor.
     *
     * @param javaCommander
     */
    public DefaultCommands(JavaCommander javaCommander) {
        this.javaCommander = javaCommander;
    }

    /**
     * Prints a list of all available commands. Called by the basic 'help'
     * command. If the given commandName is not null or empty, then the help of
     * the given command is given, listing its options. May be overridden to
     * implement custom help message.
     *
     * @param commandName name of the command to print the help of
     */
    @Command(names = {"help", "usage", "?"}, description = "Display the help.", options
            = @Option(names = "-command", description = "Display a specific command's help.", hasDefaultValue = true))
    public void usage(String commandName) {
        String toString = "--------------------\n";

        // If no command name given, then list general info of all commands
        if (commandName == null || commandName.isEmpty()) {
            toString += "Displaying help. Use option '-command' to display a specific "
                    + "command's help.\n\nAvailable commands:";

            // Iterate over the commands to find the info
            for (JcCommand command : javaCommander.getParsedCommands()) {
                toString += "\n" + command.names[0];
                for (int i = 1; i < command.names.length; i++) {
                    toString += ", " + command.names[i];
                }
                toString += "\t\t" + (command.hasDescription() ? command.description : "No description available.");
            }
        } // Else if a command name is given, then list info specific to that command
        else {
            // Retrieve the command. If it does not exist, then return with an error message.
            JcCommand command = javaCommander.commandsToAllNames.get(commandName);
            if (command == null) {
                System.out.println(String.format(
                        "'%s' is not recognized as a command", commandName));
                return;
            }

            // Build string
            toString += "Description:\n" + (command.hasDescription() ? command.description : "No description available.") + "\n\n";

            // If there are synonyms, then list them.
            toString += "Synonyms:\n" + command.names[0];
            for (int i = 1; i < command.names.length; i++) {
                toString += ", " + command.names[i];
            }
            toString += "\n\n";

            // If there are options, then list them.
            toString += "Available options:";
            if (command.hasOptions()) {
                for (JcOption option : command.options) {
                    toString += "\n" + option.names[0];
                    for (int i = 1; i < option.names.length; i++) {
                        toString += ", " + option.names[i];
                    }
                    toString += "\t\t" + option.description;
                }
            } // Otherwise, inform the user there are no options.
            else {
                toString += "\nNo options available.";
            }
        }
        // Print help
        System.out.println(toString + "\n--------------------");
    }

    /**
     * Calls System.Exit(0). Used for the basic exit command.
     */
    @Command(names = {"exit", "quit"}, description = "Exit the program.")
    public void exitProgram() {
        System.exit(0);
    }
}
