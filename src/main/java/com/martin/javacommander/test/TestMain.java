/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
        JavaCommander jc = new JavaCommander("Welcome to the testing zone", "testzone>");
        jc.registerObject(tc);
        jc.run();
    }
    
}
