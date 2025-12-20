package com.ablestrategies.web.client;

import com.ablestrategies.web.Preferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Be sure to copy the webroot folder to your home directory before running this.
 */
public class Tester {

    private static final String BufferWithQuery =
            "GET /index.html?qu=samp&qty=1 HTTP/1.1\n" +
                    "Host: localhost\n" +
                    "Content-Length: 32\n" +
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\n" +
                    "\n" +
                    "<html><body>Hello</body></html>\n";

    public static void main(String[] args) {
        try {
            Thread.sleep(5000); // wait for server to come up
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException(ex);
        }
        try (
            Socket socket = new Socket("localhost", Preferences.getInstance().getPortNumber())
        ) {
            InputStream inputStream = socket.getInputStream();
            socket.getOutputStream().write(BufferWithQuery.getBytes(StandardCharsets.UTF_8));
            int avail;
            for(avail = 0; avail < 1; avail = inputStream.available()) {
                Thread.yield(); // spinlock, wait for response
            }
            byte[] bytes = inputStream.readNBytes(avail);
            String response = new String(bytes, StandardCharsets.UTF_8);
            showResults(response);
        } catch(IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void showResults(String response) {
        System.out.println("Response...\n\n" + response + "\n");
        if(!response.contains("HTTP/1.1 200 OK")) {
            System.out.println("Bad response!\n!");
        } else if(!response.contains("content-length:")) {
            System.out.println("Missing header!");
        } else if(!response.contains("<title>MiniWebServer</title>")) {
            System.out.println("Missing body!");
        } else {
            System.out.println("Success!");
        }
        System.out.println("(Remember to shut down server, now)");
    }
}

/*
HTTP/1.1 200 OK
content-type: text/html; charset=UTF-8
set-cookie: sessionid-mws=12924873b910f172
date: Fri, 19 Dec 2025 11:49:33 EST
cache-control: max-age=15
server: mws-MiniWebServer AbleStrategies
content-length: 606

<!DOCTYPE html>
<html lang="en">
<!-- Put these four files in the web root path -->
<head>
   <meta charset="UTF-8">
   <title>MiniWebServer</title>
</head>
<body>
  <link rel="stylesheet" href="itest.css">
  <script src="itest.js"></script>
  <div style="width: 99%; align-content: center; text-align: center; display: block;">
  <h1>Test Page</h1>
    <h3 id="welcome">The MiniWebServer is up and running!</h3>
    <button type="button" onclick="sayWelcome()">Click Me!</button>
    <hr/>
    <img alt="there's supposed to be an image here" src="image.png" />
  </div>
</body>
</html>
*/

