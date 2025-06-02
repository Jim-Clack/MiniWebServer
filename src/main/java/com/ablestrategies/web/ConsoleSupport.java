package com.ablestrategies.web;

import com.ablestrategies.web.conn.ConnectionThread;
import com.ablestrategies.web.conn.SessionContext;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConsoleSupport {

    /**
     * For sorting Threads based ion name and whether they are part of this (WebServer) group.
     */
    @SuppressWarnings("ALL")
    class ThreadComparator implements Comparator<Thread> {
        @Override
        public int compare(Thread n1, Thread n2) {
            if(n1.getThreadGroup() == n2.getThreadGroup()) {
                return CharSequence.compare(n1.getName(), n2.getName());
            }
            if(n1.getThreadGroup() == ConsoleSupport.threadGroup) {
                return 1;
            }
            return -1;
        }
    }

    /** Used to see if a thread is a member of this group */
    static ThreadGroup threadGroup = null;

    /** We need to get a lot of data from our server manager. */
    private final ServerManager manager;

    /** Section separator. */
    private static final String dashes = "--------------------------------------------------------------\n";

    /**
     * Ctor.
     * @param manager The server manager who is aware of all sessions and connections.
     */
    public ConsoleSupport(ServerManager manager) {
        this.manager = manager;
        ConsoleSupport.threadGroup = Thread.currentThread().getThreadGroup();
    }

    public String getMenu() {
        return "[C]onnections, [S]essions, [T]hreads, [P]roperties, [K]illIdle60, [L]ogLevel [Q]uit";
    }

    /**
     * Change the log level.
     * @return Descriptive string.
     */
    @SuppressWarnings("ALL")
    public String toggleLogLevel() {
        StringBuilder buffer = new StringBuilder();
        boolean found = false;
        buffer.append(dashes);
        String message = toggleJavaLogger();
        if(message != null) {
            buffer.append(message);
            found = true;
        }
        // <-- Add other Logger here, and so forth
        if(!found) {
            buffer.append("Cannot access any logger");
        }
        buffer.append(dashes);
        return buffer.toString();
    }

    /**
     * Kill connections and sessions that have been idle 60 seconds or longer.
     * @return Multi-line string.
     */
    @SuppressWarnings("all")
    public String killIdleClients() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(dashes);
        buffer.append("Killing connections and sessions idle 60 seconds or more...\n");
        int countConnections = manager.killIdleConnections(60);
        int countSessions = manager.discardIdleSessions(60);
        buffer.append(dashes);
        buffer.append("  Number of connections killed: " + countConnections + "\n");
        buffer.append("  Number of sessions killed: " + countSessions + "\n");
        buffer.append(dashes);
        return buffer.toString();
    }

    /**
     * List all threads running in this JVM.
     * @return Multi-line string.
     */
    @SuppressWarnings("all")
    public String listAllThreads() {
        int threadCount = 0;
        StringBuilder buffer = new StringBuilder();
        buffer.append(dashes);
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        List<Thread> threadList = threadSet.stream().sorted(new ThreadComparator()).collect(Collectors.toList());
        boolean wasThisThreadGroup = false;
        for (Thread thread : threadList) {
            String classPath = "";
            StackTraceElement[] element = thread.getStackTrace();
            if (element.length > 2) {
                classPath = element[2].getClassName();
                classPath = classPath.substring(classPath.lastIndexOf(".") + 1);
            }
            boolean isThisThreadGroup = thread.getThreadGroup().equals(Thread.currentThread().getThreadGroup());
            if(wasThisThreadGroup != isThisThreadGroup) {
                buffer.append(dashes);
            }
            String checkMark = isThisThreadGroup ? "*" : " ";
            wasThisThreadGroup = isThisThreadGroup;
            buffer.append(String.format(" %s%-31s%-14s%s\n", checkMark, thread.getName(), thread.getState(), classPath));
            ++threadCount;
        }
        buffer.append(dashes);
        buffer.append("Number of threads: " + threadCount + "\n");
        return buffer.toString();
    }

    /**
     * Assemble a man-readable list of sessions.
     *
     * @return Multiline text.
     */
    @SuppressWarnings("ALL")
    public synchronized String listAllSessions() {
        int sessionCount = 0;
        StringBuilder buffer = new StringBuilder();
        buffer.append(dashes);
        for (SessionContext context : manager.getSessionHandler().getSessions()) {
            buffer.append("Session:  " + context.getSessionId() + "\n");
            buffer.append("Created:  " + context.getCreated().toString() + "\n");
            buffer.append("Idle:     " + context.beenIdleForHowLong() + "\n");
            Map<String, String> values = context.getStringValues();
            for (String key : values.keySet()) {
                buffer.append(" -" + key + " = " + values.get(key) + "\n");
            }
            for (ConnectionThread connection : manager.getConnections()) {
                List<String> history = connection.getHistory();
                if (history != null && !history.isEmpty() && history.get(0).contains(context.getSessionId())) {
                    buffer.append(" *" + connection.getThreadName() + "\n");
                }
            }
            buffer.append(dashes);
            sessionCount++;
        }
        buffer.append("Number of sessions: " + sessionCount + "\n");
        return buffer.toString();
    }

    /**
     * List all connections that are presentm, whether alive or dead.
     * @return Multi-line string.
     */
    @SuppressWarnings("all")
    public synchronized String listAllConnections() {
        int threadCount = 0;
        StringBuilder buffer = new StringBuilder();
        buffer.append(dashes);
        for (ConnectionThread connection : manager.getConnections()) {
            buffer.append("Thread:   " + connection.getThreadName() + "\n");
            buffer.append("Alive:    " + connection.isAlive() + "\n");
            buffer.append("Protocol: " + connection.getProtocol() + "\n");
            buffer.append("Idle:     " + connection.beenIdleForHowLong() + "\n");
            buffer.append("Client:   " + connection.getAddressAndPort() + "\n");
            List<String> history = connection.getHistory();
            for (String historyLine : history) {
                buffer.append(" >" + historyLine + "\n");
            }
            buffer.append(dashes);
            if (connection.isAlive()) {
                threadCount++;
            }
        }
        buffer.append("Number of connections Alive: " + threadCount + "\n");
        manager.discardDeadConnections();
        return buffer.toString();
    }

    /**
     * List all addresses and ports, server first, then clients.
     * @return Multi-line string.
     */
    @SuppressWarnings("all")
    public synchronized String listProperties() {
        StringBuilder buffer = new StringBuilder();
        listEnvironment(buffer);
        listProperties(buffer);
        ListPreferences(buffer);
        listIpAddresses(buffer);
        buffer.append(dashes);
        return buffer.toString();
    }

    /**
     * Change the log level for the standard Java Logger.
     * @return null if we're not using that logger. Else a descriptive message.
     */
    private String toggleJavaLogger() {
        String packageName = this.getClass().getPackage().getName();
        String message = null;
        Logger logger = Logger.getLogger("");
        if(logger != null) {
            Level[] levels = { Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE, Level.FINEST };
            Level level = logger.getLevel();
            for(int i = 0; i < levels.length; i++) {
                if(level.intValue() >= levels[i].intValue()) {
                    level = levels[(i + 1) % levels.length];
                    break;
                }
            }
            logger.setLevel(level);
            message = "Java Logger level set to " + level.toString() + "(" + level.intValue() + ")\n";
        }
        return message;
    }

    private static void listEnvironment(StringBuilder buffer) {
        buffer.append(dashes);
        buffer.append("Environment\n");
        Map<String, String> env = System.getenv();
        for(Map.Entry<String, String> entry : env.entrySet()) {
            buffer.append(" " + entry.getKey() + " = " + entry.getValue() + "\n");
        }
    }

    private static void listProperties(StringBuilder buffer) {
        buffer.append(dashes);
        buffer.append("Properties\n");
        Properties properties = System.getProperties();
        for(Object property : properties.keySet()) {
            buffer.append(" " + property.toString() + ": " + properties.get(property).toString().
                    replaceAll("\\x0d", "[CR]").replaceAll("\\x0A", "[LF]") + "\n");
        }
    }

    private static void ListPreferences(StringBuilder buffer) {
        buffer.append(dashes);
        buffer.append("Preferences\n");
        buffer.append(" Web root path directory: " + Preferences.getInstance().getRootPath() + "\n");
        buffer.append(" HTTP IP port number: " + Preferences.getInstance().getPortNumber() + "\n");
        buffer.append(" HTTPS IP port number: " + Preferences.getInstance().getSslPortNumber() + "\n");
        buffer.append(" Max history per connection: " + Preferences.getInstance().getMaxHistory() + "\n");
        buffer.append(" Connection max idle seconds: " + Preferences.getInstance().getConnectionMaxIdleSeconds() + "\n");
        buffer.append(" Session max idle seconds: " + Preferences.getInstance().getSessionMaxIdleSeconds() + "\n");
    }

    private void listIpAddresses(StringBuilder buffer) {
        buffer.append(dashes);
        buffer.append("IPAddresses\n");
        Map<String, ListenerThread> listeners = manager.getListeners();
        for(String protocol : listeners.keySet()) {
            buffer.append(" Server: " + protocol + " ==> IP Addr:/" + listeners.get(protocol).getAddressAndPort() + "\n");
        }
        for (ConnectionThread connection : manager.getConnections()) {
            buffer.append(" Client: " + connection.getProtocol() + " ==> IP Addr:" + connection.getAddressAndPort() + "\n");
        }
    }

}
