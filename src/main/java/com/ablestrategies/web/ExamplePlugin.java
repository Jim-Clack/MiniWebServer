package com.ablestrategies.web;

import com.ablestrategies.web.resp.HttpResponse;
import com.ablestrategies.web.resp.HttpResponseExample;
import com.ablestrategies.web.rqst.HttpRequest;

public class ExamplePlugin extends PluginBase {

    public ExamplePlugin(Preferences preferences) {
        super(preferences);
    }

    /**
     * Get the kind of HttpResponse, null if not of interest to this plugin
     * @param request@return null unless it can be handled by this plugin
     */
    @Override
    public Class<? extends PluginBase> getKind(HttpRequest request) {
        if(request.getFilePath(false).endsWith(".example")) {
            return ExamplePlugin.class;
        }
        return null;
    }

    /**
     * Process the HttpRequest and create a corresponding HttpResponse
     * @param request incoming http request
     * @return response
     */
    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        return new HttpResponseExample();
    }
}
