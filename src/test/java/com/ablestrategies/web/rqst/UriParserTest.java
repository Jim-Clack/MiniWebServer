package com.ablestrategies.web.rqst;

import com.ablestrategies.web.Preferences;
import junit.framework.Assert;
import junit.framework.TestCase;

public class UriParserTest extends TestCase {

    public void testGetFilePath() {
        Preferences.getInstance().reset();
        String webroot = Preferences.getInstance().getRootPath();
        String path = UriParser.getFilePath("/xyz", false);
        String expected = webroot.replaceAll("\\\\", "/") + "/xyz";
        Assert.assertEquals(expected, path);
    }

    public void testGetQueryString() {
        String query = UriParser.queryString("/xyz:1234?q=73&yyy=999", "q", "777");
        Assert.assertEquals(query, "73");
        query = UriParser.queryString("/xyz:1234?q=abc&yyy=999", "yyy", "777");
        Assert.assertEquals(query, "999");
    }

    public void testGetQueryStringDefaulted() {
        String query = UriParser.queryString("/xyz:1234?q=73&yyy=999", "rst", "777");
        Assert.assertEquals(query, "777");
    }
}