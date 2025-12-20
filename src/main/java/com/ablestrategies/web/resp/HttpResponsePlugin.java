package com.ablestrategies.web.resp;

import com.ablestrategies.web.rqst.HttpRequestPojo;

import java.net.Socket;

@SuppressWarnings("ALL") // Plugins must extend this base class
public abstract class HttpResponsePlugin extends HttpResponse {

    public abstract void initialize(HttpRequestPojo request, Socket socket);

    // @Override
    //   generateContent()
    //   getContent()
    //   set description

    @Override
    public abstract ResponseCode generateContent(Socket socket);

    @Override
    public abstract byte[] getContent();
}
