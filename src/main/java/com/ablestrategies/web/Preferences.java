package com.ablestrategies.web;

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

    public static String version = "0.0.9";

    private static Preferences instance = null;
    private int portNumber = 12345;
    private int sslPortNumber = 0; // 0 = SSL disabled
    private String rootPath;

    /**
     * Get the singleton.
     * @return The only instance.
     */
    public static Preferences getInstance() {
        if(instance == null) {
            instance = new Preferences();
        }
        return instance;
    }

    /**
     * [private - singleton] Ctor.
     */
    @SuppressWarnings("all")
    private Preferences() {
        reset();
        File rootPathFile = new File(this.rootPath);
        if(!rootPathFile.exists()) {
            // throw new RuntimeException("Web root path does not exist: " + rootPathFile);
            rootPathFile.mkdirs();
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

    /**
     * Read in PluginPojo JSON files.
     * @return List of PluginPojos.
     */
    @SuppressWarnings("ALL") // Until we flesh this method out
    public String[] getPluginClassNames() {
        String[] plugins = new String[0];
        String pluginPojos = System.getProperty("MiniWebServer.plugins", null);
        if(pluginPojos == null) {
            return plugins;
        }
        plugins = pluginPojos.split(",");
        for(int i = 0; i < plugins.length; i++) {
            plugins[i] = plugins[i].trim();
        }
        return plugins;
    }

    /**
     * How many requests/responses to keep track of per connection.
     * @return Max history records.
     */
    public int getMaxHistory() {
        return 12; // not configurable for now
    }

    /**
     * How long to keep connections open.
     * @return Seconds.
     */
    public int getConnectionMaxIdleSeconds() {
        return 60 * 5; // not configurable for now
    }

    /**
     * How long to keep sessions open.
     * @return Seconds.
     */
    public int getSessionMaxIdleSeconds() {
        return 60 * 60 * 24 * 2; // not configurable for now
    }

    /**
     * HTTP port number.
     * @return IP port.
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * HTTP port number.
     * @param portNumber IP port.
     */
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * HTTPS port number.
     * @return IP port.
     */
    public int getSslPortNumber() {
        return sslPortNumber;
    }

    /**
     * HTTPS port number.
     * @param sslPortNumber IP port.
     */
    public void setSslPortNumber(int sslPortNumber) {
        this.sslPortNumber = sslPortNumber;
    }

    /**
     * Path to web files, HTML, PNG, JS, CSS, etc.
     * @return Absolute path.
     */
    public String getRootPath() {
        return this.rootPath;
    }

    /**
     * Path to web files, HTML, PNG, JS, CSS, etc.
     * @param rootPath Absolute path.
     */
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

}
