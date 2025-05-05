package com.jc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is in charge of an HTTP session, reading HTTP requests and then
 * sending the corresponding HTTP responses.
 */
public class SessionHandler extends SocketIOBase {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(SessionHandler.class);

    /** Keep track of the timestamp of the last HTTP request. */
    private LocalDateTime lastActivity = LocalDateTime.now();

    /** Synch-lock for access to lastActivity. */
    private final Integer lastActivityLock = 0;

    /** Our connection to the client. */
    private final Socket socket;

    /** Top level manager over all sessions. */
    private final ServerManager manager;

    /** Keep track of history - most recent first. */
    private final List<String> history = new LinkedList<>();

    /**
     * Ctor.
     * @param socket Our connection to the client.
     * @param manager Top level manager over all sessions.
     * @throws IOException Only if a non-recoverable communication error occurs.
     */
    public SessionHandler(Socket socket, ServerManager manager) throws IOException {
        super(socket);
        this.manager = manager;
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
     * Get the history of requests/responses.
     * @return Strings in temporal order with most recent first.
     */
    public List<String> getHistory() {
        return history;
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
        request = HttpActionType.getTypedRequest(request); // clone to correct type
        HttpResponseBase response = HttpActionType.getTypedResponse(request, manager);
        ResponseCode code = response.generateContent(socket);
        logger.debug("Processed request, code={}, type={}", code, HttpActionType.getRequestKind(request));
        if(code == ResponseCode.RC_OK) {
            String event = request.getMethod() + " " + request.getUrl() + " ==> " +
                    code.getNumValue() + " " + code.getTextValue();
            history.add(0, event);
            send(response.getContent());
            if(history.size() > Preferences.getInstance().getMaxHistory()) {
                history.remove(history.size() - 1);
            }
        }
    }

}
