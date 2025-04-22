package com.jc;

import java.io.IOException;

public class Server
{
    /**
     * Server.main()
     * @param args portNumber and rootPath - both are optional
     * @apiNote settings can be set via configuration, properties, or command-line
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Configuration configuration = getConfiguration(args);
        ServerManager manager = new ServerManager();
        ListenerThread listener = new ListenerThread(manager, configuration);
        listener.start();
        ServerConsole console = new ServerConsole(manager, listener);
        console.interact();
        manager.killIdleSessions(0L);
        Thread.yield();
        System.out.println("Done!");
        listener.interrupt();
    }

    private static Configuration getConfiguration(String[] args) {
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
