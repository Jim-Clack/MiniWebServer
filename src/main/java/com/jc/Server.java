package com.jc;

import java.io.IOException;

/**
 * ---------------------------------------------------------------------------
 * Very basic server, does NOT yet support...
 *   full exception/error handling
 *   HTTP > 1.1
 *   basic auth
 *   plugins
 *   web services, JSON, SOAP
 *   JEE servlets JSP
 *   multi-tenant directory trees
 *   zip/jar deployment
 * ---------------------------------------------------------------------------
 * You can pass in configuration settings or put them into the java properties
 *    Setting          arg[n] Java property              Default
 *    IP port to listen   0  MiniWebServer.portNumber    12345
 *    SSL IP listen port  1  MiniWebServer.sslPortNumber 0 (disabled)
 *    Website root path   2  MiniWebServer.rootPath      /Users/[user]/webroot
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
        Configuration configuration = getConfiguration(args);
        ServerManager manager = new ServerManager();

        // Start HTTP listener
        ListenerThread httpListener = new ListenerThread("HTTP", manager, configuration);
        httpListener.start();

        // Start HTTPS listener
        if(configuration.getSslPortNumber() > 0) {
            ListenerThread httpsListener = new ListenerThread("HTTPS", manager, configuration);
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
     * @return the populated Configuration.
     */
    private Configuration getConfiguration(String[] args) {
        Configuration configuration = new Configuration();
        if(args.length > 0) {
            configuration.setPortNumber(Integer.parseInt(args[0]));
            if(args.length > 1) {
                configuration.setSslPortNumber(Integer.parseInt(args[1]));
            }
            if(args.length > 2) {
                configuration.setRootPath(args[2]);
            }
        }
        return configuration;
    }

}
