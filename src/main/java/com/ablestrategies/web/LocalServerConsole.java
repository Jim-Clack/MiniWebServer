package com.ablestrategies.web;

import java.util.Scanner;

/**
 * This is a keyboard-based management console for the server.
 * ---------------------------------------------------------------------------
 * Commands:
 *    [Enter]     Help, show commands
 *    A [Enter]   Show IP Address and port of this server
 *    C [Enter]   Show all Connections
 *    T [Enter]   Show all Threads
 *    K [Enter]   Kill connections that have been inactive for 60 seconds or more
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
                System.out.println("Select: [C]onnections, [Sessions], [T]hreads, [K]illIdle60, [A]ddress, [Q]uit");
                input = scanner.nextLine().trim().toUpperCase();
            } while(input.isEmpty());
            switch(input.charAt(0)) {
                case 'X': case 'Q':
                    running = false;
                    break;
                case 'K':
                    System.out.println("Number of connections killed: " + manager.killIdleConnections(60) + "\n");
                    break;
                case 'A':
                    System.out.println(manager.getConsole().listIpAddresses());
                    break;
                case 'C':
                    System.out.println(manager.getConsole().listAllConnections());
                    break;
                case 'S':
                    System.out.println(manager.getConsole().listAllSessions());
                    break;
                case 'T':
                    System.out.println(manager.getConsole().listAllThreads());
                    break;
                default:
                    System.out.println("Invalid command\n");
                    break;
            }
        }
    }

}
