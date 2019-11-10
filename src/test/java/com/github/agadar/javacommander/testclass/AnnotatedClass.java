package com.github.agadar.javacommander.testclass;

import com.github.agadar.javacommander.annotation.Command;
import com.github.agadar.javacommander.annotation.Option;

/**
 * Test class containing several annotated methods.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class AnnotatedClass {

    @Command(names = { "Bar", "bar" }, description = "barDescription")
    public void bar() {
    }

    @Command(names = { "BarWithParams", "barWithParams" }, description = "barWithParamsDescription", options = {
            @Option(names = { "StringParam", "stringParam" }, description = "stringParamDescription"),
            @Option(names = { "IntParam", "intParam" }, description = "intParamDescription"),
            @Option(names = { "BoolParam", "boolParam" }, description = "boolParamDescription") })
    public void barWithParams(String stringParam, int intParam, boolean boolParam) {
    }

    @Command(names = { "BarWithDefaultParams",
            "barWithDefaultParams" }, description = "barWithDefaultParamsDescription", options = {
                    @Option(names = { "StringDefaultParam",
                            "stringDefaultParam" }, description = "stringDefaultParamDescription", hasDefaultValue = true, defaultValue = "defaultString"),
                    @Option(names = { "IntDefaultParam",
                            "intDefaultParam" }, description = "intDefaultParamDescription", hasDefaultValue = true, defaultValue = "15"),
                    @Option(names = { "BoolDefaultParam",
                            "boolDefaultParam" }, description = "boolDefaultParamDescription", hasDefaultValue = true, defaultValue = "true") })
    public void barWithDefaultParams(String stringParam, int intParam, boolean boolParam) {
    }

    @Command(names = { "BarWithBazParam", "barWithBazParam" }, description = "barWithBazParamDescription", options = {
            @Option(names = { "BazParam",
                    "bazParam" }, description = "bazParamDescription", hasDefaultValue = true, defaultValue = "defaultBaz", valueParser = DataClassOptionValueParser.class) })
    public void barWithBazParam(DataClass baz) {
    }

    @Command(description = "barNamelessDescription", options = {
            @Option(description = "barNamelessParamDescription") })
    public void barNameless(String stringParam) {
    }

    @Command(names = { "BarStatic", "barStatic" }, description = "barStaticDescription")
    public static void barStatic() {
    }
}
