package com.jc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

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
       new IdleLoopThread(this).start();
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
    @SuppressWarnings("all")
    public String listAllThreads() {
        int threadCount = 0;
        StringBuilder buffer = new StringBuilder();
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
        buffer.append("Number of threads: " + threadCount + "\n");
        return buffer.toString();
    }

    /**
     * List all sessions running in server.
     * @return Multi-line string.
     */
    @SuppressWarnings("all")
    public synchronized String listAllSessions() {
        String dashes = "--------------------------------------\n";
        int threadCount = 0;
        StringBuilder buffer = new StringBuilder();
        buffer.append(dashes);
        for(SessionThread sessionThread : sessions) {
            buffer.append(sessionThread.getThreadName() + "\n");
            buffer.append("  Alive:    " + sessionThread.isAlive() + "\n");
            buffer.append("  Protocol: " + sessionThread.getProtocol() + "\n");
            buffer.append("  Idle:     " + sessionThread.beenIdleForHowLong() + "\n");
            buffer.append("  Client:   " + sessionThread.getAddressAndPort() + "\n");
            String historyHeader = "  History:  ";
            ConcurrentLinkedDeque<String> history = sessionThread.getHistory();
            for(String historyLine : history) {
                buffer.append(historyHeader + historyLine + "\n");
                historyHeader = "            ";
            }
            buffer.append(dashes);
            if(sessionThread.isAlive()) {
                threadCount++;
            }
        }
        buffer.append("Number of sessions Alive: " + threadCount + "\n");
        discardDeadSessions();
        return buffer.toString();
    }

}
