package com.jc;

import java.util.Scanner;

public class ServerConsole {

    private final ServerManager manager;
    private final ListenerThread listener;

    public ServerConsole(ServerManager manager, ListenerThread listener) {
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
                System.out.print("ws>");
                input = scanner.nextLine().trim().toUpperCase();
            } while(input.isEmpty());
            switch(input.charAt(0)) {
                case 'X': case 'Q':
                    running = false;
                    break;
                case 'H': case '?':
                    System.out.println("[H]elp, [S]essions, [K]illIdle, [A]ddress, [Q]uit");
                    break;
                case 'K':
                    System.out.println("Number of sessions killed: " + manager.killIdleSessions(60));
                    break;
                case 'A': case 'P':
                    System.out.println("Server address and port: " + listener.getAddressAndPort());
                    break;
                case 'S':
                    System.out.println("Number of Alive sessions: " + manager.listAllSessions());
                    break;
                default:
                    System.out.println("Invalid command");
                    break;
            }
        }
    }

}
