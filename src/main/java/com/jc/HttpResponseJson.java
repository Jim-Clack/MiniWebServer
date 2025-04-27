package com.jc;

import java.net.Socket;

public class HttpResponseJson extends HttpResponseBase {

    public HttpResponseJson(HttpRequestBase request, Configuration configuration) {
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
