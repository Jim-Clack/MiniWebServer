package com.ablestrategies.web.rqst;

public class HttpRequestSoap extends HttpRequest {

    public HttpRequestSoap(HttpRequestPojo original) {
        super(null);
        cloneState(original);
    }

    // Not yet coded
}
