package com.jc;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class HttpResponseFile extends HttpResponseBase {

    private final HttpRequestBase request;
    private final Configuration configuration;
    private final StringBuilder headerBuffer;
    private byte[] responseBuffer = null;
    private ResponseCode responseCode;

    public HttpResponseFile(HttpRequestBase request, Configuration configuration) {
        this.request = request;
        this.configuration = configuration;
        this.headerBuffer = new StringBuilder();
    }

    public ResponseCode generateContent(Socket socket) {
        responseCode = ResponseCode.RC_OK;
        Path pathToFile = getFilePath(request.getFilePath(), true, configuration,
                "index.html", "index.htm", "default.htm", "default.html");
        Logger.INFO("Sending " + pathToFile + " to " + socket);
        if(pathToFile == null) {
            responseCode = ResponseCode.RC_NOT_FOUND;
        } else {
            loadFile(pathToFile);
        }
        return responseCode;
    }

    public byte[] getContent() {
        return responseBuffer;
    }

    private void loadFile(Path pathToFile) {
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

    private void generateLine1AndHeaders(int contentLength) {
        String line1 = request.getVersion() + " " +
                responseCode.getNumValue() + " " + responseCode.getTextValue() + "\n";
        assembleHeaders(headerBuffer, line1, contentLength, 15);
    }

}
