package com.jc;

import java.io.IOException;

/**
 * Very basic server, does NOT yet support...
 *   full exception/error handling
 *   HTTP > 1.1
 *   basic auth
 *   https
 *   plugins
 *   web services
 *   JEE servlets JSP
 *   multi-tenant directory trees
 *   zip/jar deployment
 */
public class Server
{

    private final String[] args;

    /**
     * Server.start()
     * @param args portNumber and rootPath - both are optional
     * @apiNote settings can be set via configuration, properties, or command-line
     */
    public Server(String[] args) {
        this.args = args;
    }

    public void start() throws IOException, InterruptedException {
        Configuration configuration = getConfiguration(args);
        ServerManager manager = new ServerManager();
        ListenerThread listener = new ListenerThread(manager, configuration);
        listener.start();
        LocalServerConsole console = new LocalServerConsole(manager, listener);
        Thread.sleep(1000);
        console.interact();
        manager.killIdleSessions(0L);
        Thread.yield();
        System.out.println("Done!");
        listener.interrupt();
    }

    private Configuration getConfiguration(String[] args) {
        Configuration configuration = new Configuration();
        if(args.length > 0) {
            configuration.setPortNumber(Integer.parseInt(args[0]));
            if(args.length > 1) {
                configuration.setRootPath(args[1]);
            }
        }
        return configuration;
    }

}
