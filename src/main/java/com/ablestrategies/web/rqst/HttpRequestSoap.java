package com.ablestrategies.web.rqst;

public class HttpRequestSoap extends HttpRequestBase {

    public HttpRequestSoap(HttpRequestPojo original) {
        super(null);
        cloneState(original);
    }

    // Not yet coded
}
