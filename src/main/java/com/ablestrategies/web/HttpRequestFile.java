package com.ablestrategies.web;

/**
 * File request AS WELL AS any unknown type of request.
 * Veneer class, as most functionality if in HttpRequestBase.
 */
public class HttpRequestFile extends HttpRequestBase {

    /**
     * Ctor.
     * @param content A HTTP request as read from a socket.
     * @param manager Top-level class that manages all sessions.
     */
    public HttpRequestFile(String content, ServerManager manager) {
        super(manager);
        String[] lines = content.split("\n");
        parseStatusLine(lines[0]);
        int lineIndex = parseHeaders(lines);
        parseBody(lineIndex, lines);
    }

    /**
     * Copy constructor.
     * @param original Original - state will be cloned from original to this one.
     */
    public HttpRequestFile(HttpRequestPojo original) {
        super(null);
        cloneState(original);
    }

}
