package com.jc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Request class hierarchy...
 *     HttpRequestXxxx -> HttpRequestBase -> HttpRequestPojo
 *     Xxxx can be File, Json, or Soap
 * Why?
 *     Pojo - A lightweight class for state that can be cloned.
 *     Base - Socket HTTP Request message parsing methods.
 *     Xxxx - Specific types of requests
 */
public class HttpRequestPojo {

    /** What kind of request is this? */
    public enum RequestKind {
        RQ_FILE_GET,
        RQ_WS_SOAP,
        RQ_WS_JSON,
    }

    /** The manager at the top level. */
    protected ServerManager manager;

    /** Here's where we stash the headers. */
    protected Map<String, String> headers = new HashMap<>();

    /** The HTTP body gets stored here. */
    protected StringBuilder body = new StringBuilder();

    /** This holds a code that indicates the state of this request. */
    protected ErrorCode errorCode = ErrorCode.UNINITIALIZED;

    /** The request method, such as GET. */
    protected String method = "?";

    /** URL of file with optional query string, but not protocol and domain. */
    protected String url = "?";

    /** Typically "HTTP/1.1". */
    protected String version = "?";

    /**
     * Dreadful cheat...
     * @return An initialized HttpRequestXxxxx cloned from this request.
     */
    public HttpRequestBase getTypedRequest() {
        RequestKind requestKind = getRequestKind();
        if (requestKind == HttpRequestPojo.RequestKind.RQ_WS_SOAP) {
            return new HttpRequestSoap(this);
        } else if (requestKind == HttpRequestPojo.RequestKind.RQ_WS_JSON) {
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
        if (requestKind == HttpRequestPojo.RequestKind.RQ_WS_SOAP) {
            return new HttpResponseSoap(this, configuration);
        } else if (requestKind == HttpRequestPojo.RequestKind.RQ_WS_JSON) {
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
