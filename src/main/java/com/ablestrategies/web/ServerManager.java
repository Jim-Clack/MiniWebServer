package com.ablestrategies.web;

import com.ablestrategies.web.conn.ConnectionThread;
import com.ablestrategies.web.conn.SessionContext;
import com.ablestrategies.web.conn.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Top level class to manage all connections.
 */
public class ServerManager {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(ServerManager.class);

    /** List of all connections. */
    private final List<ConnectionThread> connections = new LinkedList<>();

    /** For tracking user/browser sessions. */
    private final SessionHandler sessionHandler = new SessionHandler();

    /** Paragraph separator. */
    private static final String dashes = "--------------------------------------\n";

    /**
     * Ctor.
     */
    public ServerManager() {
       new IdleLoopThread(this).start();
    }

    /**
     * Main method to create a new connection, and thereby a ConnectionThread/Handler.
     * @param protocol HTTP or HTTPS
     * @param socket Connection to receive requests and send responses to.
     */
    public void createConnection(String protocol, Socket socket) {
        ConnectionThread connection;
        try {
            connection = new ConnectionThread(protocol, socket, this);
            connection.start();
            connections.add(connection);
        } catch (IOException e) {
            logger.error("ServerManager accept() connection error", e);
        }
    }

    /**
     * Get the session manager over all sessions.
     * @return The session manager that tracks all user/browser sessions.
     */
    public SessionHandler getSessionManager() {
        return sessionHandler;
    }

    /**
     * Find connections that are dead and remove them from the list.
     */
    public void discardDeadConnections() {
        List<ConnectionThread> deadConnections = new LinkedList<>();
        for(ConnectionThread connection : connections) {
            if(!connection.isAlive()) {
                deadConnections.add(connection);
            }
        }
        for(ConnectionThread deadThread : deadConnections) {
            connections.remove(deadThread);
        }
    }

    /**
     * Find sessions that are old and remove them from the list.
     */
    public void discardIdleSessions(long maxIdleSeconds) {
        sessionHandler.deleteSessionsIfIdle(maxIdleSeconds);
    }

    /**
     * Kill idle connections - with no requests/responses for a while.
     * @param maxIdleSeconds Number of seconds of idle time to tolerate.
     * @return Number of connections killed.
     */
    public int killIdleConnections(long maxIdleSeconds) {
        int killCount = 0;
        for(ConnectionThread connection : connections) {
            if(connection.beenIdleForHowLong() >= maxIdleSeconds) {
                connection.interrupt();
                connection.closeSocket();
                ++killCount;
            }
        }
        discardDeadConnections();
        return killCount;
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
        for(Thread thread : Thread.getAllStackTraces().keySet()) {
            String classPath = "";
            StackTraceElement[] element = thread.getStackTrace();
            if(element.length > 2) {
                classPath = element[2].getClassName();
                classPath = classPath.substring(classPath.lastIndexOf(".") + 1);
            }
            String checkMark = thread.getName().startsWith("WebServer ") ? "*" : " ";
            buffer.append(String.format("%s%-37s%-14s%s\n", checkMark, thread.getName(), thread.getState(), classPath));
            ++threadCount;
        }
        buffer.append(dashes);
        buffer.append("Number of threads: " + threadCount + "\n");
        return buffer.toString();
    }

    /**
     * Assemble a man-readable list of sessions.
     * @return Multiline text.
     */
    @SuppressWarnings("ALL")
    public synchronized String listAllSessions() {
        int sessionCount = 0;
        StringBuilder buffer = new StringBuilder();
        buffer.append(dashes);
        for (SessionContext context : sessionHandler.getSessions()) {
            buffer.append("  Session:  " + context.getSessionId() + "\n");
            buffer.append("  IsFresh:  " + context.isFresh() + "\n");
            Map<String, String> values = context.getStringValues();
            for(String key : values.keySet()) {
                buffer.append("  " + key + " = " + values.get(key) + "\n");
            }
            for(ConnectionThread connection : connections) {
                List<String> history = connection.getHistory();
                if(history != null && !history.isEmpty() && history.get(0).contains(context.getSessionId())) {
                    buffer.append("  " + connection.getThreadName() + "\n");
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
        for(ConnectionThread connection : connections) {
            buffer.append(connection.getThreadName() + "\n");
            buffer.append("  Alive:    " + connection.isAlive() + "\n");
            buffer.append("  Protocol: " + connection.getProtocol() + "\n");
            buffer.append("  Idle:     " + connection.beenIdleForHowLong() + "\n");
            buffer.append("  Client:   " + connection.getAddressAndPort() + "\n");
            List<String> history = connection.getHistory();
            for(String historyLine : history) {
                buffer.append("  " + historyLine + "\n");
            }
            buffer.append(dashes);
            if(connection.isAlive()) {
                threadCount++;
            }
        }
        buffer.append("Number of connections Alive: " + threadCount + "\n");
        discardDeadConnections();
        return buffer.toString();
    }

}
