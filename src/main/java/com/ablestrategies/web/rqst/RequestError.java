package com.ablestrategies.web.rqst;

/**
 * Error codes returned from parsing an HTTP Request.
 */
public enum RequestError {
    OK,
    UNINITIALIZED,
    ILLEGAL_METHOD,
    UNSUPPORTED_VERSION,
    BAD_FIRST_LINE,
    BAD_HEADER,
    // EMPTY_BODY, - no, not an error
}
