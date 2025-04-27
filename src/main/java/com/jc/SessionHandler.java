package com.jc;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SessionHandler extends SocketIOBase {

    private final Configuration configuration;
    private final Integer lastActivityLock = 0;
    private LocalDateTime lastActivity = LocalDateTime.now();
    private final ServerManager manager;
    private final Socket socket;

    public SessionHandler(Socket socket, Configuration configuration, ServerManager manager) throws IOException {
        super(socket);
        this.manager = manager;
        this.configuration = configuration;
        this.socket = socket;
    }

    public void sessionLoop() {
        clearBuffers();
        if(read() > 0) {
            synchronized (lastActivityLock) {
                lastActivity = LocalDateTime.now();
            }
            handleRequest();
        }
    }

    public long beenIdleForHowLong() {
        long seconds;
        synchronized (lastActivityLock) {
            seconds = ChronoUnit.SECONDS.between(lastActivity, LocalDateTime.now());
        }
        return seconds;
    }

    private void handleRequest() {
        HttpRequestBase request = new HttpRequestFile(getReadBuffer(), manager);
        if(request.getErrorCode() == ErrorCode.UNINITIALIZED) {
            Thread.currentThread().interrupt();
            return;
        }
        request = request.getTypedRequest(); // clone to correct type
        HttpResponseBase response = request.getTypedResponse(configuration);
        ResponseCode code = response.generateContent(socket);
        Logger.DEBUG("Processed request, code=" + code + ", type=" + request.getRequestKind());
        send(response.getContent());
    }

}
