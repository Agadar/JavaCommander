package com.github.agadar.javacommander.example;

import com.github.agadar.javacommander.JavaCommander;
import com.github.agadar.javacommander.exception.CommandInvocationException;
import com.github.agadar.javacommander.exception.JavaCommanderException;
import com.github.agadar.javacommander.exception.NoValueForOptionException;
import com.github.agadar.javacommander.exception.OptionTranslatorException;
import com.github.agadar.javacommander.exception.UnknownCommandException;
import com.github.agadar.javacommander.exception.UnknownOptionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example runnable that continuously reads user input and sends the input to a
 * JavaCommander instance to find and execute the associated commands.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class TerminalInputReader implements Runnable {

    /**
     * The JavaCommander instance this reader sends input to.
     */
    public final JavaCommander javaCommander;

    /**
     * Constructor. Assigns a new JavaCommander to this.
     */
    public TerminalInputReader() {
        this(new JavaCommander());
    }

    /**
     * Constructor.
     *
     * @param javaCommander The JavaCommander this instance sends input to.
     */
    public TerminalInputReader(JavaCommander javaCommander) {
        if (javaCommander == null) {
            throw new IllegalArgumentException("'javaCommander' may not be null");
        }
        this.javaCommander = javaCommander;
    }

    /**
     * Continuously reads input from a BufferedReader and sends it to the
     * JavaCommander instance. Exceptions are printed; they don't stop the loop.
     */
    @Override
    public final void run() {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Thread loop.
        while (!Thread.currentThread().isInterrupted()) {
            try {
                javaCommander.execute(br.readLine());
            } catch (IOException | JavaCommanderException ex) {
                System.out.println(ex.getMessage());
            } finally {
                System.out.println();   // always print a newline after a command
            }
        }
    }
}
