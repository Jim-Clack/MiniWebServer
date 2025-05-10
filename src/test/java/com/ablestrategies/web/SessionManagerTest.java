package com.ablestrategies.web;

import junit.framework.TestCase;

import java.util.HashSet;

public class SessionManagerTest extends TestCase {

    @SuppressWarnings("ALL")
    public void testSessionIdUniqueness() {
        SessionManager sessionManager = new SessionManager();
        HashSet<String> sessionIds = new HashSet<String>();
        for(int i = 0; i < 10000; i++) {
            String sessionId = sessionManager.newSession().getSessionId();
            //System.out.println(sessionId);
            if(sessionIds.contains(sessionId)) {
                fail("Duplicate session id: " + sessionId);
            }
            sessionIds.add(sessionId);
        }
    }

    public void testNewSession() {
        SessionManager sessionManager = new SessionManager();
        SessionContext context = sessionManager.newSession();
        assertNotNull(context);
        String sessionId = context.getSessionId();
        assertNotNull(sessionId);
    }

    public void testGetSession() {
        SessionManager sessionManager = new SessionManager();
        SessionContext context1 = sessionManager.newSession();
        assertNotNull(context1);
        String sessionId1 = context1.getSessionId();
        SessionContext context2 = sessionManager.newSession();
        assertNotNull(context2);
        SessionContext context1b = sessionManager.getSession(sessionId1);
        assertNotNull(context1b);
        String sessionId1b = context1b.getSessionId();
        assertEquals(sessionId1, sessionId1b);
        context1.setStringValue("xyz", "xxxx");
        assertEquals("xxxx", context1b.getStringValue("xyz"));
    }

    public void testGetOrCreateSession() {
        long now = System.currentTimeMillis();
        String sessionId0 = "S" + now;
        SessionManager sessionManager = new SessionManager();
        SessionContext context1 = sessionManager.getOrCreateSession(sessionId0); // doesn't exist, so create
        assertNotNull(context1);
        String sessionId1 = context1.getSessionId();
        assertNotSame(sessionId0, sessionId1);
        SessionContext context2 = sessionManager.getOrCreateSession(null); // doesn't exist, so create
        assertNotNull(context2);
        assertNotSame(sessionId1, context2.getSessionId());
        SessionContext context3 = sessionManager.getOrCreateSession(sessionId1); // exists, so get
        assertNotNull(context3);
        assertEquals(sessionId1, context3.getSessionId()); // should have gotten sessionId1

    }
}