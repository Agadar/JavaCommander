package com.martin.javacommander;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A command parsed from a Command annotation.
 *
 * @author marti
 */
public class PCommand
{
    protected final String[] Names;
    protected final String Description;
    protected final List<POption> Options;              // list of the options, in order of the method's parameters
    protected final Map<String, POption> OptionsMapped; // options mapped to option names, including synonyms
    protected final Method ToInvoke;
    protected final Object ToInvokeOn;

    protected PCommand(String[] Names, String Description, List<POption> Options,
            Method ToInvoke, Object ToInvokeOn)
    {
        this.Names = Names;
        this.Description = Description;
        this.Options = Options;
        this.ToInvoke = ToInvoke;
        this.ToInvokeOn = ToInvokeOn;
        this.OptionsMapped = new HashMap<>();

        // Map all options
        Options.stream().forEach((option) ->
        {
            for (String name : option.Names)
            {
                OptionsMapped.put(name, option);
            }
        });
    }

    protected void execute(Object[] args) throws IllegalAccessException,
                                                 IllegalArgumentException,
                                                 InvocationTargetException
    {
        ToInvoke.invoke(ToInvokeOn, args);
    }

}
