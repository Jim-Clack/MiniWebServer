package com.jc;

public class HttpRequestJson extends HttpRequestBase {

    public HttpRequestJson(HttpRequestBase base) {
        super(null);
        cloneState(base);
    }

    // TODO - deal with a JSON body
}
