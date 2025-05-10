package com.ablestrategies.web;

import java.util.*;

/**
 * This keeps track of sessions (with a given browser) via a session-id in
 * the header. All session state should be preserved herein.
 */
public class SessionManager {

    /** Keep track of session contexts. */
    private final Map<String, SessionContext> contexts = new HashMap<String, SessionContext>();

    /** Counter to guarantee uniqueness. */
    private long sessionCount = System.currentTimeMillis() % 0x7FFFFFFFFFFFFFFFL;

    /**
     * Ctor.
     */
    public SessionManager() {
        // Wisdom of Confucius...
        //  Step in the river.
        //  But the water has moved on.
        //  There's nothing to see.
    }

    /**
     * Get a session or create it if it does not exist.
     * @param sessionId To be fetched, or null to force it to create a new onw.
     * @return A valid session.
     */
    public synchronized SessionContext getOrCreateSession(String sessionId) {
        SessionContext context = null;
        if(sessionId != null && !sessionId.trim().isEmpty()) {
            context = contexts.get(sessionId);
        }
        if (context == null) {
            context = newSession();
        }
        return context;
    }

    /**
     * Get a unique session context.
     * @return Newly created session context.
     * @apiNote Not thread safe !!! (use getOrCreateSession() instead)
     */
    SessionContext newSession() {
        sessionCount = (sessionCount + 3L) & 0x0000FFFFFFFFFFFFL;
        long hi4bits = ((sessionCount * 2111L) & 0x7FFFL) * 0x1000000000000L;
        long reversedId = sessionCount | hi4bits;
        String sessionString = String.format("%016x", reversedId);
        StringBuilder buffer = new StringBuilder();
        for(byte byt : sessionString.getBytes()) {
            buffer.insert(0, (char)byt);
        }
        String sessionId = new String(buffer);
        SessionContext context = new SessionContext(sessionId);
        contexts.put(sessionId, context);
        return context;
    }

    /**
     * Get or create a session context.
     * @param sessionId The unique session ID.
     * @return the session context, possibly null if not found.
     * @apiNote Not thread safe !!! (use getOrCreateSession() instead)
     */
    SessionContext getSession(String sessionId) {
        return contexts.get(sessionId);
    }

}
