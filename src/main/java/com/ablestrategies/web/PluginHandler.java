package com.ablestrategies.web;

@SuppressWarnings("ALL") // Until we flesh this class out
public class PluginHandler {

    private PluginHandler instance = null;

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
