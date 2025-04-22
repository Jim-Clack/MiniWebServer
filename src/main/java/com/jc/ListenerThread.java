package com.jc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenerThread extends Thread {

    private final ServerManager manager;
    private final Configuration configuration;
    private final ServerSocket serverSocket;

    public ListenerThread(ServerManager manager, Configuration configuration) throws IOException {
        this.manager = manager;
        this.configuration = configuration;
        this.serverSocket = new ServerSocket(configuration.getPortNumber());
        this.setDaemon(true);
    }

    public void run() {
        this.setName("ListenerThread");
        System.out.println("Listening on port: " + configuration.getPortNumber());
        while (!isInterrupted()) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                Logger.DEBUG("Listener accept(): " + e.getMessage());
            }
            if(socket != null) {
                manager.createSession(socket, configuration);
            }
        }
    }

    public String getAddressAndPort() {
        return serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort();
    }

}

