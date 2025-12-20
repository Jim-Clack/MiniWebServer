package com.ablestrategies.web.rqst;

/**
 * This is just a shell to pass the request to a plugin
 */
public class HttpRequestPlugin extends HttpRequest {

    public HttpRequestPlugin(HttpRequestPojo original) {
        super(null);
        cloneState(original);
    }
}
