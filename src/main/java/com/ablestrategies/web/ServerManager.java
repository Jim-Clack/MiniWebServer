package com.ablestrategies.web;

import com.ablestrategies.web.conn.ConnectionThread;
import com.ablestrategies.web.conn.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
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

    /** Support for the server management console. */
    private final ConsoleSupport console;

    /** HTTP and HTTPS listeners. */
    private final Map<String, ListenerThread> listeners = new HashMap<>();

    /**
     * Ctor.
     */
    public ServerManager() {
        console = new ConsoleSupport(this);
        new IdleLoopThread(this).start();
    }

    /**
     * Keep track of a listener thread.
     * @param protocol HTTP or HTTPS.
     * @param thread The listener thread.
     */
    public void setListener(String protocol, ListenerThread thread) {
        listeners.put(protocol, thread);
    }

    /**
     * Get the listeners.
     * @return Map of protocols and listeners.
     */
    public Map<String, ListenerThread> getListeners() {
        return listeners;
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
     * Get the session handler over all sessions.
     * @return The session handler that tracks all user/browser sessions.
     */
    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }

    /**
     * Get the console support object.
     * @return A consoleSupport object that can provide human-readable interactivity.
     */
    public ConsoleSupport getConsole() {
        return console;
    }

    /**
     * A list of all active socket connections.
     * @return List of ConnectionThreads.
     */
    public List<ConnectionThread> getConnections() {
        return connections;
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

}
