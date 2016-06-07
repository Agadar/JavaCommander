package com.martin.javacommander;

/**
 * The definition of a command option
 *
 * @param <T> the type of this command option's value
 *
 * @author Martin
 */
public class CommandOption<T>
{

    /**
     * name of this command option, should be unique to the command
     */
    public final String Name;
    /**
     * description of this command option
     */
    public final String Description;
    /**
     * (default) value of this command option. The default value is ignored if
     * this command option is required, but still needs to be given so that the
     * type of it can be derived.
     */
    public final T Value;
    /**
     * whether or not this option is mandatory
     */
    public final boolean Mandatory;

    /**
     * @param name name of this command option, should be unique to the command
     * @param description description of this command option
     * @param defaultValue (default) value of this command option
     */
    public CommandOption(String name, String description, T defaultValue)
    {
        this(name, description, defaultValue, false);
    }

    /**
     * @param name name of this command option, should be unique to the command
     * @param description description of this command option
     * @param defaultValue (default) value of this command option
     * @param mandatory whether or not this option is mandatory
     */
    public CommandOption(String name, String description, T defaultValue, boolean mandatory)
    {
        this.Name = name;
        this.Description = description;
        this.Value = defaultValue;
        this.Mandatory = mandatory;
    }

    /**
     * Gets the type of this command option's value.
     *
     * @return the type of this command option's value
     */
    public Class getValueType()
    {
        return this.Value.getClass();
    }

    /**
     * Gives a copy of this command option, but with the given value.
     *
     * @param value the new value for the copy
     * @return a copy of this command option, but with the given value
     */
    public CommandOption<T> copyWithValue(T value)
    {
        return new CommandOption<>(this.Name, this.Description, value, this.Mandatory);
    }
}
