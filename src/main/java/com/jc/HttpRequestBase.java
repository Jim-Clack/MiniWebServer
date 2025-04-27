package com.jc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBase {

    public enum RequestKind {
        RQ_FILE_GET,
        RQ_WS_SOAP,
        RQ_WS_JSON,
    }

    private ServerManager manager;
    private Map<String, String> headers = new HashMap<>();
    private StringBuilder body = new StringBuilder();
    private ErrorCode errorCode = ErrorCode.UNINITIALIZED;
    private String method = "?";
    private String url = "?";
    private String version = "?";

    public HttpRequestBase(ServerManager manager) {
        this.manager = manager;
    }

    public void cloneState(HttpRequestBase original) {
        this.manager = original.manager;
        this.headers = original.headers;
        this.body = original.body;
        this.errorCode = original.errorCode;
        this.method = original.method;
        this.url = original.url;
        this.version = original.version;
    }

    protected void parseLineOne(String[] lines) {
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

    protected int parseHeaders(String[] lines) {
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

    protected void parseBody(int lineIndex, String[] lines) {
        for(; lineIndex < lines.length; lineIndex++) {
            body.append(lines[lineIndex]).append("\n");
        }
        if(body.toString().trim().isEmpty()) {
            errorCode = ErrorCode.EMPTY_BODY;
        } else {
            Logger.TRACE("HttpRequest body=\n" + body.toString());
        }
    }

    //////////////////////////// Public Getters //////////////////////////////

    /**
     * dreadful cheat...
     * @return an initialized HttpRequestXxxxx
     */
    public HttpRequestBase getTypedRequest() {
        RequestKind requestKind = getRequestKind();
        if (requestKind == HttpRequestBase.RequestKind.RQ_WS_SOAP) {
            return new HttpRequestSoap(this);
        } else if (requestKind == HttpRequestBase.RequestKind.RQ_WS_JSON) {
            return new HttpRequestJson(this);
        }
        return this;
    }

    /**
     * dreadful cheat...
     * @return an initialized HttpResponseXxxxx
     */
    public HttpResponseBase getTypedResponse(Configuration configuration) {
        RequestKind requestKind = getRequestKind();
        if (requestKind == HttpRequestBase.RequestKind.RQ_WS_SOAP) {
            return new HttpResponseSoap(this, configuration);
        } else if (requestKind == HttpRequestBase.RequestKind.RQ_WS_JSON) {
            return new HttpResponseJson(this, configuration);
        } else if(getFilePath().startsWith("/webconsole")) {
            return new HttpResponseWebConsole(this, manager);
        }
        return new HttpResponseFile(this, configuration);
    }

    public RequestKind getRequestKind() {
        if(Arrays.stream(getHeader("Content-Type")).anyMatch(s -> s.toLowerCase().contains("/xml"))) {
            return RequestKind.RQ_WS_SOAP;
        }
        if(Arrays.stream(getHeader("Content-Type")).anyMatch(s -> s.toLowerCase().contains("/json"))) {
            return RequestKind.RQ_WS_JSON;
        }
        return RequestKind.RQ_FILE_GET;
    }

    public String getFilePath() {
        int colon = url.indexOf(":");
        int question = url.indexOf("?");
        int justPast = url.length();
        if(colon > 0) {
            justPast = colon;
        }
        if(question > 0) {
            justPast = question;
        }
        return url.substring(0, justPast);
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
        String headers = this.headers.get(key);
        if(headers == null || headers.isEmpty()) {
            String[] stringArray = new String[1];
            stringArray[0] = "";
            return stringArray;
        }
        return headers.split(",");
    }

    public String getBody() {
        return body.toString();
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

}

