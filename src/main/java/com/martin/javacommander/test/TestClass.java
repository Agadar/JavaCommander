package com.martin.javacommander.test;

import com.martin.javacommander.annotations.CommandFunctionOption;
import com.martin.javacommander.annotations.CommandFunction;

public class TestClass
{

    @CommandFunction(names = "testcommand")
    public void TestMethod(
            @CommandFunctionOption(names = "-1", mandatory = false, defaultValue = "defaultdicks") String input1,
            @CommandFunctionOption(names = "-2", mandatory = true, defaultValue = "10") int input2)
    {
        System.out.println("Called with -1 = " + input1 + " and -2 = " + input2);
    }
}
