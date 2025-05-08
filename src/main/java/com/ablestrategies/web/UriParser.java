package com.ablestrategies.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Static class for parsing URIs, as in an HTTP request.
 *   <a href="https://docs.oracle.com/javase/8/docs/api/java/net/URI.html">Oracle URI Docs</a>
 */
public class UriParser {

    /** Logger slf4j. */
    private static final Logger logger = LoggerFactory.getLogger(UriParser.class);

    /**
     * ONE REALLY UGLY METHOD...
     * We are interested in the path, the query, and the path parent (tenant path).
     * @param requestPath From the HTTP request.
     * @param canDefault True to insert "index.html" (or other default file) if not specified.
     * @return the absolute path to the file, null otherwise.
     */
    public static String getFilePath(String requestPath, boolean canDefault) {
        String[] defaultFiles = {"index.html", "index.htm", "default.htm", "default.html"};
        try {
            if(requestPath.startsWith("/")) {
                requestPath = requestPath.substring(1);
            }
            URI requestUri = new URI(new URI(requestPath).getPath());
            String rootPath = new File(
                    Preferences.getInstance().getRootPath()).getAbsolutePath().replaceAll("\\\\", "/") + "/";
            URI rootUri = new URI("file:///" + rootPath); // authority must have 3 slashes
            URI resolvedUri = rootUri.resolve(requestUri);
            if(canDefault && resolvedUri.getPath().endsWith("/")) {
                File testFile = new File(resolvedUri.getPath());
                if(testFile.exists() && testFile.isFile()) {
                    return testFile.getAbsolutePath();
                }
                for(String defaultFile : defaultFiles) {
                    logger.trace("Testing existence of: {}", resolvedUri.resolve(defaultFile));
                    testFile = new File(resolvedUri.resolve(defaultFile));
                    if(testFile.exists()) {
                        return testFile.getAbsolutePath();
                    }
                }
                return null;
            }
            logger.trace("ResolvedUri: {}", resolvedUri);
            String path = resolvedUri.getPath();
            if(path.startsWith("/") && path.charAt(2) == ':') { // for Windows file paths
                return path.substring(1);
            }
            return path;
        } catch (URISyntaxException e) {
            return null; // ???
        }
    }

    public static String queryString(String requestPath, String key, String defaultValue) {
        String query;
        try {
            query = String.valueOf(new URI(requestPath).getQuery()); // may be null
            if(query == null) {
                return defaultValue;
            }
        } catch (URISyntaxException e) {
            return defaultValue;
        }
        int index = query.indexOf(key + "=") + 1;
        if(index <= 0) {
            return defaultValue;
        }
        int pastValue = query.indexOf("&", index + key.length());
        if(pastValue == -1) {
            pastValue = query.length();
        }
        return query.substring(index + key.length(), pastValue);

    }
}
