package com.jc;

import java.util.Scanner;

public class LocalServerConsole {

    private final ServerManager manager;
    private final ListenerThread listener;

    public LocalServerConsole(ServerManager manager, ListenerThread listener) {
        this.manager = manager;
        this.listener = listener;
    }

    public void interact() {
        System.out.println("Web Server is up and running...");
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        String input;
        while(running) {
            do {
                System.out.print("[S]essions, [K]illIdle60, [A]ddress, [Q]uit >> ");
                input = scanner.nextLine().trim().toUpperCase();
            } while(input.isEmpty());
            switch(input.charAt(0)) {
                case 'X': case 'Q':
                    running = false;
                    break;
                case 'K':
                    System.out.println("\nNumber of sessions killed: " + manager.killIdleSessions(60) + "\n");
                    break;
                case 'A': case 'P':
                    System.out.println("\nServer address and port: " + listener.getAddressAndPort() + "\n");
                    break;
                case 'S':
                    System.out.println(manager.listAllSessions());
                    break;
                default:
                    System.out.println("\nInvalid command\n");
                    break;
            }
        }
    }

}
