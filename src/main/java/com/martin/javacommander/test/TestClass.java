package com.martin.javacommander.test;

import com.martin.javacommander.annotations.Command;
import com.martin.javacommander.annotations.Option;

public class TestClass
{

//    @Command(names = "test")
//    public void TestMethod(
//            @Option(names = "-a", hasDefaultValue = false, defaultValue = "1")String input1, 
//            @Option(names = "-b", hasDefaultValue = true, defaultValue = "2")int input2, 
//            @Option(names = "-c", hasDefaultValue = false, defaultValue = "3")int input3)
//    {
//        System.out.println("Called with " + input1 + " " + input2 + " " + input3);
//    }
    @Command(names = "test", options =
    {
        @Option(names = "-a", hasDefaultValue = false, defaultValue = "1"),
        @Option(names = "-b", hasDefaultValue = true, defaultValue = "haha"),
        @Option(names = "-c", hasDefaultValue = false, defaultValue = "3")
    })
    public void TestMethod(String input1, int input2, int input3)            
    {
        System.out.
                println("Called with " + input1 + " " + input2 + " " + input3);
    }

}
