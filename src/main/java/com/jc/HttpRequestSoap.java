package com.jc;

public class HttpRequestSoap extends HttpRequestBase {

    public HttpRequestSoap(HttpRequestPojo original) {
        super(null);
        cloneState(original);
    }

    // TODO - deal with a SOAP body
}
