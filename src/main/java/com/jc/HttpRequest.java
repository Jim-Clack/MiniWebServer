package com.jc;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final Map<String, String> headers = new HashMap<>();
    private final StringBuilder body = new StringBuilder();
    private ErrorCode errorCode = ErrorCode.UNINITIALIZED;
    private String method = "?";
    private String url = "?";
    private String version = "?";

    public HttpRequest(String content) {
        String[] lines = content.split("\n");
        parseLineOne(lines);
        int lineIndex = parseHeaders(lines);
        parseBody(lineIndex, lines);
    }

    public String getQueryValue(String key, String defaultValue) {
        int index = url.indexOf(key + "=") + 1;
        if(index <= 0) {
            return defaultValue;
        }
        int pastValue = url.indexOf("&", index + key.length());
        if(pastValue == -1) {
            pastValue = url.length();
        }
        return url.substring(index + key.length(), pastValue);
    }

    public String[] getHeader(String key) {
        return headers.get(key).split(",");
    }

    public String getBody() {
        return body.toString();
    }

    public String getFilePath() {
        int colon = url.indexOf(":");
        int question = url.indexOf(":");
        int justPast = url.length();
        if(colon != -1) {
            justPast = colon;
        }
        if(question != -1) {
            justPast = question;
        }
        return url.substring(0, justPast);
    }

    public String getMethod() {
        return method;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    private void parseLineOne(String[] lines) {
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
        Logger.DEBUG("HttpRequest code=" + errorCode + ", method=" + method + ", url=" + url + ", version=" + version);
    }

    private int parseHeaders(String[] lines) {
        int lineIndex;
        for(lineIndex = 1; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex].trim();
            if(line.isEmpty()) {
                break;
            }
            String[] fields = line.split("[:,]", 2);
            if(fields.length < 2) {
                Logger.WARN("Bad Header in HttpRequest: " + line);
                errorCode = ErrorCode.BAD_HEADER;
            }
            headers.put(fields[0].trim(), fields[1].trim());
            Logger.TRACE("HttpRequest header key=" + fields[0].trim() + ", value=" + fields[1].trim());
        }
        return lineIndex;
    }

    private void parseBody(int lineIndex, String[] lines) {
        for(; lineIndex < lines.length; lineIndex++) {
            body.append(lines[lineIndex]).append("\n");
        }
        if(body.toString().trim().isEmpty()) {
            errorCode = ErrorCode.EMPTY_BODY;
        } else {
            Logger.TRACE("HttpRequest body=\n" + body.toString());
        }
    }

}

