package com.ablestrategies.web.conn;

import java.util.*;

/**
 * This keeps track of sessions (with a given browser) via a session-id in
 * the header. All session state should be preserved herein.
 */
public class SessionHandler {

    /** Keep track of session contexts. */
    private final Map<String, SessionContext> contexts = new HashMap<>();

    /** Counter to guarantee uniqueness. */
    private long sessionCount = System.currentTimeMillis() % 0x7FFFFFFFFFFFFFFFL;

    /** Used to detect NON-threadsafe code. (cannot make methods private because of unit tests.) */
    protected boolean developerWarning = true;

    /**
     * Ctor.
     */
    public SessionHandler() {
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
        developerWarning = false;
        SessionContext context = null;
        if(sessionId != null && !sessionId.trim().isEmpty()) {
            context = contexts.get(sessionId);
        }
        if (context == null) {
            context = newSession();
        }
        developerWarning = true;
        return context;
    }

    /**
     * Get a unique session context.
     * @return Newly created session context.
     * @apiNote Not thread safe !!! (use getOrCreateSession() instead)
     */
    protected SessionContext newSession() {
        if(developerWarning) {
            throw new RuntimeException("Don't call newSession() directly!");
        }
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
    protected SessionContext getSession(String sessionId) {
        if(developerWarning) {
            throw new RuntimeException("Don't call getSession() directly!");
        }
        return contexts.get(sessionId);
    }

}
