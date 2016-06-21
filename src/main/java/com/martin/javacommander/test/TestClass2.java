package com.martin.javacommander.test;

import com.martin.javacommander.JavaCommanderException;
import com.martin.javacommander.annotations.Command;

public class TestClass2
{

    @Command(names = "test2")
    public void TestMethod() throws JavaCommanderException            
    {
        TestMain.jc.unregisterObject(TestMain.tc2);
    }

}
