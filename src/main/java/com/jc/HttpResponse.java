package com.jc;

import java.net.Socket;

public class HttpResponse {

    private final HttpRequest request;
    private final IConfiguration configuration;

    public HttpResponse(HttpRequest request, IConfiguration configuration) {
        this.request = request;
        this.configuration = configuration;
    }

    public int respond(Socket socket) {
        // TODO
        return 400;
    }

}
