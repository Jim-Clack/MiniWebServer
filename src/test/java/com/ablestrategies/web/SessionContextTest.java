package com.ablestrategies.web;

import junit.framework.TestCase;

public class SessionContextTest extends TestCase {

    private long now = System.currentTimeMillis();

    public void testGetSessionId() {
        String sessionId = "S" + now++;
        SessionContext context = new SessionContext(sessionId);
        assertEquals(sessionId, context.getSessionId());
    }

    public void testGetStringValues() {
        String sessionId = "S" + now++;
        SessionContext context = new SessionContext(sessionId);
        context.getStringValues().put("xyz", "7890");
        assertEquals("7890", context.getStringValue("xyz"));
    }

    public void testSetGetStringValue() {
        String sessionId = "S" + now++;
        SessionContext context = new SessionContext(sessionId);
        context.setStringValue("xyz", "123456");
        assertEquals("123456", context.getStringValue("xyz"));
    }

    public void testSetGetStringValueMultiple() {
        String sessionId = "S" + now++;
        SessionContext context = new SessionContext(sessionId);
        context.setStringValue("abc", "999");
        context.setStringValue("xyz", "123456");
        assertEquals("123456", context.getStringValue("xyz"));
        assertEquals("999", context.getStringValue("abc"));
    }

    public void testIsFresh() {
        String sessionId = "S" + now++;
        SessionContext context = new SessionContext(sessionId);
        assertTrue(context.isFresh());
        assertFalse(context.isFresh());
    }

}