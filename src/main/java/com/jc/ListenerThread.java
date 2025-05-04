package com.jc;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Simple listener for a web server.
 */
public class ListenerThread extends Thread {

    /** Top level server manager. */
    private final ServerManager manager;

    /** The server socket that we listen on. */
    private final ServerSocket serverSocket;

    /** HTTPS or HTTPS? */
    private final String protocol;

    /** IP Port number. */
    private final int portNumber;

    /**
     * Ctor.
     * @param manager Top level server manager.
     * @param protocol HTTP or HTTPS.
     * @throws IOException Fatal problem starting server/listener.
     */
    public ListenerThread(String protocol, ServerManager manager) throws IOException {
        this.protocol = protocol.trim().toUpperCase();
        this.manager = manager;
        InetAddress address = InetAddress.getByName("localhost");
        if(protocol.equals("HTTPS")) {
            this.portNumber = Configuration.getInstance().getSslPortNumber();
            this.serverSocket = SSLServerSocketFactory.getDefault().
                    createServerSocket(portNumber, 100, address);
        } else {
            this.portNumber = Configuration.getInstance().getPortNumber();
            this.serverSocket = ServerSocketFactory.getDefault().
                    createServerSocket(portNumber, 100, address);
        }
        this.setDaemon(true);
    }

    /**
     * Run loop for thread.
     */
    public void run() {
        this.setName(protocol + "-ListenerThread");
        System.out.println(protocol + " listening on port: " + this.portNumber);
        while (!isInterrupted()) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                Logger.DEBUG("Listener accept(): " + e.getMessage());
            }
            if(socket != null) {
                manager.createSession(protocol, socket);
            }
        }
    }

    /**
     * Getter for a string containing the local connection info.
     * @return The IP address/domain/port.
     */
    public String getAddressAndPort() {
        try {
            InetAddress address = InetAddress.getByName("localhost");
            return address.getHostAddress() + ":" + portNumber;
        } catch (UnknownHostException e) {
            // default below
        }
        return serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort();
    }

}

