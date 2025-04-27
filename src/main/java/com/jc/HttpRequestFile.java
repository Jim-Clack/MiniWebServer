package com.jc;

/**
 * File request AS WELL AS any unknown type of request
 */
public class HttpRequestFile extends HttpRequestBase {

    public HttpRequestFile(String content, ServerManager manager) {
        super(manager);
        String[] lines = content.split("\n");
        parseLineOne(lines);
        int lineIndex = parseHeaders(lines);
        parseBody(lineIndex, lines);
    }

}
