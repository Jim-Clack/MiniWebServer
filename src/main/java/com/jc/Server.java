package com.jc;

import java.io.IOException;

public class Server
{
    private static ListenerThread listener = null;

    public static void main(String[] args) throws IOException, InterruptedException {
        IConfiguration configuration = new Configuration();
        ServerManager manager = new ServerManager();
        listener = new ListenerThread(manager, configuration);
        listener.start();
        manager.console(listener);
        manager.killThreads(0L);
        Thread.yield();
        Logger.INFO("Done!");
        listener.interrupt();
    }

}
