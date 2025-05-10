package com.ablestrategies.web.conn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Class for handling socket I/O, designed to provide an optimal API for
 * HTTP request/response communications. There are four main APIs...
 *    int read()                reads an HTTP message and returns the length
 *    String getReadBuffer()    returns the HTTP message from read() above
 *    send(byte[])              sends the HTTP response as passed in
 *    clearBuffers()            call this between request/response cycles
 */
class SocketIOBase {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(SocketIOBase.class);

    /** Input stream from socket. */
    private InputStream inStream;

    /** Buffer for reading. */
    private StringBuilder inBuffer;

    /** Output stream from socket. */
    private final OutputStream outStream;

    /** The socket itself. */
    private final Socket socket;

    /**
     * Ctor.
     * @param socket to be read from or written to.
     * @throws IOException only on unexpected exceptions.
     */
    protected SocketIOBase(Socket socket) throws IOException {
        this.socket = socket;
        this.inStream = socket.getInputStream();
        this.outStream = socket.getOutputStream();
    }

    /**
     * Read - generally an HTTP request.
     * @return length of received message.
     * @apiNote on failure, returns nothing, but does not treat it as an error.
     */
    protected int read() {
        byte[] buffer = new byte[10000];
        if(inStream == null) {
            return 0;
        }
        try {
            int bytesRead = Math.max(1, inStream.available()); // try to read at least one char
            while (bytesRead > 0) {
                bytesRead = inStream.read(buffer, 0, bytesRead);
                if (bytesRead <= 0) {
                    return inBuffer.length();
                }
                String content = new String(buffer, 0, bytesRead);
                inBuffer.append(content);
                bytesRead = Math.min(buffer.length, inStream.available());
            }
        } catch (SSLException e) {
            logger.error("SSL problem, shutting down thread {}", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
            try {
                socket.close();
            } catch (IOException ex) {
                // ignore any exception
            }
            inStream = null;
            return 0;
        } catch (IOException e) {
            if(Thread.currentThread().isInterrupted()) {
                return 0;
            }
            logger.warn("receiveIntoInBuffer", e);
        }
        return inBuffer.length();
    }

    /**
     * Get the buffer populated by the previous read().
     * @return The bytes that were read.
     */
    protected String getReadBuffer() {
        return inBuffer.toString();
    }

    /**
     * Send - generally an HTTP response, often with a file in it.
     * @param content Binary, as it may contain a JPG or PNG file.
     */
    protected void send(byte[] content) {
        try {
            outStream.write(content);
        } catch (IOException e) {
            logger.warn("send() problem", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Clear out buffers in preparation for a fresh request/response cycle.
     */
    protected void clearBuffers() {
        inBuffer = new StringBuilder();
    }

}
