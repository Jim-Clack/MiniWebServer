package com.ablestrategies.web;

import com.ablestrategies.web.conn.ConnectionThread;
import com.ablestrategies.web.conn.SessionContext;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConsoleSupport {

    /**
     * For sorting Threads.
     */
    class ThreadComparator implements Comparator<Thread> {
        @Override
        public int compare(Thread n1, Thread n2) {
            return CharSequence.compare(n1.getName(), n2.getName());
        }
    }

    /** We need to get a lot of data from our server manager. */
    private final ServerManager manager;

    /** Paragraph separator. */
    private static final String dashes = "--------------------------------------\n";

    /**
     * Ctor.
     * @param manager The server manager who is aware of all sessions and connections.
     */
    public ConsoleSupport(ServerManager manager) {
        this.manager = manager;
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
        String checkMark = "  ";
        for (Thread thread : threadList) {
            String classPath = "";
            StackTraceElement[] element = thread.getStackTrace();
            if (element.length > 2) {
                classPath = element[2].getClassName();
                classPath = classPath.substring(classPath.lastIndexOf(".") + 1);
            }
            if(thread.getName().startsWith("WebServer ") && !checkMark.contains("*")) {
                checkMark = " *";
                buffer.append(dashes);
            }
            buffer.append(String.format("%s%-37s%-14s%s\n", checkMark, thread.getName(), thread.getState(), classPath));
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
     *
     * @return Multi-line string.
     */
    @SuppressWarnings("all")
    public synchronized String listAllConnections() {
        int threadCount = 0;
        StringBuilder buffer = new StringBuilder();
        buffer.append(dashes);
        for (ConnectionThread connection : manager.getConnections()) {
            buffer.append(connection.getThreadName() + "\n");
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

}
