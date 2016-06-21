package com.martin.javacommander;

import com.martin.javacommander.translators.OptionTranslator;

/**
 * A command option parsed from an Option annotation.
 * 
 * @author marti
 * @param <T>
 */
public class POption<T>
{
    protected final String[] Names;
    protected final String Description;
    protected final boolean HasDefaultValue;
    protected final T DefaultValue;
    protected final Class<? extends OptionTranslator<T>> Translator;

    protected POption(String[] Names, String Description, boolean HasDefaultValue, T DefaultValue, Class<? extends OptionTranslator<T>> Translator)
    {
        this.Names = Names;
        this.Description = Description;
        this.HasDefaultValue = HasDefaultValue;
        this.DefaultValue = DefaultValue;
        this.Translator = Translator;
    }
}
