package com.ablestrategies.web.rqst;

import com.ablestrategies.web.ServerManager;
import com.ablestrategies.web.conn.SessionContext;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpRequestXxxx -> HttpRequestBase -> HttpRequestPojo
 *   Xxxx = Pojo - A lightweight class for state that can be cloned.
 *   Xxxx = Base - Socket HTTP Request message parsing methods.
 *   Xxxx = File, etc. - File transfer request or other kind of request.
 */
public class HttpRequestPojo {

    /** The manager at the top level. */
    protected ServerManager manager;

    /** Here's where we stash the headers. */
    protected Map<String, String> headers = new HashMap<>();

    /** The HTTP body gets stored here. */
    protected StringBuilder body = new StringBuilder();

    /** This holds a code that indicates the state of this request. */
    protected RequestError errorCode = RequestError.UNINITIALIZED;

    /** The request method, such as GET. */
    protected String method = "?";

    /** URL of file with optional query string, but not protocol and domain. */
    protected String uri = "?";

    /** Typically "HTTP/1.1". */
    protected String version = "?";

    /** The current user/browser session. */
    private SessionContext context = null;

    /**
     * Get the session ID.
     * @param createIfNotExist true to create a new session if necessary.
     * @return Session ID for tracking user/browser sessions.
     */
    public String getSessionId(boolean createIfNotExist) {
        SessionContext context = getContext(createIfNotExist);
        if (context == null) {
            return null;
        }
       return context.getSessionId();
    }

    /**
     * Get the session context, creating it if it does not already exist.
     * @return A valid and current session context.
     */
    public SessionContext getContext(boolean createIfNotExist) {
        if(context == null && !createIfNotExist) {
            return null;
        }
        String sessionId = getRequestedSessionId();
        if(context == null) {
            context = manager.getSessionHandler().getOrCreateSession(sessionId);
        }
        // We certainly don't need all of these, but for now, they're useful diagnostics
        copyHeaderToStringValues("user-agent");
        copyHeaderToStringValues("authorization");
        copyHeaderToStringValues("accept");
        copyHeaderToStringValues("from");
        copyHeaderToStringValues("origin");
        copyHeaderToStringValues("referer");
        copyHeaderToStringValues("via");
        return context;
    }

    private @Nullable String getRequestedSessionId() {
        String sessionId = null;
        // unlike set-cookie, cookie uses a semicolon delimiter !
        String header = getHeaderValue("cookie");
        if (header != null && !header.isEmpty()) {
            String[] cookies = header.split(";");
            for (String cookie : cookies) {
                if (cookie.contains("sessionid-mws=")) {
                    String cookieValue = cookie.trim();
                    int indexOfComma = cookieValue.indexOf(",");
                    if (indexOfComma > 12) { // strip off optional attributes
                        cookieValue = cookieValue.substring(0, indexOfComma).trim();
                    }
                    String[] sections = cookieValue.split("=");
                    if (sections.length > 1) {
                        sessionId = sections[1].trim();
                        break;
                    }
                }
            }
        }
        return sessionId;
    }

    /**
     * If a chosen header exists, copy it to the Session string values.
     * @param key Lowercase header key.
     */
    private void copyHeaderToStringValues(String key) {
        String value = getHeaderValue(key);
        if(value != null && !value.isEmpty()) {
            context.setStringValue(key, value);
        }
    }

    /**
     * Get the specified file path from the URL passed in line 1.
     * @param mustExist True if file is expected to exist, such as for read.
     * @return file to transfer.
     */
    public String getFilePath(boolean mustExist) {
        return UriParser.getFilePath(uri, mustExist);
    }

    /**
     * Get a value from the query that is at the end of the url in line 1.
     * @param key the name of the query value to retrieve.
     * @param defaultValue the default to return if not found.
     * @return the value of that key.
     */
    public String getQueryValue(String key, String defaultValue) {
        return UriParser.queryString(uri, key, defaultValue);
    }

    /**
     * Fetch a value from the headers.
     * @param key The name of the header line.
     * @return the value of that key. There may be multiple comma-separated-values. May return null.
     */
    public String getHeaderValue(String key) {
        String header = this.headers.get(key.toLowerCase());
        if(header == null || header.isEmpty()) {
            return null;
        }
        return header;
    }

    /**
     * Fetch values from the headers.
     * @param key The name of the header line.
     * @return the value of that key. There may be multiple values. Never returns null.
     */
    public String[] getHeaderValues(String key) {
        String headers = this.headers.get(key.toLowerCase());
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
    public RequestError getErrorCode() {
        return errorCode;
    }

    /**
     * Fetch the complete URL, without the protocol and server IP/Domain/Port.
     * @return filepath, optionally followed by a "?" then query values, often from a form.
     */
    public String getUri() {
        return uri;
    }

    /**
     * Get the HTTP version.
     * @return typically "HTTP/1.1".
     */
    public String getVersion() {
        return version;
    }

}
