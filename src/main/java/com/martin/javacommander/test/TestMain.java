package com.martin.javacommander.test;

import com.martin.javacommander.JavaCommander;
import com.martin.javacommander.JavaCommanderException;

/**
 *
 * @author marti
 */
public class TestMain
{
    private enum testEnum{
        ONE,
        TWO,
        THREE
    }
    
    /**
     * @param args the command line arguments
     * @throws com.martin.javacommander.JavaCommanderException
     */
    public static void main(String[] args) throws JavaCommanderException
    {
        TestClass tc = new TestClass();
        JavaCommander jc = new JavaCommander(true, "Welcome to the testing zone");
        jc.registerObject(tc);
        jc.run();
        
        
        testEnum derp = null;
   
        
        //System.out.println(Boolean.valueOf("TRUE"));
    }

    public static void derp(String[] dicks)
    {

    }

}
