package com.ablestrategies.web;

import java.io.IOException;

/**
 * Test app for the MiniWebServer.
 * @see Server for more details.
 */
public class App {

    /**
     * @param args portNumber, SSL-portNumber, and rootPath - all are optional.
     */
    public static void main(String[] args) {
        try {
            new Server(args).start();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

}