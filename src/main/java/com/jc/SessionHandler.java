package com.jc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SessionHandler {

    private final Configuration configuration;
    private final Socket socket;
    private final Integer lastActivityLock = 0;
    private LocalDateTime lastActivity = LocalDateTime.now();
    private final InputStream inStream;
    private final OutputStream outStream;
    private StringBuilder inBuffer;
    private int bytesReadThisTime;

    public SessionHandler(Socket socket, Configuration configuration) throws IOException {
        this.configuration = configuration;
        this.socket = socket;
        inStream = socket.getInputStream();
        outStream = socket.getOutputStream();
    }

    public void sessionLoop() {
        while(!Thread.currentThread().isInterrupted()) {
            clearBuffers();
            if(receiveIntoInBuffer() > 0) {
                handleRequest();
            }
        }
    }

    public long beenIdleForHowLong() {
        long seconds;
        synchronized (lastActivityLock) {
            seconds = ChronoUnit.SECONDS.between(lastActivity, LocalDateTime.now());
        }
        return seconds;
    }

    private void clearBuffers() {
        inBuffer = new StringBuilder();
        bytesReadThisTime = 0;
    }

    private void handleRequest() {
        HttpRequest request = new HttpRequest(inBuffer.toString());
        HttpResponse response = new HttpResponse(request, configuration);
        ResponseCode code = response.generateContent(socket);
        // currently supports only simple HTTP file transfer
        sendResponse(response.getContent());
        synchronized (lastActivityLock) {
            lastActivity = LocalDateTime.now();
        }
    }

    @SuppressWarnings({"all"})
    private int receiveIntoInBuffer() {
        byte[] buffer = new byte[10000];
        int offset = 1;
        int bytesRead;
        try {
            inStream.read(buffer, 0, 1);
            while((bytesRead = inStream.available()) > 0) { // read remainder of message
                bytesRead = inStream.read(buffer, offset, Math.min(buffer.length - offset, bytesRead));
                String content = new String(buffer, 0, bytesRead + offset);
                inBuffer.append(content);
                offset = 0;
            }
        } catch (IOException e) {
            Logger.WARN("receiveIntoInBuffer" + e.getMessage());
            throw new RuntimeException(e);
        }
        bytesReadThisTime = inBuffer.length() - bytesReadThisTime;
        return bytesReadThisTime;
    }

    private void sendResponse(byte[] content) {
        try {
            outStream.write(content);
        } catch (IOException e) {
            Logger.WARN("sendResponse" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
