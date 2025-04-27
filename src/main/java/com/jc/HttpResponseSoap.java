package com.jc;

import java.net.Socket;

public class HttpResponseSoap extends HttpResponseBase {

    public HttpResponseSoap(HttpRequestBase request, Configuration configuration) {
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
