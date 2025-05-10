package com.ablestrategies.web.resp;

public enum ResponseCode {

    RC_UNKNOWN_ERROR(0, "Unrecognized Error"),
    RC_CONTINUE(100, "Continue"),
    RC_SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    RC_OK(200, "OK"),
    RC_CREATED(201, "Created"),
    RC_ACCEPTED(202, "Accepted"),
    RC_NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    RC_NO_CONTENT(204, "No Content"),
    RC_RESET_CONTENT(205, "Reset Content"),
    RC_PARTIAL_CONTENT(206, "Partial Content"),
    RC_MULTIPLE_CHOICES(300, "Multiple Choices"),
    RC_MOVED_PERMANENTLY(301, "Moved Permanently"),
    RC_FOUND(302, "Found"),
    RC_SEE_OTHER(303, "See Other"),
    RC_NOT_MODIFIED(304, "Not Modified"),
    RC_USE_PROXY(305, "Use Proxy"),
    RC_TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    RC_BAD_REQUEST(400, "Bad Request"),
    RC_UNAUTHORIZED(401, "Unauthorized"),
    RC_PAYMENT_REQUIRED(402, "Payment Required"),
    RC_FORBIDDEN(403, "Forbidden"),
    RC_NOT_FOUND(404, "Not Found"),
    RC_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    RC_NOT_ACCEPTABLE(406, "Not Acceptable");

    private final int numValue;
    private final String textValue;

    ResponseCode(int numValue, String textValue) {
        this.numValue = numValue;
        this.textValue = textValue;
    }

    public int getNumValue() {
        return numValue;
    }

    public String getTextValue() {
        return textValue;
    }
}
