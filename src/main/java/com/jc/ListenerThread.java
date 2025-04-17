package com.jc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenerThread extends Thread {

    private final ServerManager manager;
    private final IConfiguration configuration;
    private final ServerSocket serverSocket;

    public ListenerThread(ServerManager manager, IConfiguration configuration) throws IOException {
        this.manager = manager;
        this.configuration = configuration;
        this.serverSocket = new ServerSocket(configuration.getPortNumber());
        this.setDaemon(true);
    }

    public void run() {
        this.setName("ListenerThread");
        while (!isInterrupted()) {
            Logger.INFO("Listening on port: " + configuration.getPortNumber());
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                Logger.INFO("Listener accept(): " + e.getMessage());
            }
            if(socket != null) {
                Logger.INFO("Connection " + socket.getInetAddress().getCanonicalHostName() +
                        " / " + socket.getInetAddress().getHostAddress());
                manager.createSession(socket);
            }
        }
    }

    public String getAddressAndPort() {
        return serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort();
    }

    public void stopWaiting() {
        Logger.INFO("Listener terminating");
        manager.killThreads(0L);
        this.interrupt();
    }

}

