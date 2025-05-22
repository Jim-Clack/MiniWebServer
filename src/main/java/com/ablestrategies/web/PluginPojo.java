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
    String pathToPlugin = null;        // i.e. ../plugins/phpfilter.jar
    String hooks = null;               // i.e. request, htmlFiles, allFiles
    String mimeRegex = null;           // i.e. application/php
    String suffixRegex = null;         // i.e. \.php
    String methodsRegex = null;        // i.e. (GET)|(PUT)
    Map<String,String> responseHeaders = null;    // Header inserts/updates
    Map<String,String> responseHtmlSwaps = null;  // Regex substitutions
}
