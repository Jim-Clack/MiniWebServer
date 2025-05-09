package com.ablestrategies.web;

import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.*;

/**
 * This keeps track of sessions (with a given browser) via a session-id in
 * the header. All session state should be preserved herein.
 */
public class SessionManager {

    /** Keep track of session contexts. */
    private Map<String, SessionContext> contexts = new HashMap<String, SessionContext>();

    /** Counter to guarantee uniqueness. */
    private long sessionCount = System.currentTimeMillis() % 0x7FFFFFFFFFFFFFFFL;

    /**
     * Get a unique session context.
     * @return Newly created session context.
     */
    public SessionContext newSession() {
        sessionCount = (sessionCount + 1) & 0x7FFFFFFFFFFFFFFFL;
        String sessionString = String.format("%16x", sessionCount);
        StringBuilder buffer = new StringBuilder();
        for(byte byt : sessionString.getBytes()) {
            buffer.insert(0, byt);
        }
        String sessionId = buffer.toString();
        SessionContext context = new SessionContext(sessionId);
        contexts.put(sessionId, context);
        return context;
    }

    /**
     * Get or create a session context.
     * @param sessionId The unique session ID.
     * @return the session context, possibly null if not found.
     */
    public SessionContext getSession(String sessionId) {
        return contexts.get(sessionId);
    }

}
