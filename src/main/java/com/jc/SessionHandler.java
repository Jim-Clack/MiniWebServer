package com.jc;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SessionHandler extends SocketIOBase {

    private final Configuration configuration;
    private final ServerManager manager;
    private final Integer lastActivityLock = 0;
    private LocalDateTime lastActivity = LocalDateTime.now();
    private final Socket socket;

    public SessionHandler(Socket socket, Configuration configuration, ServerManager manager) throws IOException {
        super(socket);
        this.configuration = configuration;
        this.manager = manager;
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
        IHttpResponse response;
        HttpRequest request = new HttpRequest(getReadBuffer());
        if(request.getUrl().toLowerCase().startsWith("/webconsole")) {
            response = new WebConsoleResponse(request, manager);
        } else {
            response = new HttpResponse(request, configuration);
        }
        ResponseCode code = response.generateContent(socket);
        send(response.getContent());
    }

}
