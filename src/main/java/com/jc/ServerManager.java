package com.jc;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Top level class to manage all sessions.
 */
public class ServerManager {

    /** List of all sessions. */
    private final List<SessionThread> sessions = new LinkedList<>();

    /**
     * Main method to create a new session, and thereby a SessionTread then SessionHandler.
     * @param protocol HTTP or HTTPS
     * @param socket Connection to receive requests and send responses to.
     */
    public void createSession(String protocol, Socket socket) {
        SessionThread sessionThread;
        try {
            sessionThread = new SessionThread(protocol, socket, this);
            sessionThread.start();
            sessions.add(sessionThread);
        } catch (IOException e) {
            Logger.ERROR("ServerManager accept session error: " + e.getMessage());
        }
    }

    /**
     * Find sessions that are dead and remove them from the list.
     */
    public void discardDeadSessions() {
        List<SessionThread> deadSessions = new LinkedList<>();
        for(SessionThread sessionThread : sessions) {
            if(!sessionThread.isAlive()) {
                deadSessions.add(sessionThread);
            }
        }
        for(SessionThread deadThread : deadSessions) {
            sessions.remove(deadThread);
        }
    }

    public int killIdleSessions(long maxIdleSeconds) {
        int killCount = 0;
        for(SessionThread sessionThread : sessions) {
            if(sessionThread.beenIdleForHowLong() >= maxIdleSeconds) {
                sessionThread.interrupt();
                sessionThread.closeSocket();
                ++killCount;
            }
        }
        discardDeadSessions();
        return killCount;
    }

    public String listAllSessions() {
        int threadCount = 0;
        StringBuilder buffer = new StringBuilder();
        buffer.append("--------------------------------------\n");
        for(SessionThread sessionThread : sessions) {
            buffer.append(sessionThread.getThreadName() + "\n");
            buffer.append("  Alive:    " + sessionThread.isAlive() + "\n");
            buffer.append("  Protocol: " + sessionThread.getProtocol() + "\n");
            buffer.append("  Idle:     " + sessionThread.beenIdleForHowLong() + "\n");
            buffer.append("  Client:   " + sessionThread.getAddressAndPort() + "\n");
            buffer.append("  ------------------------------------\n");
            if(sessionThread.isAlive()) {
                threadCount++;
            }
        }
        buffer.append("Number of sessions Alive: " + threadCount + "\n");
        discardDeadSessions();
        return buffer.toString();
    }


}
