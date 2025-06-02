package com.ablestrategies.web.rqst;

import com.ablestrategies.web.HttpActionType;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Arrays;

public class HttpRequestXxxxTest extends TestCase {

    private final String bufferWithQuery =
            "GET /index.html?qu=samp&qty=1 HTTP/1.1\n" +
            "Host: localhost\n" +
            "Content-Length: 32\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\n" +
            "\n" +
            "<html><body>Hello</body></html>\n";

    public void testGetRequestKind() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        HttpActionType.RequestType rk = HttpActionType.getRequestKind(rq);
        Assert.assertEquals(HttpActionType.RequestType.RQ_FILE_GET, rk);
    }

    public void testGetFilePath() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        String fp = rq.getFilePath(true);
        Assert.assertEquals("C:/Users/jimcl/webroot/index.html", fp);
    }

    public void testGetQueryValue() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        String qv = rq.getQueryValue("qu", "???");
        Assert.assertEquals("samp", qv);
        qv = rq.getQueryValue("qty", "???");
        Assert.assertEquals("1", qv);
    }

    public void testGetHeader() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        String[] hosts = rq.getHeaderValues("Host");
        Assert.assertEquals(1, hosts.length);
        Assert.assertEquals("localhost", hosts[0]);
    }

    public void testGetBody() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        String bod = rq.getBody();
        Assert.assertEquals("\n<html><body>Hello</body></html>\n", bod);
    }

    public void testGetMethod() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        String mth = rq.getMethod();
        Assert.assertEquals("GET", mth);
    }

    public void testGetErrorCode() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        RequestError ec = rq.getErrorCode();
        assertEquals(RequestError.OK, ec);
    }

    public void testGetUrl() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        String uri = rq.getUri();
        Assert.assertEquals("/index.html?qu=samp&qty=1", uri);
    }

    public void testGetVersion() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        String ver = rq.getVersion();
        Assert.assertEquals("HTTP/1.1", ver);
    }

    public void testErrorCodeNoBody() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        String[] lines2 = Arrays.copyOfRange(lines, 0, 5);
        rq.parseStatusLine(lines2[0]);
        int lineIndex = rq.parseHeaders(lines2);
        rq.parseBody(lineIndex, lines2);
        RequestError ec = rq.getErrorCode();
        assertEquals(RequestError.OK, ec);
    }

    public void testErrorCodeBadVersion() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        lines[0] = "GET /index.html?qu=samp&qty=1 HTTP/2.0";
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        RequestError ec = rq.getErrorCode();
        assertEquals(RequestError.UNSUPPORTED_VERSION, ec);
    }

    public void testErrorCodeBadMethod() {
        HttpRequestBase rq = new HttpRequestBase(null);
        String[] lines = bufferWithQuery.split("\n");
        lines[0] = "BLUMP /index.html?qu=samp&qty=1 HTTP/1.1";
        rq.parseStatusLine(lines[0]);
        int lineIndex = rq.parseHeaders(lines);
        rq.parseBody(lineIndex, lines);
        RequestError ec = rq.getErrorCode();
        assertEquals(RequestError.ILLEGAL_METHOD, ec);
    }
}