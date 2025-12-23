package com.ablestrategies.web.conn;

import com.ablestrategies.web.sess.SessionContext;
import com.ablestrategies.web.sess.SessionHandler;
import junit.framework.TestCase;

import java.util.HashSet;

public class SessionHandlerTest extends TestCase {

    @SuppressWarnings("ALL")
    public void testSessionIdUniqueness() {
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setDeveloperWarning(false);
        HashSet<String> sessionIds = new HashSet<String>();
        for(int i = 0; i < 10000; i++) {
            String sessionId = sessionHandler.newSession(null).getSessionId();
            //System.out.println(sessionId);
            if(sessionIds.contains(sessionId)) {
                fail("Duplicate session id: " + sessionId);
            }
            sessionIds.add(sessionId);
        }
    }

    public void testNewSession() {
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setDeveloperWarning(false);
        SessionContext context = sessionHandler.newSession(null);
        assertNotNull(context);
        String sessionId = context.getSessionId();
        assertNotNull(sessionId);
    }

    public void testGetSession() {
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setDeveloperWarning(false);
        SessionContext context1 = sessionHandler.newSession(null);
        assertNotNull(context1);
        String sessionId1 = context1.getSessionId();
        SessionContext context2 = sessionHandler.newSession(null);
        assertNotNull(context2);
        SessionContext context1b = sessionHandler.getSession(sessionId1);
        assertNotNull(context1b);
        String sessionId1b = context1b.getSessionId();
        assertEquals(sessionId1, sessionId1b);
        context1.setStringValue("xyz", "xxxx");
        assertEquals("xxxx", context1b.getStringValue("xyz"));
    }

    public void testGetOrCreateSession() {
        long now = System.currentTimeMillis();
        String sessionId0 = "S" + now;
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setDeveloperWarning(false);
        SessionContext context1 = sessionHandler.getOrCreateSession(sessionId0); // doesn't exist, so create
        assertNotNull(context1);
        String sessionId1 = context1.getSessionId();
        assertEquals(sessionId0, sessionId1);
        SessionContext context2 = sessionHandler.getOrCreateSession(null); // doesn't exist, so create
        assertNotNull(context2);
        assertNotSame(sessionId1, context2.getSessionId());
        SessionContext context3 = sessionHandler.getOrCreateSession(sessionId1); // exists, so get
        assertNotNull(context3);
        assertEquals(sessionId1, context3.getSessionId()); // should have gotten sessionId1

    }
}