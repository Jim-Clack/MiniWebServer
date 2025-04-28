package com.jc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple listener for a web server.
 */
public class ListenerThread extends Thread {

    /** Top level server manager. */
    private final ServerManager manager;

    /** The configuration - for IP port, path to web files. */
    private final Configuration configuration;

    /** The server socket that we listen on. */
    private final ServerSocket serverSocket;

    /**
     * Ctor.
     * @param manager Top level server manager.
     * @param configuration The configuration - for IP port, path to web files.
     * @throws IOException Fatal problem starting server/listener.
     */
    public ListenerThread(ServerManager manager, Configuration configuration) throws IOException {
        this.manager = manager;
        this.configuration = configuration;
        this.serverSocket = new ServerSocket(configuration.getPortNumber());
        this.setDaemon(true);
    }

    /**
     * Run loop for thread.
     */
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

    /**
     * Getter for a string containing the local connection info.
     * @return The IP address/domain/port.
     */
    public String getAddressAndPort() {
        return serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort();
    }

}

