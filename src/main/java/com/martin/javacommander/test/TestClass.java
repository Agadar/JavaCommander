package com.martin.javacommander.test;

import com.martin.javacommander.annotations.CommandOption;
import com.martin.javacommander.annotations.CommandFunction;

public class TestClass
{

    @CommandFunction(names = "testcommand", description = "test description")
    public void TestMethod(@CommandOption(names = "-i", description = "the input", mandatory = false) String input)
    {
        System.out.println("Called with " + input);
    }
}
