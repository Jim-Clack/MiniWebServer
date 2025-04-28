package com.jc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Big class for handling all kinds of HTTP requests. Accepts the raw message
 * and parses out the url, headers, and body.
 * ---------------------------------------------------------------------------
 * This file contains the following methods in this order...
 *   ctor() and cloneState()   initialization
 *   parseXxxx()               methods to process the raw HTTP message
 *   getXxxx()                 lots of getters
 * ---------------------------------------------------------------------------
 * All of the HttpRequestXxxx classes extend this. They can be created by
 * calling their ctors or by cloning the state of another HttpRequestXxxx.
 */
public class HttpRequestBase {

    /** What kind of request is this? */
    public enum RequestKind {
        RQ_FILE_GET,
        RQ_WS_SOAP,
        RQ_WS_JSON,
    }

    /** The manager at the top level. */
    private ServerManager manager;

    /** Here's where we stash the headers. */
    private Map<String, String> headers = new HashMap<>();

    /** The HTTP body gets stored here. */
    private StringBuilder body = new StringBuilder();

    /** This holds a code that indicates the state of this request. */
    private ErrorCode errorCode = ErrorCode.UNINITIALIZED;

    /** The request method, such as GET. */
    private String method = "?";

    /** URL of file with optional query string, but not protocol and domain. */
    private String url = "?";

    /** Typically "HTTP/1.1". */
    private String version = "?";

    /**
     * Ctor.
     * @param manager The server manager. May be null if cloneState() will be called afterwards.
     */
    public HttpRequestBase(ServerManager manager) {
        this.manager = manager;
    }

    /**
     * Populate this HttpRequestXxxx from another.
     * @param original to copy the state from. (into this one)
     */
    public void cloneState(HttpRequestBase original) {
        this.manager = original.manager;
        this.headers = original.headers;
        this.body = original.body;
        this.errorCode = original.errorCode;
        this.method = original.method;
        this.url = original.url;
        this.version = original.version;
    }

    /**
     * Process the first line of the raw HTTP request.
     * @param lines the raw data, split at endlines.
     */
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

    /**
     * Process the headers from the raw data.
     * @param lines the raw data, split at endlines.
     * @return the line number of the last header line.
     */
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

    /**
     * Process the HTTP body - often empty.
     * @param lineIndex Line number of the start of the body.
     * @param lines the raw data, split at endlines.
     */
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
     * Dreadful cheat...
     * @return An initialized HttpRequestXxxxx cloned from this request.
     */
    public HttpRequestBase getTypedRequest() {
        RequestKind requestKind = getRequestKind();
        if (requestKind == HttpRequestBase.RequestKind.RQ_WS_SOAP) {
            return new HttpRequestSoap(this);
        } else if (requestKind == HttpRequestBase.RequestKind.RQ_WS_JSON) {
            return new HttpRequestJson(this);
        }
        return new HttpRequestFile(this);
    }

    /**
     * Dreadful cheat...
     * @return An initialized HttpResponseXxxxx suitable for this request.
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

    /**
     * What kind of request is this?
     * @return RequestKind, often based on the Content-Type header.
     */
    public RequestKind getRequestKind() {
        if(Arrays.stream(getHeader("Content-Type")).anyMatch(s -> s.toLowerCase().contains("/xml"))) {
            return RequestKind.RQ_WS_SOAP;
        }
        if(Arrays.stream(getHeader("Content-Type")).anyMatch(s -> s.toLowerCase().contains("/json"))) {
            return RequestKind.RQ_WS_JSON;
        }
        return RequestKind.RQ_FILE_GET;
    }

    /**
     * Get the specified file path from the URL passed in line 1.
     * @return file to transfer.
     */
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

    /**
     * Get a value from the query that is at the end of the url in line 1.
     * @param key the name of the query value to retrieve.
     * @param defaultValue the default to return if not found.
     * @return the value of that key.
     */
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

    /**
     * Fetch values from the headers.
     * @param key The name of the header line.
     * @return the value of that key. There may be multiple comma-separated-values.
     */
    public String[] getHeader(String key) {
        String headers = this.headers.get(key);
        if(headers == null || headers.isEmpty()) {
            String[] stringArray = new String[1];
            stringArray[0] = "";
            return stringArray;
        }
        return headers.split(",");
    }

    /**
     * Get the body of the HTTP request.
     * @return The body, possible empty, but not null.
     */
    public String getBody() {
        return body.toString();
    }

    /**
     * Get the request method.
     * @return The name of the method, typically "GET" or "POST".
     */
    public String getMethod() {
        return method;
    }

    /**
     * Get the error code.
     * @return ErrorCode.Xxxx, hopefully OK or EMPTY_BODY.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Fetch the complete URL, without the protocol and server IP/Domain/Port.
     * @return filepath, optionally followed by a "?" then query values, often from a form.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the HTTP version.
     * @return typically "HTTP/1.1".
     */
    public String getVersion() {
        return version;
    }

}
