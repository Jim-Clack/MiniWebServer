package com.ablestrategies.web;

public class PluginHandler {

    private PluginHandler instance = null;

    private PluginHandler() {
        // TODO
    }

    public PluginHandler getInstance() {
        if(instance == null) {
            instance = new PluginHandler();
        }
        return instance;
    }

}
