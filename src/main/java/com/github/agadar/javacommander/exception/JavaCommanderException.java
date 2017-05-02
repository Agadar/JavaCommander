package com.github.agadar.javacommander.exception;

/**
 * Parent class for all custom exceptions thrown by JavaCommander.
 * 
 * @author Agadar (https://github.com/Agadar/)
 */
public class JavaCommanderException extends RuntimeException {

    public JavaCommanderException() {
    }

    public JavaCommanderException(String string) {
        super(string);
    }

    public JavaCommanderException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public JavaCommanderException(Throwable thrwbl) {
        super(thrwbl);
    }

    public JavaCommanderException(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }
}
