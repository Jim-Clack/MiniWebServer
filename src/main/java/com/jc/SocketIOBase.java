package com.jc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class SocketIOBase {

    private final InputStream inStream;
    private StringBuilder inBuffer;
    private final OutputStream outStream;

    protected SocketIOBase(Socket socket) throws IOException {
        this.inStream = socket.getInputStream();
        this.outStream = socket.getOutputStream();
    }

    protected int read() {
        byte[] buffer = new byte[10000];
        try {
            int bytesRead = Math.max(1, inStream.available()); // try to read at least one char
            while(bytesRead > 0) {
                bytesRead = inStream.read(buffer, 0, bytesRead);
                if(bytesRead <= 0) { // just in case
                    return inBuffer.length();
                }
                String content = new String(buffer, 0, bytesRead);
                inBuffer.append(content);
                bytesRead = Math.min(buffer.length, inStream.available());
            }
        } catch (IOException e) {
            if(Thread.currentThread().isInterrupted()) {
                return 0;
            }
            Logger.WARN("receiveIntoInBuffer" + e.getMessage());
        }
        return inBuffer.length();
    }

    protected void send(byte[] content) {
        try {
            outStream.write(content);
        } catch (IOException e) {
            Logger.WARN("sendResponse" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected String getReadBuffer() {
        return inBuffer.toString();
    }

    protected void clearBuffers() {
        inBuffer = new StringBuilder();
    }

}
