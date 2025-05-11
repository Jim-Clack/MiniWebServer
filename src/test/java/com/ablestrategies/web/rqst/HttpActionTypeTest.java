package com.ablestrategies.web.rqst;

import com.ablestrategies.web.HttpActionType;
import com.ablestrategies.web.resp.HttpResponseBase;
import com.ablestrategies.web.resp.HttpResponseFile;
import junit.framework.TestCase;

public class HttpActionTypeTest extends TestCase {

    private final String bufferWithQuery =
            "GET /index.html?qu=samp&qty=1 HTTP/1.1\n" +
                    "Host: localhost\n" +
                    "Content-Length: 32\n" +
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36\n" +
                    "\n" +
                    "<html><body>Hello</body></html>\n";

    public void testGetHttpRequestFile() {
        HttpRequestBase rq = HttpActionType.getHttpRequest(bufferWithQuery, null);
        assertEquals(HttpRequestFile.class, rq.getClass());
    }

    public void testGetHttpRequestSoap() {
        String buffer2 = bufferWithQuery.replace("Host", "Accept: application/xml\nHost");
        HttpRequestBase rq = HttpActionType.getHttpRequest(buffer2, null);
        assertEquals(HttpRequestSoap.class, rq.getClass());
    }

    public void testGetHttpRequestJson() {
        String buffer2 = bufferWithQuery.replace("Host", "Accept: application/json\nHost");
        HttpRequestBase rq = HttpActionType.getHttpRequest(buffer2, null);
        assertEquals(HttpRequestJson.class, rq.getClass());
    }

    public void testGetTypedRequest() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        HttpRequestBase rb = HttpActionType.getTypedRequest(rq);
        assertEquals(HttpRequestFile.class, rb.getClass());
    }

    public void testGetTypedResponse() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        HttpResponseBase rs = HttpActionType.getTypedResponse(rq, rq.manager);
        assertEquals(HttpResponseFile.class, rs.getClass());
    }
}