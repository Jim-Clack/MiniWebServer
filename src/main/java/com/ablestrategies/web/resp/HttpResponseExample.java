package com.ablestrategies.web.resp;

import com.ablestrategies.web.conn.ContentMimeType;
import com.ablestrategies.web.rqst.HttpRequest;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpResponseExample extends HttpResponsePlugin {

    protected HttpRequest request;

    protected String line1 = "";

    public HttpResponseExample(HttpRequest request) {
        initialize("Example");
        this.request = request;
    }

    /**
     * Generate the response with line1, headers, and body.
     *
     * @param socket Connection to remote client.
     * @return The response code: watch out for RC_NOT_FOUND.
     */
    @Override
    public ResponseCode generateContent(Socket socket) {
        ResponseCode rc = ResponseCode.RC_OK;
        line1 = "HTTP/1.1 " + rc.getNumValue() + "  " + rc.getTextValue();
        return ResponseCode.RC_OK;
    }

    /**
     * @return A socket-ready HTTP response.
     */
    @Override
    public byte[] getContent() {
        String body = "{Body}\n";
        assembleHeaders(request, ResponseCode.RC_OK, body.length(), ContentMimeType.MIME_JSON, 10);
        String content = headerBuffer.toString() + body;
        return content.getBytes(StandardCharsets.UTF_8);
    }
}
