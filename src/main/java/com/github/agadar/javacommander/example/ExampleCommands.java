package com.github.agadar.javacommander.example;

import com.github.agadar.javacommander.JavaCommander;
import com.github.agadar.javacommander.JcCommandOption;
import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Example class that defines a 'help' and an 'exit' command. Instantiate this
 * class and add it via a JavaCommander instance's registerObject-method so that
 * the commands can be run via the same JavaCommander instance's execute-method.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@AllArgsConstructor
public class ExampleCommands {

    /**
     * Reference to a JavaCommander instance.
     */
    @Getter
    @NonNull
    private final JavaCommander javaCommander;

    /**
     * Constructor. Assigns a new JavaCommander to this.
     */
    public ExampleCommands() {
        this(new JavaCommander());
    }

    /**
     * Prints a list of all available commands. Called by the basic 'help' command.
     * If the commandName is not null and not empty, then the help of the command
     * with that name is given.
     *
     * @param commandName The name of the command to return the help of, or
     *                    null/empty.
     */
    @Command(names = { "help", "usage",
            "?" }, description = "Display the help.", options = @Option(names = "-command", description = "Display a specific command's help.", hasDefaultValue = true))
    public void usage(String commandName) {
        var stringBuilder = new StringBuilder("--------------------\n");

        // If no command name given, then list general info of all commands.
        if (commandName == null || commandName.isEmpty()) {
            stringBuilder.append(
                    "Displaying help. Use option '-command' to display a specific command's help.\n\nAvailable commands:");

            // Iterate over the commands to find the info
            javaCommander.getJcRegistry().getParsedCommands().forEach((command) -> {
                stringBuilder.append("\n");
                stringBuilder.append(command.getPrimaryName());

                for (int i = 1; i < command.numberOfNames(); i++) {
                    stringBuilder.append(", ");
                    stringBuilder.append(command.getNameByIndex(i));
                }
                stringBuilder.append("\t\t");
                stringBuilder.append(command.hasDescription() ? command.getDescription() : "No description available.");
            });
        } // Else if a command name is given, then list info specific to that command
        else {
            // Retrieve the command. If it does not exist, then return with an error
            // message.
            var command = javaCommander.getJcRegistry().getCommand(commandName);

            if (!command.isPresent()) {
                stringBuilder.append(String.format("'%s' is not recognized as a command", commandName));
                System.out.println(stringBuilder.toString());
                return;
            }

            // Build string
            stringBuilder.append("Description:\n");
            stringBuilder.append(command.get().hasDescription() ? command.get().getDescription()
                    : "No description available.");
            stringBuilder.append("\n\n");

            // If there are synonyms, then list them.
            stringBuilder.append("Synonyms:\n");
            stringBuilder.append(command.get().getPrimaryName());

            for (int i = 1; i < command.get().numberOfNames(); i++) {
                stringBuilder.append(", ");
                stringBuilder.append(command.get().getNameByIndex(i));
            }
            stringBuilder.append("\n\n");

            // If there are options, then list them.
            stringBuilder.append("Available options:");
            if (command.get().hasOptions()) {
                for (int i = 0; i < command.get().numberOfOptions(); i++) {
                    optionToString(command.get().getOptionByIndex(i).get(), stringBuilder);
                }
            } // Otherwise, inform the user there are no options.
            else {
                stringBuilder.append("\nNo options available.");
            }
        }
        // Print help
        stringBuilder.append("\n--------------------");
        System.out.println(stringBuilder.toString());
    }

    /**
     * Appends a string representation of a JcCommandOption to a StringBuilder.
     *
     * @param jcOption      The JcCommandOption to append a string representation
     *                      of.
     * @param stringBuilder The StringBuilder to append it to.
     */
    protected void optionToString(JcCommandOption<?> jcOption, StringBuilder stringBuilder) {
        stringBuilder.append("\n");
        stringBuilder.append(jcOption.getPrimaryName());

        for (int i = 1; i < jcOption.numberOfNames(); i++) {
            stringBuilder.append(", ");
            stringBuilder.append(jcOption.getNameByIndex(i));
        }
        stringBuilder.append("\t\t");
        stringBuilder.append(jcOption.getDescription());
    }

    /**
     * Calls System.Exit(0). Used for the basic exit command.
     */
    @Command(names = { "exit", "quit" }, description = "Exit the program.")
    public void exitProgram() {
        System.exit(0);
    }
}
