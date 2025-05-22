package com.ablestrategies.web;

import java.util.Scanner;

/**
 * This is a keyboard-based management console for the server.
 * ---------------------------------------------------------------------------
 * Commands:
 *    [Enter]     Help, show commands
 *    C [Enter]   List all Connections
 *    S [Enter]   List all Sessions
 *    T [Enter]   List all Threads
 *    P [Enter]   List properties and preferences
 *    K [Enter]   Kill connections/sessions that have been inactive for 60 seconds
 *    Q [Enter]   Quit - shut down the server
 */
public class LocalServerConsole {

    /** The top-level object that knows about all connections. */
    private final ServerManager manager;

    /**
     * Ctor.
     * @param manager The top-level object that knows about all connections.
     */
    public LocalServerConsole(ServerManager manager) {
        this.manager = manager;
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
                System.out.println(manager.getConsole().getMenu());
                input = scanner.nextLine().trim().toUpperCase();
            } while(input.isEmpty());
            switch(input.charAt(0)) {
                case 'C':
                    System.out.println(manager.getConsole().listAllConnections());
                    break;
                case 'S':
                    System.out.println(manager.getConsole().listAllSessions());
                    break;
                case 'T':
                    System.out.println(manager.getConsole().listAllThreads());
                    break;
                case 'P':
                    System.out.println(manager.getConsole().listProperties());
                    break;
                case 'K':
                    System.out.println(manager.getConsole().killIdleClients());
                    break;
                case 'X': case 'Q':
                    running = false;
                    break;
                default:
                    System.out.println("Invalid command\n");
                    break;
            }
        }
    }

}
