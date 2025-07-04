package com.ablestrategies.web.conn;

import com.ablestrategies.web.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Main per-connection thread.
 * ---------------------------------------------------------------------------
 * Connection (IP Address:port pair) is tied to ConnectionThread by its socket.
 * It is expected that, when the server accepts a connection, it will be
 * passed to this object to deal with any further communications.
 * ---------------------------------------------------------------------------
 */
public class ConnectionThread extends Thread {

    /** Logger slf4j. */
    @SuppressWarnings("all")
    private final Logger logger = LoggerFactory.getLogger(ConnectionThread.class);

    /** [static] For numbering threads. */
    private static int ThreadCounter = 0;

    /** Name of this thread. (Also same as Thread.currentThread.getName().) */
    private String threadName = "?";

    /** Create this to handle the connection. */
    private final ConnectionHandler handler;

    /** Stash the Ip address and port of the remote client for reference. */
    private final String clientIp;

    /** All of our web communication for this connection goes over this socket. */
    private final Socket socket;

    /** HTTP or HTTPS. */
    private final String protocol;

    /**
     * ctor.
     * @param protocol HTTP or HTTPS
     * @param socket Connection accepted by server.
     * @param manager This is the Top Dog that knows everything about the server.
     * @throws IOException If we cannot recover, we will give up and throw this.
     */
    public ConnectionThread(String protocol, Socket socket, ServerManager manager) throws IOException {
        this.setDaemon(true);
        this.protocol = protocol;
        this.socket = socket;
        clientIp = socket.getRemoteSocketAddress().toString();
        logger.debug("### Starting - connection with {}", clientIp);
        handler = new ConnectionHandler(socket, manager);
    }

    /**
     * Main loop of thread.
     * To kill this, call closeSocket() then interrupt(), not stop().
     */
    @SuppressWarnings({"all"})
    public void run() {
        threadName = "ConnectionThread" + (++ThreadCounter) + "-" + protocol + socket.getPort();
        Thread.currentThread().setName(threadName);
        while(!isInterrupted()) {
            handler.connectionLoop();
        }
    }

    /**
     * Because java threads can't interrupt sockets, which are at a lower level.
     */
    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("closeSocket " + e.getMessage());
        }
    }

    /**
     * How many seconds have passed since the last http request?
     * @return number of seconds since last request.
     */
    public long beenIdleForHowLong() {
        return handler.beenIdleForHowLong();
    }

    /**
     * Get the history of requests/responses.
     * @return Strings in temporal order.
     */
    public List<String> getHistory() {
        return handler.getHistory();
    }

    /**
     * Get the name of the thread for logging and diagnostic purposes.
     * @return Name of this thread.
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * Get the address and port of the client.
     * @return string containing address and port of remote host on this connection.
     */
    public String getAddressAndPort() {
        return clientIp;
    }

    /**
     * Get the protocol.
     * @return HTTP or HTTPS.
     */
    public String getProtocol() {
        return protocol;
    }

}
