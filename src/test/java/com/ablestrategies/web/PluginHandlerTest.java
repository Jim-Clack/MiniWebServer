package com.ablestrategies.web;

import com.ablestrategies.web.resp.HttpResponse;
import com.ablestrategies.web.resp.HttpResponseExample;
import com.ablestrategies.web.rqst.HttpRequest;
import junit.framework.TestCase;

public class PluginHandlerTest extends TestCase {

    /**
     * See ExamplePlugin and HttpResponseExample
     */
    public void testExamplePlugin() {
        String oldValue = System.getProperty("MiniWebServer.plugins", "");
        System.setProperty("MiniWebServer.plugins", "com.ablestrategies.web.ExamplePlugin");
        PluginHandler.getInstance().loadPlugins(); // reload after changing property
        String bufferWithQuery =
                "GET /index.example HTTP/1.1\n" +
                        "Host: localhost\n" +
                        "Content-Length: 32\n" +
                        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\n" +
                        "\n" +
                        "nothing here\n";
        HttpRequest rq = HttpActionType.getHttpRequest(bufferWithQuery, null);
        HttpResponse rs = HttpActionType.getTypedResponse(rq, null);
        assertEquals(HttpResponseExample.class, rs.getClass());
        assertEquals("Example", rs.getDescription());
        assertTrue(new String(rs.getContent()).contains("Body"));
        System.setProperty("MiniWebServer.plugins", oldValue);
        PluginHandler.getInstance().loadPlugins(); // reload after changing property
    }

}