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
            sessionThread = new SessionThread(socket, configuration);
            sessionThread.start();
            sessions.add(sessionThread);
        } catch (IOException e) {
            Logger.ERROR("ServerManager acceptSession error: " + e.getMessage());
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
                ++killCount;
            }
        }
        discardDeadSessions();
        return killCount;
    }

    public int listAllSessions() {
        int threadCount = 0;
        for(SessionThread sessionThread : sessions) {
            System.out.println(sessionThread.getThreadName());
            System.out.println("  Alive:   " + sessionThread.isAlive());
            System.out.println("  Idle:    " + sessionThread.beenIdleForHowLong());
            System.out.println("  Client:  " + sessionThread.getAddressAndPort());
            if(sessionThread.isAlive()) {
                threadCount++;
            }
        }
        discardDeadSessions();
        return threadCount;
    }


}
