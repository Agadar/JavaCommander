package com.martin.javacommander.test;

import com.martin.javacommander.annotations.Command;
import com.martin.javacommander.annotations.Option;

public class TestClass
{

    @Command(options =
             {
                 @Option(names = { "2", "two" }, defaultValue = "2")
             })
    public void TestMethod(@Option(names = { "1", "one" }, defaultValue = "1") String input1,
                           int input2,
                           @Option(names = {"3", "three" }, hasDefaultValue = true, defaultValue = "3") int input3)
    {
        System.out.println("Called with " + input1 + " " + input2 + " " + input3);
    }

//    @Command(names = "test")
//    public void TestMethod(
//            @Option(names = "-1", hasDefaultValue = false, defaultValue = "1")String input1, 
//            @Option(names = "-2", hasDefaultValue = false, defaultValue = "2")int input2, 
//            @Option(names = "-3", hasDefaultValue = false, defaultValue = "3")int input3)
//    {
//        System.out.println("Called with " + input1 + " " + input2 + " " + input3);
//    }
}
