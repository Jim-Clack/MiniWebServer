package com.jc;

import java.io.File;

/**
 * Simple class just for testing.
 */
public class Configuration {

    private int portNumber = 12345;
    private int sslPortNumber = 0; // 0 = SSL disabled
    private String rootPath = ".";

    public Configuration() {
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
