package com.ablestrategies.web.resp;

import com.ablestrategies.web.rqst.HttpRequestPojo;

import java.net.Socket;

@SuppressWarnings("ALL") // Until we flesh this class out
public class HttpResponsePlugin extends HttpResponseBase {

    public HttpResponsePlugin(HttpRequestPojo request) {
        this.description = "Plug-in";
        // Not yet coded
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
