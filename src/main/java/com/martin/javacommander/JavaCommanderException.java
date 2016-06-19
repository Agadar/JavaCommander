package com.martin.javacommander;

/**
 * Custom exception for JavaCommander. Any and all exceptions thrown by
 * JavaCommander should preferably be of this type.
 * 
 * @author marti
 */
public class JavaCommanderException extends Exception
{
    public JavaCommanderException()
    {
        super();
    }

    public JavaCommanderException(String message)
    {
        super(message);
    }

    public JavaCommanderException(Throwable cause)
    {
        super(cause);
    }

    public JavaCommanderException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
