package com.jc;

import java.io.IOException;

/**
 * Test app for the MiniWebServer.
 * Put test html/jpg files into /users/<yourname>/webroot, then try these URLs...
 *   localhost:12345/webconsole
 *   localhost:12345/index.html
 * @see Server for more details
 */
public class App {

    /**
     * @param args portNumber and rootPath - both are optional.
     */
    public static void main(String[] args) {
        try {
            new Server(args).start();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

}