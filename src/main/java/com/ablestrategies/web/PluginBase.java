package com.ablestrategies.web;

import com.ablestrategies.web.resp.HttpResponse;
import com.ablestrategies.web.rqst.HttpRequest;

/**
 * A Plugin must extend this class.
 */
@SuppressWarnings("ALL") // Base class for processing plugins
public abstract class PluginBase {

    protected String name = null;
    protected String author = null;
    protected String version = null;

    protected final Preferences preferences;

    /**
     * Any derived class must implement this ctor and it must call this one.
     * @param preferences configuration settings.
     */
    public PluginBase(Preferences preferences) {
        this.preferences = preferences;
    }

    /**
     * Get the kind of HttpResponse, null if not of interest to this plugin
     * @param response for analysis
     * @return null unless it can be handled by this plugin
     */
    public abstract Class<? extends PluginBase> getKind(HttpRequest request);

    /**
     * Process the HttpRequest and create a corresponding HttpResponse
     * @param request
     * @return
     */
    public abstract HttpResponse handleRequest(HttpRequest request);

}
