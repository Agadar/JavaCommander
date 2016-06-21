package com.martin.javacommander.test;

import com.martin.javacommander.JavaCommanderException;
import com.martin.javacommander.annotations.Command;
import com.martin.javacommander.annotations.Option;

public class TestClass
{

    @Command(names = "test1", options = {@Option(names = {"name", "alt"})})
    public void TestMethod(String name) throws JavaCommanderException            
    {
        System.out.println(name + " had a little lamb");
    }

}
