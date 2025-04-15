package com.jc;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class SessionThread extends Thread {

    private static int ThreadCounter = 0;

    private final Socket socket;
    private int requestCounter = 0;
    private LocalDateTime lastActivity = LocalDateTime.now();
    private final InputStream inStream;
    private final OutputStream outStream;
    private StringBuilder inBuffer;
    private StringBuilder outBuffer;

    public SessionThread(Socket socket) throws IOException {
        this.socket = socket;
        inStream = socket.getInputStream();
        outStream = socket.getOutputStream();
        this.setDaemon(true);
    }

    public void run() {
        Thread.currentThread().setName("SessionThread" + (++ThreadCounter) +
                socket.getRemoteSocketAddress());
        while(!isInterrupted()) {
            receiveIntoInBuffer();
            if(inBuffer.length() <= 0) {
                continue;
            }
            lastActivity = LocalDateTime.now();
            Logger.INFO("Received RQST" + (++requestCounter) + "\n" + inBuffer.toString());
            // Diagnostic
            if(requestCounter > 10) {
                break;
            }
        }
    }

    private void receiveIntoInBuffer() {
        inBuffer = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = inStream.read(buffer)) != -1) {
                inBuffer.append(new String(buffer, 0, bytesRead));
            }
        } catch (IOException e) {
            Logger.INFO("receiveIntoInBuffer" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private long idleForHowLong() {

        return 1L;
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
