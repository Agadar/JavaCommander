package com.martin.javacommander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Standard reader responsible for reading string commands from the console, 
 * passing them to a CommandRegistry, and printing the results back to the console.
 * 
 * @author Martin
 */
public class CommandReader implements Runnable
{
    /**
     * This application's command manager.
     */
    public final CommandRegistry commandRegistry = new CommandRegistry();   
    /**
     * This application's input reader.
     */
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    /**
     * Welcoming message.
     */
    private final String welcomeMsg;
    /**
     * The name of the program, which is shown on the user's input line.
     */
    private final String programName;
    
    /**
     * Constructor which takes a welcoming message and the name of the program
     * (or something else if so desired), which is shown on the user's input line.
     * 
     * @param welcomeMsg
     * @param programName 
     */
    public CommandReader(String welcomeMsg, String programName)
    {
        this.welcomeMsg = welcomeMsg;
        this.programName = String.format("%s> ", programName);
    }
    
    /**
     * Run this in a separate thread to prevent blocking the main thread.
     */
    @Override
    public void run()
    {
        System.out.println(welcomeMsg + System.lineSeparator());

        while (!Thread.currentThread().isInterrupted())
        {
            try
            {
                //System.out.print(programName);
                String command = br.readLine();
                String result = commandRegistry.execute(command);
                System.out.println(result + System.lineSeparator());
            } catch (IOException ex)
            {
                Thread.currentThread().interrupt();
                Logger.getLogger(CommandReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
