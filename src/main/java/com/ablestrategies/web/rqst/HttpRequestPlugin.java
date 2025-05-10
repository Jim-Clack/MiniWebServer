package com.ablestrategies.web.rqst;

@SuppressWarnings("ALL") // Until we flesh this class out
public class HttpRequestPlugin extends HttpRequestBase {

    public HttpRequestPlugin(HttpRequestPojo original) {
        super(null);
        cloneState(original);
    }

    // TODO - deal with an extension/plugin body
}
