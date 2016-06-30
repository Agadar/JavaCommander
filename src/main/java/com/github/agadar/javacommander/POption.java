package com.github.agadar.javacommander;

import com.github.agadar.javacommander.translator.NoTranslator;
import com.github.agadar.javacommander.translator.OptionTranslator;

/**
 * A command option parsed from an Option annotation.
 * 
 * @author Agadar
 * @param <T> the type of this option's value
 */
public class POption<T>
{
    public final String[] Names;
    public final String Description;
    public final boolean HasDefaultValue;
    public final Class<T> Type;
    public final T DefaultValue;
    public final Class<? extends OptionTranslator<T>> Translator;

    public POption(String[] Names, String Description, boolean HasDefaultValue, 
            Class<T> Type, T DefaultValue, Class<? extends OptionTranslator<T>> Translator)
    {
        this.Names = Names;
        this.Description = Description;
        this.HasDefaultValue = HasDefaultValue;
        this.Type = Type;
        this.DefaultValue = DefaultValue;
        this.Translator = Translator;
    }
    
    /**
     * Convenience method for getting the primary name.
     * 
     * @return this option's primary name
     */
    public final String getPrimaryName()
    {
        return Names[0];
    }
    
    /**
     * Convenience method for checking whether this option has a description.
     *
     * @return whether this option has a description
     */
    public final boolean hasDescription()
    {
        return Description != null && !Description.isEmpty();
    }
    
    /**
     * Convenience method for checking whether this option has a translator set.
     * 
     * @return whether this option has a translator set
     */
    public final boolean hasTranslator()
    {
        return !Translator.equals(NoTranslator.class);
    }
}
