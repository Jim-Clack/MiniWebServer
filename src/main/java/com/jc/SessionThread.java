package com.jc;

import java.io.*;
import java.net.Socket;

public class SessionThread extends Thread {

    private static int ThreadCounter = 0;

    private String threadName = "?";
    private final SessionHandler handler;
    private String clientIp;

    public SessionThread(Socket socket, Configuration configuration) throws IOException {
        this.setDaemon(true);
        String clientIp = socket.getRemoteSocketAddress().toString();
        Logger.INFO("Starting - connection with " + clientIp);
        System.out.println("\n### Connection with " + clientIp);
        handler = new SessionHandler(socket, configuration);
    }

    @SuppressWarnings({"all"})
    public void run() {
        threadName = "SessionThread" + (++ThreadCounter);
        Thread.currentThread().setName(threadName);
        handler.sessionLoop();
    }

    public long beenIdleForHowLong() {
        return handler.beenIdleForHowLong();
    }

    public String getThreadName() {
        return threadName;
    }

    public String getAddressAndPort() {
        return clientIp;
    }

}
