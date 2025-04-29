package com.jc;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * This class is in charge of an HTTP session, reading HTTP requests and then
 * sending the corresponding HTTP responses.
 */
public class SessionHandler extends SocketIOBase {

    /** Keep track of the timestamp of the last HTTP request. */
    private LocalDateTime lastActivity = LocalDateTime.now();

    /** Synch-lock for access to lastActivity. */
    private final Integer lastActivityLock = 0;

    /** Our connection to the client. */
    private final Socket socket;

    /** Top level manager over all sessions. */
    private final ServerManager manager;

    /** Configuration - settings. */
    private final Configuration configuration;

    /**
     * Ctor.
     * @param socket Our connection to the client.
     * @param configuration Configuration - settings.
     * @param manager Top level manager over all sessions.
     * @throws IOException Only if a non-recoverable communication error occurs.
     */
    public SessionHandler(Socket socket, Configuration configuration, ServerManager manager) throws IOException {
        super(socket);
        this.manager = manager;
        this.configuration = configuration;
        this.socket = socket;
    }

    /**
     * Here is the main loop for the session. (see handleRequest)
     */
    public void sessionLoop() {
        clearBuffers();
        if(read() > 0) {
            synchronized (lastActivityLock) {
                lastActivity = LocalDateTime.now();
            }
            handleRequest();
        }
    }

    /**
     * How long since the last request was handled?
     * @return number of seconds since last activity.
     */
    public long beenIdleForHowLong() {
        long seconds;
        synchronized (lastActivityLock) {
            seconds = ChronoUnit.SECONDS.between(lastActivity, LocalDateTime.now());
        }
        return seconds;
    }

    /**
     * Receive an HTTP request then send an HTTP response.
     */
    private void handleRequest() {
        HttpRequestBase request = new HttpRequestFile(getReadBuffer(), manager);
        if(request.getErrorCode() == ErrorCode.UNINITIALIZED) {
            Thread.currentThread().interrupt();
            return;
        }
        request = TransactionType.getTypedRequest(request); // clone to correct type
        HttpResponseBase response = TransactionType.getTypedResponse(request, configuration, manager);
        ResponseCode code = response.generateContent(socket);
        Logger.DEBUG("Processed request, code=" + code + ", type=" + request.getRequestKind());
        send(response.getContent());
    }

}
