package com.jc;

import java.io.File;

/**
 * Simple settings class just for testing.
 * ---------------------------------------------------------------------------
 * To enable HTTPS, acquire a certificate from a certificate authority based
 * on your deployed environment, and install it via the following properties:
 *  javax.net.ssl.keyStore
 *  javax.net.ssl.keyStorePassword
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
public class Configuration {

    private int portNumber = 12345;
    private int sslPortNumber = 0; // 0 = SSL disabled
    private String rootPath = ".";

    public Configuration() {
        // System.setProperty("javax.net.ssl.keyStore", "/somepath/kstore.key");
        // System.setProperty("javax.net.ssl.keyStorePassword", "T@Zz932105");
        // System.setProperty("javax.net.ssl.trustStore", "/somepath/tstore.key");
        // System.setProperty("javax.net.ssl.trustStorePassword", "T@Zz932105");
        String userHome = System.getProperty("user.home");
        rootPath = userHome + File.separator + "webroot";
        this.portNumber = Integer.parseInt(
                System.getProperty("MiniWebServer.portNumber", ""+this.portNumber));
        this.sslPortNumber = Integer.parseInt(
                System.getProperty("MiniWebServer.sslPortNumber", ""+this.sslPortNumber));
        this.rootPath =
                System.getProperty("MiniWebServer.rootPath", this.rootPath);
        File rootPathFile = new File(this.rootPath);
        if(!rootPathFile.exists()) {
            throw new RuntimeException("Web root path does not exist: " + rootPathFile.toString());
        }
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
