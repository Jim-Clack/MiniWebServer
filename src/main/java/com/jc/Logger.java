package com.jc;

/**
 * Sloppy, just for testing - needs to be replaced.
 */
public class Logger {

    public static int LOGLEVEL_TRACE = 0;
    public static int LOGLEVEL_DEBUG = 1;
    public static int LOGLEVEL_INFO = 2;
    public static int LOGLEVEL_WARN = 3;
    public static int LOGLEVEL_ERROR = 4;

    private static final Logger instance = new Logger();

    private int level = LOGLEVEL_WARN;

    private Logger() {
        // sloppy singleton
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void log(int level, String message, Throwable throwable) {
        if(level <= this.level) {
            return;
        }
        System.err.println(Thread.currentThread().getName() + ">>> " + message);
    }

    public static void TRACE(String message) {
        instance.log(LOGLEVEL_TRACE, message, null);
    }
    public static void DEBUG(String message) {
        instance.log(LOGLEVEL_DEBUG, message, null);
    }
    public static void INFO(String message) {
        instance.log(LOGLEVEL_INFO, message, null);
    }
    public static void WARN(String message) {
        instance.log(LOGLEVEL_WARN, message, null);
    }
    public static void ERROR(String message) {
        instance.log(LOGLEVEL_ERROR, message, null);
    }
}
