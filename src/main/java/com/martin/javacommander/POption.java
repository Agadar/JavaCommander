package com.martin.javacommander;

import com.martin.javacommander.translators.NoTranslator;
import com.martin.javacommander.translators.OptionTranslator;

/**
 * A command option parsed from an Option annotation.
 * 
 * @author marti
 * @param <T>
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
     * @return 
     */
    public final String getPrimaryName()
    {
        return Names[0];
    }
    
    /**
     * Convenience method for checking whether this option has a description.
     *
     * @return
     */
    public final boolean hasDescription()
    {
        return Description != null && !Description.isEmpty();
    }
    
    /**
     * Convenience method for checking whether this option has a translator set.
     * 
     * @return 
     */
    public final boolean hasTranslator()
    {
        return !Translator.equals(NoTranslator.class);
    }
}
