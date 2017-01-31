package com.anthonynsimon.url;

import org.junit.Assert;
import org.junit.Test;

public class URLBuilderTest {

    @Test
    public void testEmptyUrl() {
        URL url = new URLBuilder()
                .build();
        Assert.assertEquals("", url.toString());
    }

    @Test
    public void testSetFields() {
        URL url0 = new URLBuilder()
                .setScheme("http")
                .setHost("example.com")
                .build();

        Assert.assertEquals("http://example.com", url0.toString());

        URL url1 = new URLBuilder()
                .setScheme("https")
                .setHost("example.com")
                .setUsername("user")
                .setPassword("secret")
                .build();

        Assert.assertEquals("https://user:secret@example.com", url1.toString());

        URL url2 = new URLBuilder()
                .setScheme("https")
                .setHost("192.168.1.1:443")
                .setUsername("user")
                .setPassword("secret")
                .build();

        Assert.assertEquals("https://user:secret@192.168.1.1:443", url2.toString());

        URL url3 = new URLBuilder()
                .setFragment("testingTheFragment")
                .setScheme("https")
                .setHost("192.168.1.1:8080")
                .setUsername("user")
                .setPassword("secret")
                .build();

        Assert.assertEquals("https://user:secret@192.168.1.1:8080#testingTheFragment", url3.toString());

        URL url4 = new URLBuilder()
                .setFragment("testingTheFragment")
                .setScheme("https")
                .setHost("192.168.1.1:8080")
                .setUsername("user")
                .setPassword("secret")
                .setQuery("key=value&something=else")
                .build();

        Assert.assertEquals("https://user:secret@192.168.1.1:8080?key=value&something=else#testingTheFragment", url4.toString());


        URL url5 = new URLBuilder()
                .setOpaque("user@example.com")
                .setScheme("mailto")
                .setHost("192.168.1.1:8080")
                .setUsername("user")
                .setPassword("secret")
                .build();

        Assert.assertEquals("mailto:user@example.com", url5.toString());
        Assert.assertTrue(url5.isOpaque());
        Assert.assertTrue(url5.isAbsolute());

        URL url6 = new URLBuilder()
                .setPath("/path/to/file.html")
                .build();

        Assert.assertEquals("/path/to/file.html", url6.toString());
        Assert.assertFalse(url6.isOpaque());
        Assert.assertFalse(url6.isAbsolute());
    }
}
