package com.ablestrategies.web.conn;

import com.ablestrategies.web.Preferences;
import com.ablestrategies.web.resp.ResponseCode;
import com.ablestrategies.web.ServerManager;
import com.ablestrategies.web.resp.HttpResponseBase;
import com.ablestrategies.web.rqst.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is in charge of an HTTP connection, reading HTTP requests and then
 * sending the corresponding HTTP responses.
 */
public class ConnectionHandler extends SocketIOBase {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    /** Keep track of the timestamp of the last HTTP request. */
    private LocalDateTime lastActivity = LocalDateTime.now();

    /** Synchlock for access to lastActivity. */
    private final Integer lastActivityLock = 0;

    /** Our connection to the client. */
    private final Socket socket;

    /** Top level manager over all connections. */
    private final ServerManager manager;

    /** For history timestamps. */
    private final DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss.SSS ");

    /**
     * Keep track of history - most recent first.
     * BTW - ConcurrentLinkedDeque is not a good solution here because of implied
     * iteration over the collection in getHistory() and that causes some bizarre
     * conflicts. So, instead, we just synchronize access to the collection.
     */
    private final List<String> history = new LinkedList<>();

    /**
     * Ctor.
     * @param socket Our connection to the client.
     * @param manager Top level manager over all connections.
     * @throws IOException Only if a non-recoverable communication error occurs.
     */
    public ConnectionHandler(Socket socket, ServerManager manager) throws IOException {
        super(socket);
        this.manager = manager;
        this.socket = socket;
    }

    /**
     * Here is the main loop for the connection. (see handleRequest)
     */
    public void connectionLoop() {
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
        // Synchronized collections have issues with iteration, so we synchronize
        // it here (in the LinkedList ctor) to return a copy of the list.
        synchronized (this) {
            return new LinkedList<>(history);
        }
    }

    /**
     * Receive an HTTP request then send an HTTP response.
     */
    private void handleRequest() {
        HttpRequestBase request = new HttpRequestFile(getReadBuffer(), manager);
        if(request.getErrorCode() == RequestError.UNINITIALIZED) {
            updateHistory(request, null, ResponseCode.RC_UNKNOWN_ERROR);
            Thread.currentThread().interrupt();
            return;
        }
        request = HttpActionType.getTypedRequest(request); // clone to correct type
        HttpResponseBase response = HttpActionType.getTypedResponse(request, manager);
        ResponseCode code = response.generateContent(socket);
        logger.debug("Processed request, code={}, type={}", code, HttpActionType.getRequestKind(request));
        updateHistory(request, response, code);
        if(code != ResponseCode.RC_SWITCHING_PROTOCOLS) {
            send(response.getContent());
        }
    }

    /**
     * Keep track of connection history.
     * @param request As received and parsed.
     * @param response The response, null if none.
     * @param code Per response.
     */
    private void updateHistory(HttpRequestPojo request, HttpResponseBase response, ResponseCode code) {
        String now = dateFormat.format(new Date());
        String requestSpecs = now + request.getSessionId() + " " + request.getMethod() + " " + request.getUrl();
        String responseSpecs = "(no response)";
        if(response != null) {
            responseSpecs = response.getDescription() + " " + code.getNumValue() + " " + code.getTextValue();
        }
        synchronized (this) {
            history.add(0, requestSpecs + " ==> " + responseSpecs);
            if (history.size() > Preferences.getInstance().getMaxHistory()) {
                history.remove(history.size() - 1);
            }
        }
        logger.debug("### " + requestSpecs + " ==> " + responseSpecs);
    }

}
