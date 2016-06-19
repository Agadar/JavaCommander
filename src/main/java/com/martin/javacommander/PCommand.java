package com.martin.javacommander;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A command parsed from a Command annotation.
 * 
 * @author marti
 */
class PCommand
{
    protected final String[] Names;
    protected final String Description;
    protected final POption[] Options;
    protected final Method ToInvoke;
    protected final Object ToInvokeOn;

    protected PCommand(String[] Names, String Description, POption[] Options, Method ToInvoke, Object ToInvokeOn)
    {
        this.Names = Names;
        this.Description = Description;
        this.Options = Options;
        this.ToInvoke = ToInvoke;
        this.ToInvokeOn = ToInvokeOn;
    }
    
    protected void execute(Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        ToInvoke.invoke(ToInvokeOn, args);
    }
}
