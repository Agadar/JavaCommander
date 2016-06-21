package com.martin.javacommander.test;

import com.martin.javacommander.JavaCommander;
import com.martin.javacommander.JavaCommanderException;

/**
 *
 * @author marti
 */
public class TestMain
{
  
    public static JavaCommander jc;
    public static TestClass tc1;
    public static TestClass2 tc2;
    
    /**
     * @param args the command line arguments
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public static void main(String[] args) throws JavaCommanderException
    {
        jc = new JavaCommander(true, "Welcome to the testing zone");
        tc1 = new TestClass();
        tc2 = new TestClass2();
        jc.registerObject(tc1);
        jc.run();
    }
}
