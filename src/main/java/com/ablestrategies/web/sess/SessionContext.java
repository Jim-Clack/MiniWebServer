package com.ablestrategies.web.sess;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class SessionContext {

    /** Sessions are tracked by exchanging a sessionId in the header. */
    private final String sessionId;

    /** Is this a newly created session? */
    private boolean isFresh = true;

    /** Stored state values. */
    private final Map<String, String> stringValues = new HashMap<>();

    /** Keep track of the timestamp of the last HTTP request. */
    private LocalDateTime lastActivity = LocalDateTime.now();

    /** Synchlock for access to lastActivity. */
    private final Integer lastActivityLock = 0;

    /** Keep track of when the session was started. */
    private final LocalDateTime created = LocalDateTime.now();

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

    /**
     * get the map of String values.
     * @return the map, which can be read or modified.
     */
    public Map<String, String> getStringValues() {
        return stringValues;
    }

    /**
     * Get a specific String value.
     * @param key To look up the value.
     * @return The value, null if not found.
     */
    public String getStringValue(String key) {
        return stringValues.get(key);
    }

    /**
     * Set a specific String value - add or update.
     * @param key The lookup key.
     * @param value The value.
     */
    public void setStringValue(String key, String value) {
        stringValues.put(key, value);
    }

    /**
     * Update activity timer.
     */
    public void setActivityNow() {
        synchronized (lastActivityLock) {
            lastActivity = LocalDateTime.now();
        }
    }

    /**
     * How long since the last request was handled?
     * @return number of seconds since last activity.
     */
    public long beenIdleForHowLong() {
        long seconds;
        synchronized (lastActivityLock) {
            seconds = ChronoUnit.SECONDS.between(lastActivity, LocalDateTime.now());
        }
        return seconds;
    }

    /**
     * Is this a freshly created session?
     * @return True, but only once - thereafter it will return false.
     */
    public boolean isFresh() {
        boolean returnValue = isFresh;
        isFresh = false;
        return returnValue;
    }

    /**
     * When was this session created?
     * @return Date/Time created.
     */
    public LocalDateTime getCreated() {
        return created;
    }

}
