package com.ablestrategies.web;

import java.net.Socket;

public class HttpResponseSoap extends HttpResponseBase {

    public HttpResponseSoap(HttpRequestPojo request) {
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
