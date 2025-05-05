package com.jc;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UriParserTest extends TestCase {

    public void testGetFilePath() {
        String webroot = Preferences.getInstance().getRootPath();
        String path = UriParser.getFilePath("xyz", false);
        Assert.assertEquals(webroot.replaceAll("\\\\", "/") + "/xyz", path);
    }

}