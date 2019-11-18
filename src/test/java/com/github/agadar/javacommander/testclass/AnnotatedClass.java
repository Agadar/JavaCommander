package com.github.agadar.javacommander.testclass;

import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;

import lombok.Getter;

/**
 * Test class containing several annotated methods.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class AnnotatedClass {

    /**
     * The last arguments passed to any of this object's methods.
     */
    @Getter
    private static Object[] latestArguments = new Object[0];

    @Command(names = { "Bar", "bar" }, description = "barDescription")
    public void bar() {
        latestArguments = new Object[0];
    }

    @Command(names = { "BarWithParams", "barWithParams" }, description = "barWithParamsDescription", options = {
            @Option(names = { "StringParam", "stringParam" }, description = "stringParamDescription"),
            @Option(names = { "IntParam", "intParam" }, description = "intParamDescription"),
            @Option(names = { "BoolParam", "boolParam" }, description = "boolParamDescription") })
    public void barWithParams(String stringParam, int intParam, boolean boolParam) {
        latestArguments = new Object[] { stringParam, intParam, boolParam };
    }

    @Command(names = { "BarWithDefaultParams",
            "barWithDefaultParams" }, description = "barWithDefaultParamsDescription", options = {
                    @Option(names = { "StringDefaultParam",
                            "stringDefaultParam" }, description = "stringDefaultParamDescription", defaultValue = "defaultString"),
                    @Option(names = { "IntDefaultParam",
                            "intDefaultParam" }, description = "intDefaultParamDescription", defaultValue = "15"),
                    @Option(names = { "BoolDefaultParam",
                            "boolDefaultParam" }, description = "boolDefaultParamDescription", defaultValue = "true") })
    public void barWithDefaultParams(String stringParam, int intParam, boolean boolParam) {
        latestArguments = new Object[] { stringParam, intParam, boolParam };
    }

    @Command(names = { "BarWithBazParam", "barWithBazParam" }, description = "barWithBazParamDescription", options = {
            @Option(names = { "BazParam",
                    "bazParam" }, description = "bazParamDescription", defaultValue = "defaultBaz", valueParser = DataClassOptionValueParser.class) })
    public void barWithBazParam(DataClass baz) {
        latestArguments = new Object[] { baz };
    }

    @Command(description = "barNamelessDescription", options = {
            @Option(description = "barNamelessParamDescription") })
    public void barNameless(String stringParam) {
        latestArguments = new Object[] { stringParam };
    }

    @Command(names = { "barWithFlags", "BarWithFlags" }, description = "barWithFlags", options = {
            @Option(names = { "stringParam", "StringParam" }),
            @Option(names = { "flag1", "Flag1" }, defaultValue = "false", flagValue = "true"),
            @Option(names = { "flag2", "Flag2" }, defaultValue = "true", flagValue = "false") })
    public void barWithFlags(String stringParam, boolean flag1, boolean flag2) {
        latestArguments = new Object[] { stringParam, flag1, flag2 };
    }

    @Command(names = { "BarStatic", "barStatic" }, description = "barStaticDescription")
    public static void barStatic() {
        latestArguments = new Object[0];
    }
}
