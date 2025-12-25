package com.ablestrategies.web;

import junit.framework.TestCase;

public class ConsoleSupportTest extends TestCase {

    public void testGetMenu() {
        ServerManager manager = new ServerManager();
        ConsoleSupport con = new ConsoleSupport(manager);
        assertTrue(con.getMenu().contains("[C]onnections"));
    }

    public void testListAllThreads() {
        ServerManager manager = new ServerManager();
        ConsoleSupport con = new ConsoleSupport(manager);
        assertTrue(con.listAllThreads().contains("RUNNABLE"));
    }

    public void testListAllSessions() {
        ServerManager manager = new ServerManager();
        ConsoleSupport con = new ConsoleSupport(manager);
        assertTrue(con.listAllSessions().contains("Number of sessions"));
    }

    public void testListAllConnections() {
        ServerManager manager = new ServerManager();
        ConsoleSupport con = new ConsoleSupport(manager);
        assertTrue(con.listAllConnections().contains("Number of connections"));
    }

    public void testListProperties() {
        ServerManager manager = new ServerManager();
        ConsoleSupport con = new ConsoleSupport(manager);
        assertTrue(con.listAllProperties().contains("IPAddresses"));
    }
}