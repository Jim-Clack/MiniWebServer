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
    @SuppressWarnings("ALL")
    public synchronized SessionContext getOrCreateSession(String sessionId) {
        developerWarning = false;
        SessionContext context = null;
        if(sessionId != null && !sessionId.trim().isEmpty()) {
            if(doesSessionExist(sessionId)) {
                context = contexts.get(sessionId);
            }
            context = contexts.get(sessionId);
        }
        if (context == null) {
            context = newSession(sessionId);
        }
        developerWarning = true;
        context.setActivityNow();
        return context;
    }

    /**
     * Does a session exist?
     * @param sessionId The session ID.
     * @return True only if it exists.
     */
    public synchronized boolean doesSessionExist(String sessionId) {
        if(sessionId != null && !sessionId.trim().isEmpty()) {
            return contexts.containsKey(sessionId);
        }
        return false;
    }

    /**
     * Delete inactive or invalid sessions.
     * @param maxIdleSeconds If idle this many seconds.
     */
    public synchronized void deleteSessionsIfIdle(long maxIdleSeconds) {
        for(SessionContext context : contexts.values()) {
            if (context != null && context.beenIdleForHowLong() > maxIdleSeconds) {
                contexts.remove(context.getSessionId());
            }
        }
    }

    /**
     * Get a copy of the sessions.
     * @return Copies of all session contexts.
     */
    public List<SessionContext> getSessions() {
        // Synchronized collections have issues with iteration, so we synchronize
        // it here (in the LinkedList ctor) to return a copy of the list.
        synchronized (this) {
            return new LinkedList<>(contexts.values());
        }
    }

    /**
     * Get a unique session context.
     * @param sessionId null to generate a new sessionId, else the desired one
     * @return Newly created session context.
     * @apiNote Not thread safe !!! (use getOrCreateSession() instead)
     */
    protected SessionContext newSession(String sessionId) {
        if(developerWarning) {
            throw new RuntimeException("Don't call newSession() directly!");
        }
        if(sessionId == null) {
            sessionCount = (sessionCount + 3L) & 0x0000FFFFFFFFFFFFL;
            long hi4bits = ((sessionCount * 2111L) & 0x7FFFL) * 0x1000000000000L;
            long reversedId = sessionCount | hi4bits;
            String sessionString = String.format("%016x", reversedId);
            StringBuilder buffer = new StringBuilder();
            for (byte byt : sessionString.getBytes()) {
                buffer.insert(0, (char) byt);
            }
            sessionId = new String(buffer);
        }
        if(doesSessionExist(sessionId)) {
            return contexts.get(sessionId);
        }
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
