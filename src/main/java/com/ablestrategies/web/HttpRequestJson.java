package com.ablestrategies.web;

public class HttpRequestJson extends HttpRequestBase {

    public HttpRequestJson(HttpRequestPojo original) {
        super(null);
        cloneState(original);
    }

    // TODO - deal with a JSON body
}
