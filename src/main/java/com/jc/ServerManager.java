package com.jc;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ServerManager {

    private final List<SessionThread> sessions = new LinkedList<>();

    public void createSession(Socket socket, Configuration configuration) {
        SessionThread sessionThread;
        try {
            sessionThread = new SessionThread(socket, configuration, this);
            sessionThread.start();
            sessions.add(sessionThread);
        } catch (IOException e) {
            Logger.ERROR("ServerManager accept session error: " + e.getMessage());
        }
    }

    public void discardDeadSessions() {
        List<SessionThread> deadSessions = new LinkedList<>();
        for(SessionThread sessionThread : sessions) {
            if(!sessionThread.isAlive()) {
                deadSessions.add(sessionThread); // remove it and any dead threads
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
            buffer.append("  Alive:   " + sessionThread.isAlive() + "\n");
            buffer.append("  Idle:    " + sessionThread.beenIdleForHowLong() + "\n");
            buffer.append("  Client:  " + sessionThread.getAddressAndPort() + "\n");
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
