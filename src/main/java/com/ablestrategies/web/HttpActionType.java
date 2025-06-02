package com.ablestrategies.web;

import com.ablestrategies.web.conn.ContentMimeType;
import com.ablestrategies.web.resp.*;
import com.ablestrategies.web.rqst.*;

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
     * @param manager The top level manager that knows about all connections.
     * @return The appropriate type of HttpRequestXxxx,
     */
    public static HttpRequestBase getHttpRequest(String content, ServerManager manager) {
        HttpRequestBase request = new HttpRequestFile(content, manager);
        return getTypedRequest(request);
    }

    /**
     * What kind of mimeType is expected in the response for this request?
     * @return RequestKind, typically based on the Content-Type header.
     */
    public static RequestType getRequestKind(HttpRequestPojo request) {
        String[] acceptContents = request.getHeaderValues("Accept");
        if(acceptContents == null || acceptContents.length == 0) {
            acceptContents = request.getHeaderValues("Content-Type");
        }
        ContentMimeType responseMimeType = ContentMimeType.getMimeType(acceptContents);
        if(responseMimeType == ContentMimeType.MIME_XML) {
            return RequestType.RQ_WS_SOAP;
        } else if(responseMimeType == ContentMimeType.MIME_JSON) {
            return RequestType.RQ_WS_JSON;
        } else if(request.getUri().startsWith("/webconsole")) {
            return RequestType.RQ_WEB_CONSOLE;
        } else if(request.getMethod().equals("POST")) {
            return RequestType.RQ_FILE_POST;
        } // more to add as we develop more response types
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
     * @param manager Top level manager that is aware of all connections.
     * @return An initialized HttpResponseXxxxx suitable for this request.
     */
    @SuppressWarnings("ALL")
    public static HttpResponseBase getTypedResponse(HttpRequestPojo request, ServerManager manager) {
        RequestType requestType = getRequestKind(request);
        if (requestType == RequestType.RQ_WS_SOAP) {
            return new HttpResponseSoap(request);
        } else if (requestType == RequestType.RQ_WS_JSON) {
            return new HttpResponseJson(request);
        } else if (requestType == RequestType.RQ_WEB_CONSOLE) {
            return new HttpResponseWebConsole(request, manager);
        }
        return new HttpResponseFile(request);
    }

}
