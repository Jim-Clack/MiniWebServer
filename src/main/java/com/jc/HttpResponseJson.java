package com.jc;

import java.net.Socket;

public class HttpResponseJson extends HttpResponseBase {

    public HttpResponseJson(HttpRequestPojo request) {
        // TODO
    }

    @Override
    public ResponseCode generateContent(Socket socket) {
        return null;
    }

    @Override
    public byte[] getContent() {
        return new byte[0];
    }
}
