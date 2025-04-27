package com.jc;

public class HttpRequestSoap extends HttpRequestBase {

    public HttpRequestSoap(HttpRequestBase base) {
        super(null);
        cloneState(base);
    }

    // TODO - deal with a SOAP body
}
