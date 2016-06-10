package com.martin.javacommander.test;

import com.martin.javacommander.annotations.Command;
import com.martin.javacommander.annotations.Option;

public class TestClass
{

    @Command(names = "test")
    public void TestMethod(
            @Option(names = "-1", mandatory = false, defaultValue = "defaultdicks") String input1,
            @Option(names = "-2", mandatory = true, defaultValue = "10") int input2)
    {
        System.out.println("Called with -1 = " + input1 + " and -2 = " + input2);
    }
}
