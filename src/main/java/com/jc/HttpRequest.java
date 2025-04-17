package com.jc;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    Map<String, String> headers = new HashMap<>();
    StringBuilder body = new StringBuilder();
    ErrorCode errorCode = ErrorCode.UNINITIALIZED;
    String method = "?";
    String url = "?";
    String version = "?";

    public HttpRequest(String content) {
        int lineIndex;
        String[] lines = content.split("\n");
        String[] tokens = lines[0].split(" ");
        if(tokens.length < 3) {
            errorCode = ErrorCode.BAD_FIRST_LINE;
        } else {
            errorCode = ErrorCode.OK;
            method = tokens[0].trim();
            url = tokens[1].trim();
            version = tokens[2].trim();
        }
        if(!method.equals("GET")) {
            errorCode = ErrorCode.ILLEGAL_METHOD;
        }
        if(!version.equals("HTTP/1.1")) {
            errorCode = ErrorCode.UNSUPPORTED_VERSION;
        }
        Logger.INFO("HttpRequest code=" + errorCode + ", method=" + method + ", url=" + url + ", version=" + version);
        for(lineIndex = 1; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex].trim();
            if(line.length() <= 0) {
                break;
            }
            String[] fields = line.split("[:,]", 1);
            if(fields.length < 2) {
                Logger.INFO("Bad Header: " + line);
                errorCode = ErrorCode.BAD_HEADER;
            }
            headers.put(fields[0].trim(), fields[1].trim());
            Logger.INFO("HttpRequest header key=" + fields[0].trim() + ", value=" + fields[1].trim());
        }
        for(; lineIndex < lines.length; lineIndex++) {
            body.append(lines[lineIndex]).append("\n");
            if(body.toString().trim().isEmpty()) {
                errorCode = ErrorCode.EMPTY_BODY;
            }
        }
        Logger.INFO("HttpRequest body=\n" + body.toString());
    }

    public ErrorCode getErrorCode() {
        return errorCode;
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

