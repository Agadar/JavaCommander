package com.martin.javacommander.test;

import com.martin.javacommander.annotations.Command;
import com.martin.javacommander.annotations.CommandOption;

public class TestClass
{

    @Command(names = "testcommand")
    public void TestMethod(
            @CommandOption(names = "-1", mandatory = false, defaultValue = "defaultdicks") String input1,
            @CommandOption(names = "-2", mandatory = true, defaultValue = "10") int input2)
    {
        System.out.println("Called with -1 = " + input1 + " and -2 = " + input2);
    }
}
