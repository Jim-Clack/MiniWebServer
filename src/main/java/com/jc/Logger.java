package com.jc;

/**
 * Sloppy, just for testing - needs to be replaced.
 */
public class Logger {

    private static Logger instance = new Logger();

    public void log(int level, String message, Throwable throwable) {
        System.err.println(Thread.currentThread().getName() + ">>> " + message);
    }

    public static void INFO(String message) {
        instance.log(0, message, null);
    }
}
