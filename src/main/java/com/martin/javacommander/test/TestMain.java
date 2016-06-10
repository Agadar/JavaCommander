package com.martin.javacommander.test;

import com.martin.javacommander.JavaCommander;

/**
 *
 * @author marti
 */
public class TestMain
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        TestClass tc = new TestClass();
        JavaCommander jc = new JavaCommander(true, "Welcome to the testing zone");
        jc.registerObject(tc);
        jc.execute("test");
        jc.run();
    
    }

}
