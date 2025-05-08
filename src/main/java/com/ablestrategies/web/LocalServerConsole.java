package com.ablestrategies.web;

import java.util.Scanner;

/**
 * This is a keyboard-based management console for the server.
 * ---------------------------------------------------------------------------
 * Commands:
 *    [Enter]     Help, show commands
 *    A [Enter]   Show IP Address and port of this server
 *    S [Enter]   Show all sessions
 *    T [Enter]   Show all threads
 *    K [Enter]   Kill sessions that have been inactive for 60 seconds or more
 *    Q [Enter]   Quit - shut down the server
 */
public class LocalServerConsole {

    /** The top-level object that knows about all sessions. */
    private final ServerManager manager;

    /** The server's listener thread. */
    private final ListenerThread listener;

    /**
     * Ctor.
     * @param manager The top-level object that knows about all sessions.
     * @param listener The server's listener thread.
     */
    public LocalServerConsole(ServerManager manager, ListenerThread listener) {
        this.manager = manager;
        this.listener = listener;
    }

    /**
     * User interaction loop. Menu-based query text-UI.
     */
    public void interact() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        String input;
        while(running) {
            do {
                System.out.println("Select: [S]essions, [T]hreads, [K]illIdle60, [A]ddress, [Q]uit");
                input = scanner.nextLine().trim().toUpperCase();
            } while(input.isEmpty());
            switch(input.charAt(0)) {
                case 'X': case 'Q':
                    running = false;
                    break;
                case 'K':
                    System.out.println("Number of sessions killed: " + manager.killIdleSessions(60) + "\n");
                    break;
                case 'A': case 'P':
                    System.out.println("Server address and port: " + listener.getAddressAndPort() + "\n");
                    break;
                case 'S':
                    System.out.println(manager.listAllSessions());
                    break;
                case 'T':
                    System.out.println(manager.listAllThreads());
                    break;
                default:
                    System.out.println("Invalid command\n");
                    break;
            }
        }
    }

}
