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
        jc.registerCommands(tc);
        //jc.execute2("testcommand -1 inputdicks -2 22 -3");
        jc.execute2("testcommand -2 12 -1 inputdicks");
        //jc.execute2("testcommand");
        //jc.execute2("nonexistantcommand");
    }
    
}
