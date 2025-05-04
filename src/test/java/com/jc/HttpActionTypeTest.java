package com.jc;

import junit.framework.Assert;
import junit.framework.TestCase;

public class HttpActionTypeTest extends TestCase {

    private final String bufferWithQuery =
            "GET /index.html?qu=samp&qty=1 HTTP/1.1\n" +
                    "Host: localhost\n" +
                    "Content-Length: 32\n" +
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\n" +
                    "\n" +
                    "<html><body>Hello</body></html>\n";

    public void testGetHttpRequestFile() {
        HttpRequestBase rq = HttpActionType.getHttpRequest(bufferWithQuery, null);
        Assert.assertEquals(HttpRequestFile.class, rq.getClass());
    }

    public void testGetHttpRequestSoap() {
        String buffer2 = bufferWithQuery.replace("Host", "Content-Type: text/xml\nHost");
        HttpRequestBase rq = HttpActionType.getHttpRequest(buffer2, null);
        Assert.assertEquals(HttpRequestSoap.class, rq.getClass());
    }

    public void testGetHttpRequestJson() {
        String buffer2 = bufferWithQuery.replace("Host", "Content-Type: application/json\nHost");
        HttpRequestBase rq = HttpActionType.getHttpRequest(buffer2, null);
        Assert.assertEquals(HttpRequestJson.class, rq.getClass());
    }

    public void testGetTypedRequest() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        HttpRequestBase rb = HttpActionType.getTypedRequest(rq);
        Assert.assertEquals(HttpRequestFile.class, rb.getClass());
    }

    public void testGetTypedResponse() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        HttpResponseBase rs = HttpActionType.getTypedResponse(rq, rq.manager);
        Assert.assertEquals(HttpResponseFile.class, rs.getClass());
    }
}