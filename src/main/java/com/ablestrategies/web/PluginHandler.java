package com.ablestrategies.web;

@SuppressWarnings("ALL") // Until we flesh this class out
public class PluginHandler {

    private PluginHandler instance = null;

    /**
     * Need to support getProperty("mws-plugins", "comma-delimited-jars");
     * - Each such jar must have a class named PlugInPojo to configure it
     * - Intercept regex: fileType, requestType, mimeType, acceptType
     * - hook: HttpResponse response = Plugin.handle(HttpRequest request);
     */

    private PluginHandler() {
        // Not yet coded
    }

    public PluginHandler getInstance() {
        if(instance == null) {
            instance = new PluginHandler();
        }
        return instance;
    }

}
