package com.ablestrategies.web;

/**
 * This does periodical cleanup, like killing idle connections.
 */
public class IdleLoopThread extends Thread {

    /** Need to call the server manager to kill idle connections. */
    private final ServerManager manager;

    /**
     * Ctor.
     * @param manager The server manager to be called for cleanup.
     */
    IdleLoopThread(ServerManager manager) {
        this.manager = manager;
        setDaemon(true);
    }

    /**
     * Thread loop.
     */
    @SuppressWarnings("all")
    public void run() {
        int maxIdle = Preferences.getInstance().getMaxIdleSeconds();
        setName("WebServer " + maxIdle + "-IdleLoopThread");
        while(!isInterrupted()) {
            try {
                sleep(10000); // check every 10 seconds
            } catch (InterruptedException e) {
                // ignore interrupts
            }
            manager.killIdleConnections(maxIdle);
        }
    }
}
