package com.ablestrategies.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.security.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;

/**
 * Simple listener for a web server.
 * HTTPS, per Gemini:
 * Before writing the Java code, you need a Java Keystore (JKS or PKCS12 format) file
 * that holds your server's public certificate and private key. The keytool utility,
 * included with the JDK, can be used for this purpose:
 *   keytool -genkeypair -alias myServerKey -keyalg RSA -keysize 2048 -storetype
 *              PKCS12 -keystore keystore.p12 -validity 3650
 * This command generates a key pair and stores it in a file named keystore.p12.
 * Remember the passwords you set during this process, as you will need them in your
 * Java code.
 */
public class ListenerThread extends Thread {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(ListenerThread.class);

    /** Top level server manager. */
    private final ServerManager manager;

    /** The server socket that we listen on. */
    private ServerSocket serverSocket = null;

    /** HTTPS or HTTPS? */
    private final String protocol;

    /** IP Port number. */
    private final int portNumber;

    /**
     * Ctor.
     * @param manager Top level server manager.
     * @param protocol HTTP or HTTPS.
     * @throws IOException Fatal problem starting server/listener.
     */
    public ListenerThread(String protocol, ServerManager manager) throws IOException {
        this.protocol = protocol.trim().toUpperCase();
        this.manager = manager;
        InetAddress address = InetAddress.getByName("localhost");
        if(protocol.equals("HTTPS")) {
            this.portNumber = Preferences.getInstance().getSslPortNumber();
            SSLContext sslContext = getSslContext();
            if(sslContext == null) {
                logger.error("SSLContext FAILURE - HTTPS Listener NOT active!\n" +
                        "Is the keystore.p12 installed and use proper passwords?");
                return;
            }
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            this.serverSocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(portNumber);
        } else {
            this.portNumber = Preferences.getInstance().getPortNumber();
            this.serverSocket = ServerSocketFactory.getDefault().
                    createServerSocket(portNumber, 100, address);
        }
        this.setDaemon(true);
    }

    /**
     * Run loop for thread.
     */
    public void run() {
        this.setName("ListenerThread-" + protocol + portNumber);
        System.out.println(protocol + " listening on port: " + this.portNumber);
        while (!isInterrupted()) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                logger.debug("Listener accept() problem", e);
            }
            if(socket != null) {
                manager.createConnection(protocol, socket);
            }
        }
    }

    /**
     * Getter for a string containing the local connection info.
     * @return The IP address/domain/port.
     */
    public String getAddressAndPort() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress() + ":" + portNumber;
        } catch (UnknownHostException e) {
            // default below
        }
        return serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort();
    }

    /**
     * Initialize for HTTPS (SSLContext supports TLS)
     * @return SSLContext, null on error
     */
    private SSLContext getSslContext() {
        String KEYSTORE_PATH = "keystore.p12";
        String KEYSTORE_PASSWORD = "keystore_password";
        String KEY_PASSWORD = "key_password";
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] keystorePasswordChars = KEYSTORE_PASSWORD.toCharArray();
            try (FileInputStream fis = new FileInputStream(KEYSTORE_PATH)) {
                keyStore.load(fis, keystorePasswordChars);
            }
            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            char[] keyPasswordChars = KEY_PASSWORD.toCharArray();
            keyManagerFactory.init(keyStore, keyPasswordChars);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
            return sslContext;
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException |
                 CertificateException | UnrecoverableKeyException | KeyManagementException e) {
            logger.warn("Cannot initialize SSL/TLS " + e.getMessage(), e);
            return null;
        }
    }
}

