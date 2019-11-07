package com.github.agadar.javacommander.example;

import com.github.agadar.javacommander.JavaCommander;
import com.github.agadar.javacommander.exception.JavaCommanderException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Example runnable that continuously reads user input and sends the input to a
 * JavaCommander instance to find and execute the associated commands.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@AllArgsConstructor
public class TerminalInputReader implements Runnable {

    /**
     * The JavaCommander instance this reader sends input to.
     */
    @Getter
    @NonNull
    private final JavaCommander javaCommander;

    /**
     * Constructor. Assigns a new JavaCommander to this.
     */
    public TerminalInputReader() {
        this(new JavaCommander());
    }

    /**
     * Continuously reads input from a BufferedReader and sends it to the
     * JavaCommander instance. Exceptions are printed; they don't stop the loop.
     */
    @Override
    public final void run() {
        var br = new BufferedReader(new InputStreamReader(System.in));

        // Thread loop.
        while (!Thread.currentThread().isInterrupted()) {
            try {
                javaCommander.execute(br.readLine());
            } catch (IOException | JavaCommanderException ex) {
                System.out.println(ex.getMessage());
            } finally {
                System.out.println(); // always print a newline after a command
            }
        }
    }
}
