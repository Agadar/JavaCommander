package com.martin.javacommander.test;

import com.martin.javacommander.JavaCommanderException;
import com.martin.javacommander.annotations.Command;
import com.martin.javacommander.annotations.Option;

public class TestClass
{

    @Command(names = "test1", description = "How many lambs did who have?", options =
    {
        @Option(names = { "name", "alt" }, description = "Her name", hasDefaultValue = true, defaultValue = "Mary"),
        @Option(names = { "lambs" }, description = "# of lambs", hasDefaultValue = true, defaultValue = "5")
    })
    public void TestMethod(String name, int lambs) throws JavaCommanderException
    {
        System.out.println(name + " had " + lambs + " little lambs");
    }

}
