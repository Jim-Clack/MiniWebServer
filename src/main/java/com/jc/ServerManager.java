package com.jc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Top level class to manage all sessions.
 */
public class ServerManager {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(ServerManager.class);

    /** List of all sessions. */
    private final List<SessionThread> sessions = new LinkedList<>();

    /**
     * Ctor.
     */
    public ServerManager() {
       new IdleChecker(this).start();
    }

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
            logger.error("ServerManager accept() session error", e);
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

    /**
     * Kill idle sessions - with no requests/responses for a while.
     * @param maxIdleSeconds Number of seconds of idle time to tolerate.
     * @return Number of sessions killed.
     */
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

    /**
     * List all threads running in this JVM.
     * @return Multi-line string.
     */
    public String listAllThreads() {
        int threadCount = 0;
        StringBuilder buffer = new StringBuilder();
        for(Thread thread : Thread.getAllStackTraces().keySet()) {
            buffer.append(thread.getName() + " ");
            buffer.append(thread.getState().toString() + "\n");
            ++threadCount;
        }
        buffer.append("Number of threads: " + threadCount + "\n");
        return buffer.toString();
    }

    /**
     * List all sessions running in server.
     * @return Multi-line string.
     */
    @SuppressWarnings("all")
    public String listAllSessions() {
        int threadCount = 0;
        StringBuilder buffer = new StringBuilder();
        buffer.append("--------------------------------------\n");
        for(SessionThread sessionThread : sessions) {
            String historyHeader = "  History:  ";
            List<String> history = sessionThread.getHistory();
            buffer.append(sessionThread.getThreadName() + "\n");
            buffer.append("  Alive:    " + sessionThread.isAlive() + "\n");
            buffer.append("  Protocol: " + sessionThread.getProtocol() + "\n");
            buffer.append("  Idle:     " + sessionThread.beenIdleForHowLong() + "\n");
            buffer.append("  Client:   " + sessionThread.getAddressAndPort() + "\n");
            for(String historyLine : history) {
                buffer.append(historyHeader + historyLine + "\n");
                historyHeader = "            ";
            }
            buffer.append("  ------------------------------------\n");
            if(sessionThread.isAlive()) {
                threadCount++;
            }
        }
        buffer.append("Number of sessions Alive: " + threadCount + "\n");
        discardDeadSessions();
        return buffer.toString();
    }

    /**
     * This kills idle sessions periodically.
     */
    static class IdleChecker extends Thread {
        private final ServerManager manager;
        IdleChecker(ServerManager manager) {
            this.manager = manager;
            setDaemon(true);
        }
        @SuppressWarnings("all")
        public void run() {
            setName("IdleCheckerThread");
            while(!isInterrupted()) {
                try {
                    sleep(10000); // check every 10 seconds
                } catch (InterruptedException e) {
                    // ignore interrupts
                }
                manager.killIdleSessions(Preferences.getInstance().getMaxIdleSeconds());
            }
        }
    }
}
