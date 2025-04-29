package com.jc;

public class TransactionType {

    /**
     * What kind of request is this?
     */
    public enum RequestKind {
        RQ_FILE_GET,
        RQ_WS_SOAP,
        RQ_WS_JSON,
        RQ_WEB_CONSOLE,
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

    /*
     * Create an HttpResponseXxxx type based on the header info.
     * @param request Any kind of HttpRequestXxxx
     * @return An initialized HttpRequestXxxxx cloned from this request.
     */
    public static HttpRequestBase getTypedRequest(HttpRequestPojo request) {
        TransactionType.RequestKind requestKind = request.getRequestKind();
        if (requestKind == TransactionType.RequestKind.RQ_WS_SOAP) {
            return new HttpRequestSoap(request);
        } else if (requestKind == TransactionType.RequestKind.RQ_WS_JSON) {
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
        TransactionType.RequestKind requestKind = request.getRequestKind();
        if (requestKind == TransactionType.RequestKind.RQ_WS_SOAP) {
            return new HttpResponseSoap(request, configuration);
        } else if (requestKind == TransactionType.RequestKind.RQ_WS_JSON) {
            return new HttpResponseJson(request, configuration);
        } else if (requestKind == TransactionType.RequestKind.RQ_WEB_CONSOLE) {
            return new HttpResponseWebConsole(request, manager);
        }
        return new HttpResponseFile(request, configuration);
    }

}
