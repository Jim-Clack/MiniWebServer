package com.jc;

/**
 * Simple static methods to assist with creating HTTP requests and responses.
 */
public class HttpActionType {

    /**
     * What kind of request is this?
     */
    public enum RequestType {
        RQ_FILE_GET,
        RQ_FILE_POST,   // not yet handled
        RQ_WEB_CONSOLE,
        RQ_WS_SOAP,     // not yet implemented
        RQ_WS_JSON,     // not yet implemented
    }

    /**
     * Create an HttpRequestXxxx of the appropriate type, based on header values.
     * @param content HTTP Request as received from a socket.
     * @param manager The top level manager that knows about all sessions.
     * @return The appropriate type of HttpRequestXxxx,
     */
    public static HttpRequestBase getHttpRequest(String content, ServerManager manager) {
        HttpRequestBase request = new HttpRequestFile(content, manager);
        return getTypedRequest(request);
    }

    /**
     * What kind of request is this?
     * @return RequestKind, typically based on the Content-Type header.
     */
    public static RequestType getRequestKind(HttpRequestPojo request) {
        String[] contentTypes = request.getHeader("Content-Type");
        String contentType = "text/html";
        if(contentTypes != null && contentTypes.length > 0) {
            contentType = contentTypes[0];
        }
        if(contentType.contains("/xml")) {
            return RequestType.RQ_WS_SOAP;
        } else if(contentType.contains("/json")) {
            return RequestType.RQ_WS_JSON;
        } else if(request.getFilePath().startsWith("/webconsole")) {
            return RequestType.RQ_WEB_CONSOLE;
        } else if(request.getMethod().equals("POST")) {
            return RequestType.RQ_FILE_POST;
        }
        return RequestType.RQ_FILE_GET;
    }

    /**
     * Create an HttpResponseXxxx type based on the header info.
     * @param request Any kind of HttpRequestXxxx
     * @return An initialized HttpRequestXxxxx cloned from this request.
     */
    public static HttpRequestBase getTypedRequest(HttpRequestPojo request) {
        RequestType requestType = getRequestKind(request);
        if (requestType == RequestType.RQ_WS_SOAP) {
            return new HttpRequestSoap(request);
        } else if (requestType == RequestType.RQ_WS_JSON) {
            return new HttpRequestJson(request);
        }
        return new HttpRequestFile(request);
    }

    /**
     * Instantiate a HttpResponseXxxxx based on the request.
     * @param request Any kind of HttpRequestXxxx
     * @param configuration Settings.
     * @param manager Top level manager that is aware of all sessions.
     * @return An initialized HttpResponseXxxxx suitable for this request.
     */
    public static HttpResponseBase getTypedResponse(
            HttpRequestPojo request, Configuration configuration, ServerManager manager) {
        RequestType requestType = getRequestKind(request);
        if (requestType == RequestType.RQ_WS_SOAP) {
            return new HttpResponseSoap(request, configuration);
        } else if (requestType == RequestType.RQ_WS_JSON) {
            return new HttpResponseJson(request, configuration);
        } else if (requestType == RequestType.RQ_WEB_CONSOLE) {
            return new HttpResponseWebConsole(request, manager);
        }
        return new HttpResponseFile(request, configuration);
    }

}
