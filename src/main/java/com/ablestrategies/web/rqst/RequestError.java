package com.ablestrategies.web.rqst;

/**
 * Error codes returned from parsing an HTTP Request.
 */
public enum RequestError {
    OK,
    EMPTY_BODY,
    UNINITIALIZED,
    ILLEGAL_METHOD,
    UNSUPPORTED_VERSION,
    BAD_FIRST_LINE,
    BAD_HEADER,
}
