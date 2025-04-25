package com.jc;

import java.io.File;

/**
 * Simple class just for testing
 */
public class Configuration {

    private int portNumber = 12345;
    private String rootPath = ".";

    public Configuration() {
        String userHome = System.getProperty("user.home");
        rootPath = userHome + File.separator + "webroot";
        this.portNumber = Integer.parseInt(
                System.getProperty("MiniWebServer.portNumber", ""+this.portNumber));
        this.rootPath =
                System.getProperty("MiniWebServer.rootPath", this.rootPath);
    }

    public int getPortNumber() {
        File rootPathFile = new File(this.rootPath);
        if(!rootPathFile.exists()) {
            throw new RuntimeException("Web root path does not exist: " + rootPathFile.toString());
        }
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

}
