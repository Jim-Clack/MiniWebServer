package com.ablestrategies.web.rqst;

public class HttpRequestJson extends HttpRequest {

    public HttpRequestJson(HttpRequestPojo original) {
        super(null);
        cloneState(original);
    }

    // Not yet coded
}
