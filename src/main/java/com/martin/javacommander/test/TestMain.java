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
        JavaCommander jc = new JavaCommander("Welcome to the testing zone");
        jc.registerObject(tc);
        jc.run();
        //jc.execute("testcommand -1 inputdicks -2 22 -3");
        //jc.execute("testcommand -2 lel -1 inputdicks");
        //jc.execute("testcommand");
        //jc.execute("nonexistantcommand");
    }
    
}
