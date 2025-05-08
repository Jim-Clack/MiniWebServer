package com.ablestrategies.web;

import java.io.IOException;

/**
 * Basic web server - lightweight and easy to use.
 *   Handles HTML, PNG, JPEG, JS, CSS, etc,
 *   Supports both HTTP and HTTPS. (TLS/SSL)
 *   Has a local and a web console for managing the server.
 *   Can be embedded or standalone.
 *   Requires no extra code libraries except slf4j.
 * Put the files from webroot into /users/<yourname>/webroot, then try these URLs...
 *   localhost:12345/index.html
 *   localhost:12345/webconsole
 * Note: Requires slf4j 2 (i.e. slf4j-api:2.0.3 and slf4j-simple:2.0.3)
 * ---------------------------------------------------------------------------
 * Does NOT support...
 *   sessions based on a cookie and a sessionId (yet)
 *   basic auth, URL-based credentials (yet)
 *   web services, JSON, SOAP, etc. (yet)
 *   websockets
 *   multi-part messages
 *   HTTP other than 1.1, overlapping requests
 *   plugins
 *   JEE, servlets, JSP
 *   zip/jar/was/aar deployment
 *   load balancing
 *   alternate connections (non-HTTP)
 *   could also use more thorough exception/error handling
 * ---------------------------------------------------------------------------
 * You can pass in configuration settings or put them into the java properties
 *    Setting          arg[n] Java property              Default
 *    IP port to listen   0  MiniWebServer.portNumber    12345
 *    SSL IP listen port  1  MiniWebServer.sslPortNumber 0 (disabled)
 *    Website root path   2  MiniWebServer.rootPath      /Users/[user]/webroot
 * In order to support SSL/HTTPS, you have to set certain Java properties, as
 * listed in Preferences.java.
 * ---------------------------------------------------------------------------
 * You may link with this in order to create an embedded web server or run it
 * under "App,java" as a standalone web server. To call it, do this:
 *    new Server(args).start();
 * You will have to catch IOException and InterruptedException, although all
 * recoverable exceptions will be handled by the server without throwing.
 * ---------------------------------------------------------------------------
 */
public class Server
{

    /** Store args for passing from ctor to runtime. */
    private final String[] args;

    /**
     * Server.start()
     * @param args portNumber and rootPath - both are optional.
     * @apiNote settings can be set via configuration, properties, or command-line
     */
    public Server(String[] args) {
        this.args = args;
    }

    /**
     * Main entry point to start the server.
     * @throws IOException if an unrecoverable IO problem is encountered.
     * @throws InterruptedException if an interrupt occurs where not expected.
     */
    public void start() throws IOException, InterruptedException {
        setConfiguration(args);
        System.out.println("AbleStrategies MiniWebServer version " + Preferences.version);
        Thread.currentThread().setName("WebServer MainServerThread");
        ServerManager manager = new ServerManager();

        // Start HTTP listener
        ListenerThread httpListener = new ListenerThread("HTTP", manager);
        httpListener.start();

        // Start HTTPS listener
        if(Preferences.getInstance().getSslPortNumber() > 0) {
            ListenerThread httpsListener = new ListenerThread("HTTPS", manager);
            httpsListener.start();
        }

        LocalServerConsole console = new LocalServerConsole(manager, httpListener);
        Thread.sleep(1000);
        console.interact();
        manager.killIdleSessions(0L);
        Thread.yield();
        System.out.println("Done!");
        httpListener.interrupt();
    }

    /**
     * This handles command-line args and reading the configuration.
     * @param args portNumber and rootPath - both are optional.
     */
    private void setConfiguration(String[] args) {
        if(args.length > 0) {
            Preferences.getInstance().setPortNumber(Integer.parseInt(args[0]));
            if(args.length > 1) {
                Preferences.getInstance().setSslPortNumber(Integer.parseInt(args[1]));
                if (args.length > 2) {
                    Preferences.getInstance().setRootPath(args[2]);
                }
            }
        }
    }

}
