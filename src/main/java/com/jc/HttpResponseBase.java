package com.jc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * All HTTP responses are based on this.
 */
public abstract class HttpResponseBase {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(HttpResponseBase.class);

    /**
     * Generate the response with line1, headers, and body.
     * @param socket Connection to remote client.
     * @return The response code: watch out for RC_NOT_FOUND.
     */
    public abstract ResponseCode generateContent(Socket socket);

    /**
     * Get the response that was assembled by the generateContent() call.
     * @return A socket-ready HTTP response.
     */
    public abstract byte[] getContent();

    /**
     * Read a file in, such as a HTML. JPEG, or PNG file.
     * @param pathToFile Absolute path of the file.
     * @return File content.
     */
    protected byte[] readFile(String pathToFile) {
        File fileToReturn = new File(pathToFile);
        try (FileInputStream inStream = new FileInputStream(fileToReturn)) {
            return inStream.readAllBytes();
        } catch (IOException e) {
            logger.error("Problem reading file {}", fileToReturn.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate the headers for the response.
     * @param headerBuffer For results. You may pre-populate this with additional headers.
     * @param line1 First line, ending with an endline, such as: "HTTP/1.1 200 OK\n".
     * @param contentLength Length of body. (Yes, you have to generate the body first)
     * @param maxSeconds Cache-control - validity of response in seconds.
     */
    @SuppressWarnings("all")
    protected void assembleHeaders(
            StringBuilder headerBuffer, String line1, int contentLength, int maxSeconds) {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
        String now = dateFormat.format(new Date());
        headerBuffer.insert(0, line1);
        headerBuffer.append("content-type: text/html; charset=UTF-8\n");
        headerBuffer.append("date: " + now + "\n");
        headerBuffer.append("cache-control: public, max-age=" + maxSeconds + "\n");
        headerBuffer.append("server: mini\n");
        headerBuffer.append("content-length: " + contentLength + "\n");
        headerBuffer.append("x-xss-protection: 0\n");
        headerBuffer.append("x-frame-options: SAMEORIGIN\n");
        headerBuffer.append("\n");
    }

}
