package com.jc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class SocketIOBase {

    protected final InputStream inStream;
    protected StringBuilder inBuffer;
    protected int bytesReadThisTime;
    protected final Socket socket;
    protected final OutputStream outStream;

    protected SocketIOBase(Socket socket) throws IOException {
        this.socket = socket;
        this.inStream = socket.getInputStream();
        this.outStream = socket.getOutputStream();
    }

    protected int receiveIntoInBuffer() {
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
            if(Thread.currentThread().isInterrupted()) {
                return 0;
            }
            Logger.WARN("receiveIntoInBuffer" + e.getMessage());
        }
        bytesReadThisTime = inBuffer.length() - bytesReadThisTime;
        return bytesReadThisTime;
    }

    protected void sendResponse(byte[] content) {
        try {
            outStream.write(content);
        } catch (IOException e) {
            Logger.WARN("sendResponse" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
