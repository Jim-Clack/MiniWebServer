package com.jc;

import java.io.*;
import java.net.Socket;

public class SessionThread extends Thread {

    private static int ThreadCounter = 0;

    private String threadName = "?";
    private final SessionHandler handler;
    private final String clientIp;
    private final Socket socket;

    public SessionThread(Socket socket, Configuration configuration, ServerManager manager) throws IOException {
        this.setDaemon(true);
        this.socket = socket;
        clientIp = socket.getRemoteSocketAddress().toString();
        Logger.INFO("Starting - connection with " + clientIp);
        System.out.println("\n### Connection with " + clientIp);
        handler = new SessionHandler(socket, configuration, manager);
    }

    @SuppressWarnings({"all"})
    public void run() {
        threadName = "SessionThread" + (++ThreadCounter);
        Thread.currentThread().setName(threadName);
        while(!isInterrupted()) {
            handler.sessionLoop();
        }
    }

    public void closeSocket() {
        // necessary because java threads don't interrupt sockets which are at a lower level
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("closeSocket " + e.getMessage());
        }
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
