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
        int connectionMaxIdle = Preferences.getInstance().getConnectionMaxIdleSeconds();
        int sessionMaxIdle = Preferences.getInstance().getSessionMaxIdleSeconds();
        setName("IdleLoopThread-Max" + sessionMaxIdle);
        int priority = Math.max(Thread.MIN_PRIORITY, Thread.currentThread().getPriority() - 1);
        Thread.currentThread().setPriority(priority);
        while(!isInterrupted()) {
            try {
                sleep(10000); // check every 10 seconds
            } catch (InterruptedException e) {
                // daemon, ignore interrupts
            }
            manager.killIdleConnections(connectionMaxIdle);
            manager.discardIdleSessions(sessionMaxIdle);
        }
    }
}
