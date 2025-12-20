package com.ablestrategies.web.resp;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpResponseExample extends HttpResponsePlugin {

    public HttpResponseExample() {
        initialize("Example");
    }

    /**
     * Generate the response with line1, headers, and body.
     *
     * @param socket Connection to remote client.
     * @return The response code: watch out for RC_NOT_FOUND.
     */
    @Override
    public ResponseCode generateContent(Socket socket) {
        return ResponseCode.RC_OK;
    }

    /**
     * Get the response that was assembled by the generateContent() call.
     *
     * @return A socket-ready HTTP response.
     */
    @Override
    public byte[] getContent() {
        return "Content".getBytes(StandardCharsets.UTF_8);
    }
}
