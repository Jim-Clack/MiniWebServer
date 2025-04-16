package com.jc;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class SessionThread extends Thread {

    private static int ThreadCounter = 0;

    private final Socket socket;
    private Integer lastActivityLock = Integer.valueOf(0);
    private LocalDateTime lastActivity = LocalDateTime.now();
    private final InputStream inStream;
    private final OutputStream outStream;
    private StringBuilder inBuffer;
    private StringBuilder outBuffer;
    private int bytesReadThisTime;

    public SessionThread(Socket socket) throws IOException {
        this.socket = socket;
        inStream = socket.getInputStream();
        outStream = socket.getOutputStream();
        this.setDaemon(true);
    }

    public void run() {
        Thread.currentThread().setName("SessionThread" + (++ThreadCounter));
        Logger.INFO("Connection with " + socket.getRemoteSocketAddress());
        while(!isInterrupted()) {
            emptyBuffers();
            try {
                if(inStream.available() <= 0) {
                    Thread.sleep(50);
                    continue;
                }
            } catch (IOException | InterruptedException e) {
                Logger.INFO("Awaiting receive, e=" + e.getMessage());
            }
            if(receiveIntoInBuffer() > 0) {
                synchronized (lastActivityLock) {
                    lastActivity = LocalDateTime.now();
                }
                HttpRequest request = new HttpRequest(inBuffer.toString());
            }
        }
    }

    public long beenIdleForHowLong() {
        synchronized (lastActivityLock) {
            // TODO
        }
        return 1L;
    }

    private void emptyBuffers() {
        outBuffer = new StringBuilder();
        inBuffer = new StringBuilder();
        bytesReadThisTime = 0;
    }

    private int receiveIntoInBuffer() {
        byte[] buffer = new byte[10000];
        int bytesRead;
        try {
            while((bytesRead = inStream.available()) > 0) {
                bytesRead = inStream.read(buffer, 0, Math.min(buffer.length, bytesRead));
                String content = new String(buffer, 0, bytesRead);
                inBuffer.append(content);
            }
        } catch (IOException e) {
            Logger.INFO("receiveIntoInBuffer" + e.getMessage());
            throw new RuntimeException(e);
        }
        bytesReadThisTime = inBuffer.length() - bytesReadThisTime;
        return bytesReadThisTime;
    }

    private void sendFromOutBuffer() {
        try {
            outStream.write(outBuffer.toString().getBytes());
        } catch (IOException e) {
            Logger.INFO("sendFromOutBuffer" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
