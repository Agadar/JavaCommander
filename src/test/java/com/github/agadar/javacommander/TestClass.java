package com.github.agadar.javacommander;

import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;
import com.github.agadar.javacommander.example.IntArrayTranslator;

/**
 * Used by JavaCommanderTest for testing
 * com.github.agadar.javacommander.JavaCommander.
 *
 * @author Agadar
 */
public class TestClass {

    @Command
    public void a() {
    }

    @Command(options
            = {
                @Option(hasDefaultValue = false)
            })
    public void b0(String arg0) {
    }

    @Command(options
            = {
                @Option(hasDefaultValue = true, defaultValue = "default")
            })
    public void b1(String arg0) {
    }

    @Command(options
            = {
                @Option(hasDefaultValue = false),
                @Option(hasDefaultValue = false, translator = IntArrayTranslator.class)
            })
    public void c0(String arg0, int[] arg1) {
    }

    @Command(options
            = {
                @Option(hasDefaultValue = true, defaultValue = "default"),
                @Option(hasDefaultValue = false, translator = IntArrayTranslator.class)
            })
    public void c1(String arg0, int[] arg1) {
    }

    @Command(options
            = {
                @Option(hasDefaultValue = true, defaultValue = "default"),
                @Option(hasDefaultValue = true, defaultValue = "1,2,3", translator = IntArrayTranslator.class)
            })
    public void c2(String arg0, int[] arg1) {
    }

    @Command
    public void d(
            @Option(hasDefaultValue = true, defaultValue = "default") String arg0,
            @Option(hasDefaultValue = false, translator = IntArrayTranslator.class) int[] arg1) {
    }

    @Command(names = JavaCommander.MASTER_COMMAND,
            options = @Option(hasDefaultValue = false))
    public void e(String arg0) {
    }
}
