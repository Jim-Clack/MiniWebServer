package com.ablestrategies.web;

import java.util.HashMap;
import java.util.Map;

public class SessionContext {

    /** Sessions are tracked by exchanging a sessionId in the header. */
    private final String sessionId;

    /** Stored state values. */
    private Map<String, String> stringValues = new HashMap<String, String>();

    /**
     * Ctor.
     * @param sessionId Sessions are tracked by exchanging a sessionId in the header.
     */
    public SessionContext(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Access a collection of stored values.
     * @return Stored state values.
     */
    public String getSessionId() {
        return sessionId;
    }

    public Map<String, String> getStringValues() {
        return stringValues;
    }

}
