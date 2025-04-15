package com.jc;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ServerManager {

    private List<SessionThread> sessions = new LinkedList<SessionThread>();

    public SessionThread acceptSession(Socket socket) {
        SessionThread sessionThread = null;
        try {
            sessionThread = new SessionThread(socket);
            sessions.add(sessionThread);
        } catch (IOException e) {
            Logger.INFO("acceptSession: " + e.getMessage());
            return null;
        }
        sessionThread.start();
        return sessionThread;
    }

    public void killThreads(long MaxIdleSeconds) {

    }

}
