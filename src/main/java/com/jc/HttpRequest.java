package com.jc;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    int code = 10;
    String method = "?";
    String url = "?";
    String version = "?";
    Map<String, String> headers = new HashMap<>();
    StringBuilder body = new StringBuilder();

    public HttpRequest(String content) {
        int lineIndex;
        String[] lines = content.split("\n");
        String[] tokens = lines[0].split(" ");
        if(tokens.length < 3) {
            code = 9; // TODO
        } else {
            method = tokens[0].trim();
            url = tokens[1].trim();
            version = tokens[2].trim();
        }
        if(method.equals("GET") && version.equals("HTTP/1.1")) {
            code = 0;
        }
        Logger.INFO("HttpRequest code=" + code + ", method=" + method + ", url=" + url + ", version=" + version);
        for(lineIndex = 1; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex].trim();
            if(line.length() <= 0) {
                break;
            }
            String[] fields = line.split("[:,]");
            headers.put(fields[0].trim(), fields[1].trim());
            Logger.INFO("HttpRequest header key=" + fields[0].trim() + ", value=" + fields[1].trim());
        }
        for(; lineIndex < lines.length; lineIndex++) {
            body.append(lines[lineIndex]).append("\n");
        }
        Logger.INFO("HttpRequest body=\n" + body.toString());
    }

    public int getCode() {
        return code;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String[] getHeader(String key) {
        return headers.get(key).split(",");
    }

    public String getBody() {
        return body.toString();
    }
}

