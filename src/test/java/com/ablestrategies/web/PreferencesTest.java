package com.ablestrategies.web;

import junit.framework.TestCase;

public class PreferencesTest extends TestCase {

    public void testPortNumber() {
        int portNumber = Preferences.getInstance().getPortNumber();
        System.setProperty("MiniWebServer.portNumber", "888");
        Preferences.getInstance().reset();
        assertEquals(888, Preferences.getInstance().getPortNumber());
        Preferences.getInstance().setPortNumber(777);
        assertEquals(777, Preferences.getInstance().getPortNumber());
        Preferences.getInstance().setPortNumber(portNumber);
        Preferences.getInstance().reset();
    }

    public void testSslPortNumber() {
        int sslPortNumber = Preferences.getInstance().getSslPortNumber();
        System.setProperty("MiniWebServer.sslPortNumber", "888");
        Preferences.getInstance().reset();
        assertEquals(888, Preferences.getInstance().getSslPortNumber());
        Preferences.getInstance().setSslPortNumber(777);
        assertEquals(777, Preferences.getInstance().getSslPortNumber());
        Preferences.getInstance().setSslPortNumber(sslPortNumber);
        Preferences.getInstance().reset();
    }

}