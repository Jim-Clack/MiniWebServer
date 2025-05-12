package com.ablestrategies.web.resp;

import com.ablestrategies.web.conn.ContentMimeType;
import com.ablestrategies.web.rqst.HttpRequestPojo;
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

    /** Description of the response. */
    protected String description = "----";

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
     * Get the response description.
     * @return May be the class name, authentication/user, the file sent/etc.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Read a file in, such as a HTML. JPEG, or PNG file.
     * @param pathToFile Absolute path of the file.
     * @return File content.
     */
    protected byte[] readFile(String pathToFile) {
        File fileToReturn = new File(pathToFile);
        if(!fileToReturn.exists()) {
            return new byte[0];
        }
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
     * @param request The HTTP request - needed for Session ID.
     * @param line1 First line, ending with an endline, such as: "HTTP/1.1 200 OK\n".
     * @param contentLength Length of body. (Yes, you have to generate the body first)
     * @param mimeType Type of content in body
     * @param maxSeconds Cache-control - validity of response in seconds.
     */
    @SuppressWarnings("all")
    protected void assembleHeaders(HttpRequestPojo request, StringBuilder headerBuffer,
                                   String line1, int contentLength, ContentMimeType mimeType, int maxSeconds) {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
        String now = dateFormat.format(new Date());
        String sessionId = request.getSessionId(true);
        headerBuffer.insert(0, line1);
        headerBuffer.append("content-type: " + mimeType.getMimeString() + "\n");
        headerBuffer.append("set-cookie: sessionid-mws=" + sessionId + "\n");
        headerBuffer.append("date: " + now + "\n");
        headerBuffer.append("cache-control: max-age=" + maxSeconds + "\n");
        if(maxSeconds == 0) {
            headerBuffer.append("expires: " + now + "\n");
            headerBuffer.append("pragma: no-cache\n");
        }
        headerBuffer.append("server: mws-MiniWebServer AbleStrategies\n");
        headerBuffer.append("content-length: " + contentLength + "\n");
        headerBuffer.append("\n");
    }

}
