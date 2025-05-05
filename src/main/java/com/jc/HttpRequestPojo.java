package com.jc;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpRequestXxxx -> HttpRequestBase -> HttpRequestPojo
 *   Xxxx = Pojo - A lightweight class for state that can be cloned.
 *   Xxxx = Base - Socket HTTP Request message parsing methods.
 *   Xxxx = File - File transfer request or other kind of request.
 */
public class HttpRequestPojo {

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
     * Get the specified file path from the URL passed in line 1.
     * @param mustExist True if file is expected to exist, such as for read.
     * @return file to transfer.
     */
    public String getFilePath(boolean mustExist) {
        return UriParser.getFilePath(url, mustExist);
    }

    /**
     * Get a value from the query that is at the end of the url in line 1.
     * @param key the name of the query value to retrieve.
     * @param defaultValue the default to return if not found.
     * @return the value of that key.
     */
    public String getQueryValue(String key, String defaultValue) {
        return UriParser.queryString(url, key, defaultValue);
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
