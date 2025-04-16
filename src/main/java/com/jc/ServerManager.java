package com.jc;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ServerManager {

    private final List<SessionThread> sessions = new LinkedList<>();

    public void acceptSession(Socket socket) {
        SessionThread sessionThread;
        try {
            sessionThread = new SessionThread(socket);
            sessionThread.start();
            sessions.add(sessionThread);
        } catch (IOException e) {
            Logger.INFO("acceptSession: " + e.getMessage());
        }
    }

    public void killThreads(long MaxIdleSeconds) {
        for(SessionThread sessionThread : sessions) {
            if(sessionThread.beenIdleForHowLong() > MaxIdleSeconds && sessionThread.isAlive()) {
                sessionThread.interrupt();
            }
        }
    }

}
