package com.jc;

import junit.framework.Assert;
import junit.framework.TestCase;

public class HttpResponseXxxxTest extends TestCase {


    public void testGenerateContent() {
        String bufferWithQuery =
                "GET /index.html?qu=samp&qty=1 HTTP/1.1\n" +
                        "Host: localhost\n" +
                        "Content-Length: 32\n" +
                        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\n" +
                        "\n" +
                        "<html><body>Hello</body></html>\n";
        HttpRequestBase rq = HttpActionType.getHttpRequest(bufferWithQuery, null);
        HttpResponseBase rs = HttpActionType.getTypedResponse(rq, null);
        Assert.assertEquals(HttpResponseFile.class, rs.getClass());
    }

}