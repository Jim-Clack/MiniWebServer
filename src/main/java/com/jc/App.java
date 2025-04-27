package com.jc;

import java.io.IOException;

/**
 * Put test html/jpg files into /users/<yourname>/webroot, then try these URLs...
 *   localhost:12345/webconsole
 *   localhost:12345/index.html
 */
public class App {
    public static void main(String[] args) {
        try {
            new Server(args).start();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}