package com.jc;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ServerManager {

    private final List<SessionThread> sessions = new LinkedList<>();

    public void createSession(Socket socket) {
        SessionThread sessionThread;
        try {
            sessionThread = new SessionThread(socket);
            sessionThread.start();
            sessions.add(sessionThread);
        } catch (IOException e) {
            Logger.INFO("acceptSession: " + e.getMessage());
        }
    }

    public int discardDeadSessions() {
        int discardCount = 0;
        List<SessionThread> deadSessions = new LinkedList<>();
        for(SessionThread sessionThread : sessions) {
            if(!sessionThread.isAlive()) {
                deadSessions.add(sessionThread); // remove it and any dead threads
            }
        }
        for(SessionThread deadThread : deadSessions) {
            sessions.remove(deadThread);
            ++discardCount;
        }
        return discardCount;
    }

    public int killThreads(long maxIdleSeconds) {
        int killCount = 0;
        for(SessionThread sessionThread : sessions) {
            if(sessionThread.beenIdleForHowLong() >= maxIdleSeconds) {
                sessionThread.interrupt();
                ++killCount;
            }
        }
        discardDeadSessions();
        return killCount;
    }

    public int listThreads() {
        int threadCount = 0;
        for(SessionThread sessionThread : sessions) {
            System.out.println(sessionThread.getThreadName());
            System.out.println("  Alive:   " + sessionThread.isAlive());
            System.out.println("  Idle:    " + sessionThread.beenIdleForHowLong());
            System.out.println("  Client:  " + sessionThread.getAddressAndPort());
            threadCount++;
        }
        discardDeadSessions();
        return threadCount;
    }

    public void console(ListenerThread listener) {
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
                    System.out.println("Help, Sessions, KillIdle, Quit, Address");
                    break;
                case 'K':
                    System.out.println("Number of sessions killed: " + killThreads(60));
                    break;
                case 'A': case 'P':
                    System.out.println(listener.getAddressAndPort());
                    break;
                case 'S':
                    System.out.println("Number of sessions: " + listThreads());
                    break;
                default:
                    System.out.println("Invalid command");
                    break;
            }
        }
    }

}
