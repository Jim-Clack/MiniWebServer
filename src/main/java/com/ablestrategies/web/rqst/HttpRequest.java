package com.ablestrategies.web.rqst;

import com.ablestrategies.web.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for handling all kinds of HTTP requests. Accepts the raw message and
 * parses out the url, headers, and body.
 * ---------------------------------------------------------------------------
 * All of the HttpRequestXxxx classes extend this. They can be created by
 * calling their ctors or by cloning the state of another HttpRequestXxxx.
 */
public class HttpRequest extends HttpRequestPojo {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    /**
     * Ctor.
     * @param manager The server manager. May be null if cloneState() will be called afterwards.
     */
    public HttpRequest(ServerManager manager) {
        this.manager = manager;
    }

    /**
     * Populate this HttpRequestXxxx from another.
     * @param original to copy the state from. (into this one)
     */
    public void cloneState(HttpRequestPojo original) {
        this.manager = original.manager;
        this.headers = original.headers;
        this.body = original.body;
        this.errorCode = original.errorCode;
        this.method = original.method;
        this.uri = original.uri;
        this.version = original.version;
    }

    /**
     * Process the first line of the raw HTTP request.
     * @param line the raw data
     */
    protected void parseStatusLine(String line) {
        String[] tokens = line.split(" ");
        if(tokens.length < 3) {
            errorCode = RequestError.BAD_FIRST_LINE;
        } else {
            errorCode = RequestError.OK;
            method = tokens[0].trim();
            uri = tokens[1].trim();
            version = tokens[2].trim();
        }
        if(!method.equals("GET")) {
            errorCode = RequestError.ILLEGAL_METHOD;
        }
        if(!version.equals("HTTP/1.1")) {
            errorCode = RequestError.UNSUPPORTED_VERSION;
        }
        logger.debug("HttpRequest code={}, method={}, url={}, version={}", errorCode, method, uri, version);
    }

    /**
     * Process the headers from the raw data.
     * @param lines the raw data, split at endlines.
     * @return the line number of the last header line.
     */
    protected int parseHeaders(String[] lines) {
        int lineIndex;
        for(lineIndex = 1; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex].trim();
            if(line.isEmpty()) {
                break;
            }
            String[] fields = line.split("[:,]", 2);
            if(fields.length < 2) {
                logger.warn("Bad Header in HttpRequest: {}", line);
                errorCode = RequestError.BAD_HEADER;
            }
            headers.put(fields[0].trim().toLowerCase(), fields[1].trim());
            logger.trace("HttpRequest header key={}, value={}", fields[0].trim().toLowerCase(), fields[1].trim());
        }
        return lineIndex;
    }

    /**
     * Process the HTTP body - often empty.
     * @param lineIndex Line number of the start of the body.
     * @param lines the raw data, split at endlines.
     */
    protected void parseBody(int lineIndex, String[] lines) {
        for(; lineIndex < lines.length; lineIndex++) {
            body.append(lines[lineIndex]).append("\n");
        }
    }

}
