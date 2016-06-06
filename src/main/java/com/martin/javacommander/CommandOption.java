package com.martin.javacommander;

/**
 * Represents a command parameter.
 * 
 * @author Martin
 * @param <T>
 */
public class CommandOption<T>
{
    public final String Name;
    public final String Description;
    /**
     * The (default) value of this command option.
     * The default value is ignored if this command option is required, but still
     * needs to be given so that the type of it can be derived.
     */
    public final T Value;
    public final boolean IsRequired;
    
    public CommandOption(String name, String description, T defaultValue)
    {
        this(name, description, defaultValue, false);
    }
    
    public CommandOption(String name, String description, T defaultValue, boolean isRequired)            
    {
        this.Name = name;
        this.Description = description;
        this.Value = defaultValue;
        this.IsRequired = isRequired;
    }
    
    /**
     * Returns the type of this command option's value.
     * 
     * @return 
     */
    public Class getValueType()
    {
        return this.Value.getClass();
    }
    
    /**
     * Gives a copy of this command option, but with the given value.
     * 
     * @param value
     * @return 
     */
    public CommandOption<T> copyWithValue(T value)
    {
        return new CommandOption<>(this.Name, this.Description, value, this.IsRequired);
    }
}
