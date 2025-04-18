package com.jc;

import java.io.File;

/**
 * Simple class just for testing
 */
public class Configuration implements IConfiguration {

    private int portNumber = 12345;
    private String rootPath = "C:/Users/jimcl/webroot";

    public Configuration() {
        this.portNumber = Integer.parseInt(
                System.getProperty("MiniWebServer.portNumber", ""+this.portNumber));
        this.rootPath =
                System.getProperty("MiniWebServer.rootPath", this.rootPath);
    }

    @SuppressWarnings("all")
    public int getPortNumber() {
        File rootPathFile = new File(this.rootPath);
        rootPathFile.mkdirs();
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
