package com.jc;

import java.io.File;

/**
 * Simple settings class.
 * ---------------------------------------------------------------------------
 * To enable HTTPS, acquire a certificate from a certificate authority based
 * on your deployed environment, and install it via the following properties
 * in the reset() method below.
 *  javax.net.ssl.keyStore
 *  javax.net.ssl.keyStorePassword
 * <a href="https://docs.oracle.com/javadb/10.10.1.2/adminguide/cadminsslserver.html">Server Properties</a>
 * You may want to set some of these properties as well.
 *  javax.net.ssl.trustStore
 *  javax.net.ssl.trustStorePassword
 *  javax.net.ssl.keyStoreType
 *  javax.net.ssl.trustStoreType
 *  javax.net.debug
 *  javax.net.ssl.sessionCacheSize
 *  javax.net.ssl.sessionTimeout
 *  jdk.tls.server.protocols
 *  jdk.tls.disabledAlgorithms
 *  https.protocols
 *  https.proxyHost
 *  https.proxyPort
 */
public class Preferences {

    private static Preferences instance = null;
    private int portNumber = 12345;
    private int sslPortNumber = 0; // 0 = SSL disabled
    private String rootPath;

    public static Preferences getInstance() {
        if(instance == null) {
            instance = new Preferences();
        }
        return instance;
    }

    private Preferences() {
        reset();
        File rootPathFile = new File(this.rootPath);
        if(!rootPathFile.exists()) {
            throw new RuntimeException("Web root path does not exist: " + rootPathFile);
        }
    }

    public void reset() {
        // System.setProperty("javax.net.ssl.keyStore", "/somepath/keystore.key");
        // System.setProperty("javax.net.ssl.keyStorePassword", "T@Zz932105");
        String userHome = System.getProperty("user.home");
        rootPath = userHome + File.separator + "webroot";
        this.portNumber = Integer.parseInt(
                System.getProperty("MiniWebServer.portNumber", ""+this.portNumber));
        this.sslPortNumber = Integer.parseInt(
                System.getProperty("MiniWebServer.sslPortNumber", ""+this.sslPortNumber));
        this.rootPath =
                System.getProperty("MiniWebServer.rootPath", this.rootPath);
    }

    public int getMaxHistory() {
        return 12; // not configurable for now
    }

    public int getMaxIdleSeconds() {
        return 300; // not configurable for now
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getSslPortNumber() {
        return sslPortNumber;
    }

    public void setSslPortNumber(int sslPortNumber) {
        this.sslPortNumber = sslPortNumber;
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

}
