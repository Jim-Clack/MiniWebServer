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
        IConfiguration configuration = new Configuration();
        ServerManager manager = new ServerManager();
        if(args.length > 0) {
            configuration.setPortNumber(Integer.parseInt(args[0]));
            if(args.length > 1) {
                configuration.setRootPath(args[1]);
            }
        }
        ListenerThread listener = new ListenerThread(manager, configuration);
        listener.start();
        manager.console(listener);
        manager.killThreads(0L);
        Thread.yield();
        Logger.INFO("Done!");
        listener.interrupt();
    }

}
