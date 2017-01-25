package com.anthonynsimon.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class URLTest {

    private URLTestCase[] urlTestCases = {
            // IPv6 address with zone identifiers
            new URLTestCase(
                    "http://[fe80::1%25en0]:8080/",
                    "http",
                    null,
                    null,
                    "[fe80::1%en0]:8080",
                    "/",
                    null,
                    null,
                    "http://[fe80::1%25en0]:8080/"
            ),
            new URLTestCase(
                    "http://www.example.com",
                    "http",
                    null,
                    null,
                    "www.example.com",
                    null,
                    null,
                    null,
                    "http://www.example.com"
            ),
            new URLTestCase(
                    "https://example.com",
                    "https",
                    null,
                    null,
                    "example.com",
                    null,
                    null,
                    null,
                    "https://example.com"
            ),
            new URLTestCase(
                    "example.com",
                    null,
                    null,
                    null,
                    "example.com",
                    null,
                    null,
                    null,
                    "example.com"
            ),
            new URLTestCase(
                    "www.example.com",
                    null,
                    null,
                    null,
                    "www.example.com",
                    null,
                    null,
                    null,
                    "www.example.com"
            ),
            new URLTestCase(
                    "http://myusername:mypassword@www.example.com",
                    "http",
                    "myusername",
                    "mypassword",
                    "www.example.com",
                    null,
                    null,
                    null,
                    "http://myusername:mypassword@www.example.com"
            ),
            new URLTestCase(
                    "http://myusername:mypassword@example.co.uk",
                    "http",
                    "myusername",
                    "mypassword",
                    "example.co.uk",
                    null,
                    null,
                    null,
                    "http://myusername:mypassword@example.co.uk"
            ),
            new URLTestCase(
                    "http://example.co.uk:9090?q=true#q=hello+t",
                    "http",
                    null,
                    null,
                    "example.co.uk:9090",
                    null,
                    "q=true",
                    "q=hello+t",
                    "http://example.co.uk:9090?q=true#q=hello+t"
            ),
            new URLTestCase(
                    "http://www.domain.com/path/to/RESOURCE.html?q=abc#q=123",
                    "http",
                    null,
                    null,
                    "www.domain.com",
                    "/path/to/RESOURCE.html",
                    "q=abc",
                    "q=123",
                    "http://www.domain.com/path/to/RESOURCE.html?q=abc#q=123"
            ),
            new URLTestCase(
                    "http://www.DOMAIN.com/path/to/RESOURCE.html?q=abc#q=123",
                    "http",
                    null,
                    null,
                    "www.domain.com",
                    "/path/to/RESOURCE.html",
                    "q=abc",
                    "q=123",
                    "http://www.domain.com/path/to/RESOURCE.html?q=abc#q=123"
            ),
            new URLTestCase(
                    "http://myusername:mypassword@wWw.DOMAIN.co.CR/path/to/RESOURCE.html?q=aBc#q=123",
                    "http",
                    "myusername",
                    "mypassword",
                    "www.domain.co.cr",
                    "/path/to/RESOURCE.html",
                    "q=aBc",
                    "q=123",
                    "http://myusername:mypassword@www.domain.co.cr/path/to/RESOURCE.html?q=aBc#q=123"
            ),
            new URLTestCase(
                    "http://myusername:mypassword@www.domain.com:5432/path/to/RESOURCE.html?key=value&another=pair",
                    "http",
                    "myusername",
                    "mypassword",
                    "www.domain.com:5432",
                    "/path/to/RESOURCE.html",
                    "key=value&another=pair",
                    null,
                    "http://myusername:mypassword@www.domain.com:5432/path/to/RESOURCE.html?key=value&another=pair"
            ),
            new URLTestCase(
                    "ftp://john%20doe@www.domain.com/",
                    "ftp",
                    "john doe",
                    null,
                    "www.domain.com",
                    "/",
                    null,
                    null,
                    "ftp://john%20doe@www.domain.com/"
            ),
            new URLTestCase(
                    "ftp://john%20doe:my%20secret@www.domain.com/",
                    "ftp",
                    "john doe",
                    "my secret",
                    "www.domain.com",
                    "/",
                    null,
                    null,
                    "ftp://john%20doe:my%20secret@www.domain.com/"
            ),
            // Escape outside query
            new URLTestCase(
                    "https://www.domain.com/a%20b?q=c+d",
                    "https",
                    null,
                    null,
                    "www.domain.com",
                    "/a b",
                    "q=c+d",
                    null,
                    "https://www.domain.com/a%20b?q=c+d"
            ),
            // Escape inside query, DO NOT PARSE
            new URLTestCase(
                    "https://www.domain.com/a?q=my%20value",
                    "https",
                    null,
                    null,
                    "www.domain.com",
                    "/a",
                    "q=my%20value",
                    null,
                    "https://www.domain.com/a?q=my%20value"
            ),
            // Unescaped protocol inside query
            new URLTestCase(
                    "https://www.domain.com/a?q=http://bad",
                    "https",
                    null,
                    null,
                    "www.domain.com",
                    "/a",
                    "q=http://bad",
                    null,
                    "https://www.domain.com/a?q=http://bad"
            ),
            // Wildcard
            new URLTestCase(
                    "*#q=abc",
                    null,
                    null,
                    null,
                    null,
                    "*",
                    null,
                    "q=abc",
                    "*#q=abc"
            ),

    };

    @Test
    public void testUrls() throws Exception {
        for (URLTestCase testCase : urlTestCases) {
            URL url = new URL(testCase.input);
            assertEquals(testCase.expectedProtocol, url.protocol());
            assertEquals(testCase.expectedUsername, url.username());
            assertEquals(testCase.expectedPassword, url.password());
            assertEquals(testCase.expectedHost, url.host());
            assertEquals(testCase.expectedPath, url.path());
            assertEquals(testCase.expectedQuery, url.query());
            assertEquals(testCase.expectedFragment, url.fragment());
            assertEquals(testCase.expectedStringRepr, url.toString());
        }
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

        urlA = new URL("http://www.domain.com:8080/path/to/RESOURCE.html?q=abc");
        urlB = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        assertFalse(urlA.equals(urlB));

        urlA = new URL("http://www.domain.com/path/to/ANOTHER.html?q=abc");
        urlB = new URL("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        assertFalse(urlA.equals(urlB));
    }

    private class URLTestCase {
        public String input;
        public String expectedProtocol;
        public String expectedUsername;
        public String expectedPassword;
        public String expectedHost;
        public String expectedPath;
        public String expectedQuery;
        public String expectedFragment;
        public String expectedStringRepr;

        public URLTestCase(String input, String expectedProtocol, String expectedUsername, String expectedPassword, String expectedHost, String expectedPath, String expectedQuery, String expectedFragment, String expectedStringRepr) {
            this.input = input;
            this.expectedProtocol = expectedProtocol;
            this.expectedUsername = expectedUsername;
            this.expectedPassword = expectedPassword;
            this.expectedHost = expectedHost;
            this.expectedPath = expectedPath;
            this.expectedQuery = expectedQuery;
            this.expectedFragment = expectedFragment;
            this.expectedStringRepr = expectedStringRepr;
        }
    }
}