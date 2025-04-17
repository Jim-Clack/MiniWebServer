package com.jc;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SessionThread extends Thread {

    private static int ThreadCounter = 0;

    private String threadName = "?";
    private String clientIp = "?";
    private final Socket socket;
    private final Integer lastActivityLock = Integer.valueOf(0);
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
        threadName = "SessionThread" + (++ThreadCounter);
        Thread.currentThread().setName(threadName);
        clientIp = socket.getRemoteSocketAddress().toString();
        Logger.INFO("Connection with " + clientIp);
        System.out.println("\n### Connect " + threadName + " with " + clientIp);
        while(!isInterrupted()) {
            emptyBuffers();
            try {
                if(inStream.available() <= 0) {
                    Thread.sleep(50);
                    continue;
                }
            } catch (IOException | InterruptedException e) {
                Logger.INFO("Awaiting receive, e=" + e.getMessage());
                break;
            }
            if(receiveIntoInBuffer() > 0) {
                synchronized (lastActivityLock) {
                    lastActivity = LocalDateTime.now();
                }
                HttpRequest request = new HttpRequest(inBuffer.toString());
                // TODO
            }
        }
    }

    public long beenIdleForHowLong() {
        long seconds = 0;
        synchronized (lastActivityLock) {
            seconds = ChronoUnit.SECONDS.between(lastActivity, LocalDateTime.now());
        }
        return seconds;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getAddressAndPort() {
        return clientIp;
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
