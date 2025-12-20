package com.ablestrategies.web;

/**
 * Test app for the MiniWebServer.
 * @see Server for more details.
 */
public class App {

    /**
     * @param args HTTP-port, HTTPS-port, and WebRoot - all are optional, 0=disabled.
     */
    public static void main(String[] args) {
        try {
            new Server(args).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}