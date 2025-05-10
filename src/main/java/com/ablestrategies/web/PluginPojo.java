package com.ablestrategies.web;

import java.util.Map;

/**
 * Generally, this is read from a local JSON file.
 */
@SuppressWarnings("ALL") // Until we flesh this class out
public class PluginPojo {
    String name = null;                // i.e. PHP
    String description = null;
    String author = null;
    String version = null;
    String pluginType = null;          // htmlFilter, hooksApi, appServer
    String connection = null;          // shell, app, class, jar, url
    String mimeRegex = null;           // i.e. Accept: application/php
    String suffixRegex = null;         // i.e. \.php
    String methodTriggers = null;      // i.e. (GET)|(PUT)
    String pathToPlugin = null;        // i.e. ../plugins/phpfilter.class
    String hooks = null;               // i.e. request, htmlFiles, allFiles
    Map<String,String> rqstHeaderSwaps = null;  // Substitutions
    Map<String,String> respHeaderSwaps = null;  // Substitutions
    Map<String,String> respHtmlSwaps = null;    // Substitutions
}
