package com.ablestrategies.web;

import java.io.IOException;

/**
 * ---------------------------------------------------------------------------
 * Basic web server - lightweight and easy to use.
 * Refer to README.MD for usage and notes.
 *   TODO - Fix all the SuppressWarnings directives
 * ---------------------------------------------------------------------------
 */
public class Server
{

    /** Store args for passing from ctor to runtime. */
    private final String[] args;

    /** HTTP server. */
    private ListenerThread httpListener = null;

    /** HTTPS server. */
    private ListenerThread httpsListener = null;

    /** The management console on the local host. */
    private LocalServerConsole console = null;

    /** This object is in charge of all connections. */
    private ServerManager manager = null;

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
        initialize(args);
        startListeners();
        Thread.sleep(1000);
        console.interact();
        shutDown();
    }

    /**
     * This handles command-line args and reading the configuration.
     * @param args portNumber and rootPath - both are optional.
     */
    private void initialize(String[] args) {
        if(args.length > 0) {
            Preferences.getInstance().setPortNumber(Integer.parseInt(args[0]));
            if(args.length > 1) {
                Preferences.getInstance().setSslPortNumber(Integer.parseInt(args[1]));
                if (args.length > 2) {
                    Preferences.getInstance().setRootPath(args[2]);
                }
            }
        }
        System.out.println("AbleStrategies MiniWebServer version " + Preferences.version);
        Thread.currentThread().setName("WebServer Console-MainThread");
        manager = new ServerManager();
    }

    /**
     * Launch the HTTP and HTTPS listener threads.
     * @throws IOException Only for unrecoverable problems.
     */
    private void startListeners() throws IOException {
        // Start HTTP listener
        if(Preferences.getInstance().getPortNumber() > 0) {
            httpListener = new ListenerThread("HTTP", manager);
            httpListener.start();
            manager.setListener("HTTP", httpListener);
            console = new LocalServerConsole(manager);
        }
        // Start HTTPS listener
        if(Preferences.getInstance().getSslPortNumber() > 0) {
            httpsListener = new ListenerThread("HTTPS", manager);
            httpsListener.start();
            manager.setListener("HTTPS", httpListener);
            if(console == null) {
                console = new LocalServerConsole(manager);
            }
        }
    }

    /**
     * Shut down the server.
     */
    private void shutDown() {
        manager.killIdleConnections(0L);
        Thread.yield();
        System.out.println("Done!");
        if(httpsListener != null) {
            httpsListener.interrupt();
        }
        if(httpListener != null) {
            httpListener.interrupt();
        }
    }

}
