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
    
    /**
     * @param args the command line arguments
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public static void main(String[] args) throws JavaCommanderException
    {
        jc = new JavaCommander(true, "Welcome to the testing zone");
        tc1 = new TestClass();
        jc.registerObject(tc1);
        jc.run();
    }
}
