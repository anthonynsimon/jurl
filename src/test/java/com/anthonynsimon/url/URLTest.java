package com.anthonynsimon.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class URLTest {

    @Test(expected = MalformedURLException.class)
    public void testMalformed() throws MalformedURLException {
        URL url = new URL("http://");
    }

    @Test(expected = MalformedURLException.class)
    public void testNullParam() throws MalformedURLException {
        URL url = new URL("");
    }

    @Test(expected = MalformedURLException.class)
    public void testEmpty() throws MalformedURLException {
        URL url = new URL("");
    }

    @Test
    public void testProtocol() throws Exception {
        URL url = new URL("http://www.example.com");
        assertEquals("http", url.protocol());

        url = new URL("https://example.com");
        assertEquals("https", url.protocol());

        url = new URL("example.com");
        assertNull(url.protocol());

        url = new URL("www.example.com");
        assertNull(url.protocol());
    }

    @Test
    public void testUsername() throws Exception {
        URL url = new URL("http://myusername:mypassword@www.example.com");
        assertEquals("myusername", url.username());

        url = new URL("http://myusername:mypassword@example.co.uk");
        assertEquals("myusername", url.username());

        url = new URL("http://myotherusername@example.com");
        assertEquals("myotherusername", url.username());

        url = new URL("http://www.example.com");
        assertNull(url.username());
    }

    @Test(expected = MalformedURLException.class)
    public void testInvalidCredentials() throws Exception {
        URL url = new URL("http://myusername:mypassword:abc@www.example.com");
    }

    @Test
    public void testNoCredentials() throws Exception {
        URL url = new URL("http://@www.example.com");
        assertNull(url.username());
        assertNull(url.password());
    }

    @Test
    public void testPassword() throws Exception {
        URL url = new URL("http://myusername:mypassword@www.example.com");
        assertEquals("mypassword", url.password());

        url = new URL("http://:password@www.example.com");
        assertEquals("password", url.password());

        url = new URL("http://abc:password@example.co.uk");
        assertEquals("password", url.password());

        url = new URL("http://www.example.com");
        assertNull(url.password());
    }

    @Test
    public void testHost() throws Exception {
        URL url = new URL("http://myusername:mypassword@www.example.com");
        assertEquals("www.example.com", url.host());

        url = new URL("http://www.example.com");
        assertEquals("www.example.com", url.host());

        url = new URL("http://www.example.co.uk");
        assertEquals("www.example.co.uk", url.host());

        url = new URL("http://example.co.uk");
        assertEquals("example.co.uk", url.host());

        url = new URL("http://example.co.uk:9090");
        assertEquals("example.co.uk", url.host());

        url = new URL("http://example.co.uk:9090?q=true#q=hello+t");
        assertEquals("example.co.uk", url.host());
    }

    @Test
    public void testHostCaseInsensitive() throws Exception {
        URL url = new URL("http://myusername:mypassword@www.Example.com");
        assertEquals("www.example.com", url.host());

        url = new URL("http://eXAMPle.com");
        assertEquals("example.com", url.host());
    }

    @Test
    public void testPort() throws Exception {
        URL url = new URL("http://example.co.uk");
        assertNull(url.port());

        url = new URL("http://example.co.uk:9090");
        assertEquals(Integer.valueOf(9090), url.port());

        url = new URL("http://example.co.uk:5432?q=true#q=hello+t");
        assertEquals(Integer.valueOf(5432), url.port());
    }

    @Test
    public void testPath() throws Exception {
        URL url = new URL("http://myusername:mypassword@www.Example.com");
        assertEquals("/", url.path());

        url = new URL("http://myusername:mypassword@www.EXAMPLE.com/my/path/here");
        assertEquals("/my/path/here", url.path());

        url = new URL("http://eXAMPle.com/my/house");
        assertEquals("/my/house", url.path());

        url = new URL("http://www.domain.com/path/to/resource.html?q=abc#q=123");
        assertEquals("/path/to/resource.html", url.path());
    }

    @Test
    public void testPathCaseSensitive() throws Exception {
        URL url = new URL("http://myusername:mypassword@www.Example.com/ABc");
        assertEquals("/ABc", url.path());

        url = new URL("http://myusername:mypassword@www.EXAMPLE.com/my/PATH/here");
        assertEquals("/my/PATH/here", url.path());

        url = new URL("http://eXAMPle.com/my/house");
        assertEquals("/my/house", url.path());

        url = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc#q=123");
        assertEquals("/path/to/RESOURCE.html", url.path());
    }

    @Test
    public void testQuery() throws Exception {
        URL url = new URL("http://example.com/my/house");
        assertNull(url.query());

        url = new URL("http://example.com/my/house?");
        assertNull(url.query());

        url = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc#q=123");
        assertEquals("q=abc", url.query());

        url = new URL("http://www.domain.com/path/to/RESOURCE.html?q=#q=123");
        assertEquals("q=", url.query());

        url = new URL("http://www.domain.com/path/to/RESOURCE.html?present");
        assertEquals("present", url.query());

        url = new URL("http://www.domain.com/path/to/RESOURCE.html?q=one+two+three");
        assertEquals("q=one+two+three", url.query());

        url = new URL("http://www.domain.com/path/to/RESOURCE.html?key=value&another=pair");
        assertEquals("key=value&another=pair", url.query());
    }

    @Test
    public void testFragment() throws Exception {
        URL url = new URL("http://www.domain.com/path/to/RESOURCE.html?q=123");
        assertNull(url.fragment());

        url = new URL("http://www.domain.com/path/to/RESOURCE.html?q=#q=123");
        assertEquals("q=123", url.fragment());

        url = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc#q=one+two+three");
        assertEquals("q=one+two+three", url.fragment());

        url = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        assertEquals("section", url.fragment());
    }

    @Test
    public void testToString() throws Exception {
        URL url = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        assertEquals("http://www.domain.com/path/to/RESOURCE.html?q=abc#section", url.toString());

        url = new URL("http://www.domain.com:/path/to/RESOURCE.html?");
        assertEquals("http://www.domain.com/path/to/RESOURCE.html", url.toString());

        url = new URL("http://www.domain.com:/path/to/RESOURCE.html#");
        assertEquals("http://www.domain.com/path/to/RESOURCE.html", url.toString());

        url = new URL("http://www.myDomain.COM/path/to/RESOURCE.html?");
        assertEquals("http://www.mydomain.com/path/to/RESOURCE.html", url.toString());
    }

    @Test
    public void testEquals() throws Exception {
        URL urlA = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        URL urlB = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        assertTrue(urlA.equals(urlB));

        urlA = new URL("http://domain.com/path/to/RESOURCE.html?q=abc#section");
        urlB = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        assertFalse(urlA.equals(urlB));

        urlA = new URL("http://www.DOMAIN.com/path/to/RESOURCE.html?q=abc#section");
        urlB = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        assertTrue(urlA.equals(urlB));

        urlA = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        urlB = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        assertFalse(urlA.equals(urlB));

        urlA = new URL("http://domain.com/path/to/RESOURCE.html?q=abc");
        urlB = new URL("http://domain.com/path/to/RESOURCE.html?q=abc#");
        assertTrue(urlA.equals(urlB));

        urlA = new URL("http://www.domain.de/path/to/RESOURCE.html?q=abc");
        urlB = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        assertFalse(urlA.equals(urlB));

        urlA = new URL("http://www.domain.com/path/to/ANOTHER.html?q=abc");
        urlB = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        assertFalse(urlA.equals(urlB));
    }
}