package com.ablestrategies.web.resp;

import com.ablestrategies.web.conn.ContentMimeType;
import com.ablestrategies.web.rqst.HttpRequestPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * HTTP Response that simply returns the contents of a file.
 */
public class HttpResponseFile extends HttpResponse {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    /** The HTTP that requested the file. */
    private final HttpRequestPojo request;

    /** The response header gets assembled into this. */
    private final StringBuilder headerBuffer;

    /** Line1, headers, and body (file content) are assembled into this. */
    private byte[] responseBuffer = null;

    /** Resultant HTTP status code. i.e. 200/OK */
    private ResponseCode responseCode;

    /**
     * Ctor.
     * @param request The HTTP that requested the file.
     */
    public HttpResponseFile(HttpRequestPojo request) {
        this.description = "File";
        this.request = request;
        this.headerBuffer = new StringBuilder();
    }

    /**
     * Generate the response with line1, headers, and body.
     * @param socket Connection to remote client.
     * @return The response code: watch out for RC_NOT_FOUND.
     */
    public ResponseCode generateContent(Socket socket) {
        responseCode = ResponseCode.RC_OK;
        String pathToFile = request.getFilePath(true);
        logger.debug("Sending {} to {}", pathToFile, socket);
        if(pathToFile == null) {
            responseCode = ResponseCode.RC_NOT_FOUND;
        } else {
            assembleResponseWithFile(pathToFile);
        }
        description += " " + pathToFile;
        return responseCode;
    }

    /**
     * Get the response that was assembled by the gernateContent() call.
     * @return A socket-ready HTTP response.
     */
    public byte[] getContent() {
        return responseBuffer;
    }

    /**
     * Assemble the HTTP response, line1, headers, and read the file in.
     * @param pathToFile Absolute or relative path to file to be read.
     */
    private void assembleResponseWithFile(String pathToFile) {
        byte[] content = readFile(pathToFile);
        if (content == null || content.length == 0) {
            responseCode = ResponseCode.RC_NOT_FOUND;
            content = new byte[0];
        }
        ContentMimeType mimeType = ContentMimeType.mimeTypeFromFileSuffix(pathToFile);
        generateLine1AndHeaders(content.length, mimeType);
        // now switch to binary I/O...
        responseBuffer = java.util.Arrays.copyOf(
                headerBuffer.toString().getBytes(StandardCharsets.UTF_8),
                headerBuffer.length() + content.length);
        System.arraycopy(content, 0, responseBuffer, headerBuffer.length(), content.length);
    }

    /**
     * Assemble line1 and headers.
     * @param contentLength Size of body - file to be returned.
     */
    private void generateLine1AndHeaders(int contentLength, ContentMimeType mimeType) {
        String line1 = request.getVersion() + " " +
                responseCode.getNumValue() + " " + responseCode.getTextValue() + "\n";
        assembleHeaders(request, headerBuffer, line1, contentLength, mimeType, 15);
    }

}
