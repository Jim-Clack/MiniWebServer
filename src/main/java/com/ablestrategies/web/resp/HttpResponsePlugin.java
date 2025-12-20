package com.ablestrategies.web.resp;

import com.ablestrategies.web.ServerManager;

import java.net.Socket;

@SuppressWarnings("ALL") // Plugins must extend this base class
public abstract class HttpResponsePlugin extends HttpResponse {

    public void initialize(String description) {
        this.description = description;
    }

    @Override
    public abstract ResponseCode generateContent(Socket socket);

    @Override
    public abstract byte[] getContent();
}
