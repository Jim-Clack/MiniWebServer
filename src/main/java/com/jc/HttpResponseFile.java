package com.jc;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * HTTP Response that simply returns the contents of a file.
 */
public class HttpResponseFile extends HttpResponseBase {

    /** The HTTP that requested the file. */
    private final HttpRequestBase request;

    /** Configuration settings. */
    private final Configuration configuration;

    /** The response header gets assembled into this. */
    private final StringBuilder headerBuffer;

    /** Line1, headers, and body (file content) are assembled into this. */
    private byte[] responseBuffer = null;

    /** Resultant HTTP status code. i.e. 200/OK */
    private ResponseCode responseCode;

    /**
     * Ctor.
     * @param request The HTTP that requested the file.
     * @param configuration Configuration settings.
     */
    public HttpResponseFile(HttpRequestBase request, Configuration configuration) {
        this.request = request;
        this.configuration = configuration;
        this.headerBuffer = new StringBuilder();
    }

    /**
     * Generate the response with line1, headers, and body.
     * @param socket Connection to remote client.
     * @return The response code: watch out for RC_NOT_FOUND.
     */
    public ResponseCode generateContent(Socket socket) {
        responseCode = ResponseCode.RC_OK;
        Path pathToFile = getFilePath(request.getFilePath(), true, configuration,
                "index.html", "index.htm", "default.htm", "default.html");
        Logger.INFO("Sending " + pathToFile + " to " + socket);
        if(pathToFile == null) {
            responseCode = ResponseCode.RC_NOT_FOUND;
        } else {
            assembleResponseWithFile(pathToFile);
        }
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
    private void assembleResponseWithFile(Path pathToFile) {
        byte[] content = readFile(pathToFile);
        if (content == null || content.length <= 0) {
            responseCode = ResponseCode.RC_UNAUTHORIZED;
            generateLine1AndHeaders(0);
            return;
        }
        generateLine1AndHeaders(content.length);
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
    private void generateLine1AndHeaders(int contentLength) {
        String line1 = request.getVersion() + " " +
                responseCode.getNumValue() + " " + responseCode.getTextValue() + "\n";
        assembleHeaders(headerBuffer, line1, contentLength, 15);
    }

}
