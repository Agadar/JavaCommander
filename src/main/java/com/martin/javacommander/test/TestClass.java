package com.martin.javacommander.test;

import com.martin.javacommander.JavaCommanderException;
import com.martin.javacommander.annotations.Command;

public class TestClass
{

    @Command(names = "test1")
    public void TestMethod() throws JavaCommanderException            
    {
        TestMain.jc.registerObject(TestMain.tc2);
    }

}
