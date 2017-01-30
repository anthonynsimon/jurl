package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.MalformedURLException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class URLTest {

    private URLTestCase[] urlTestCases = {
            // No path
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
            // With path
            new URLTestCase(
                    "http://www.example.com/",
                    "http",
                    null,
                    null,
                    "www.example.com",
                    "/",
                    null,
                    null,
                    "http://www.example.com/"
            ),
            // Path with hex escaping
            new URLTestCase(
                    "http://www.example.com/path%20one%20two%26three",
                    "http",
                    null,
                    null,
                    "www.example.com",
                    "/path one two&three",
                    null,
                    null,
                    "http://www.example.com/path%20one%20two&three"
            ),
            // Non - ASCII
            new URLTestCase(
                    "http://test.ü€€€€€𡺸.com/foo",
                    "http",
                    null,
                    null,
                    "test.ü€€€€€𡺸.com",
                    "/foo",
                    null,
                    null,
                    "http://test.%C3%BC%E2%82%AC%E2%82%AC%E2%82%AC%E2%82%AC%E2%82%AC%F0%A1%BA%B8.com/foo"
            ),
            // Non-ASCII
            new URLTestCase(
                    "http://test.%C3%BC%E2%82%AC%E2%82%AC%E2%82%AC%E2%82%AC%E2%82%AC%F0%A1%BA%B8.com/foo",
                    "http",
                    null,
                    null,
                    "test.ü€€€€€𡺸.com",
                    "/foo",
                    null,
                    null,
                    "http://test.%C3%BC%E2%82%AC%E2%82%AC%E2%82%AC%E2%82%AC%E2%82%AC%F0%A1%BA%B8.com/foo"
            ),
            // Username
            new URLTestCase(
                    "ftp://me@www.example.com/",
                    "ftp",
                    "me",
                    null,
                    "www.example.com",
                    "/",
                    null,
                    null,
                    "ftp://me@www.example.com/"
            ),
            // Username with escaping
            new URLTestCase(
                    "ftp://me%20again@www.example.com/",
                    "ftp",
                    "me again",
                    null,
                    "www.example.com",
                    "/",
                    null,
                    null,
                    "ftp://me%20again@www.example.com/"
            ),
            // Empty query string
            new URLTestCase(
                    "http://www.example.com/?",
                    "http",
                    null,
                    null,
                    "www.example.com",
                    "/",
                    null,
                    null,
                    "http://www.example.com/"
            ),
            // Query string ending in query char
            new URLTestCase(
                    "http://www.example.com/?foo=bar?",
                    "http",
                    null,
                    null,
                    "www.example.com",
                    "/",
                    "foo=bar?",
                    null,
                    "http://www.example.com/?foo=bar?"
            ),
            // Query string
            new URLTestCase(
                    "http://www.example.com/?q=one+two",
                    "http",
                    null,
                    null,
                    "www.example.com",
                    "/",
                    "q=one+two",
                    null,
                    "http://www.example.com/?q=one+two"
            ),
            // Query string with multiple values
            new URLTestCase(
                    "http://www.example.com/?q=one+two&key=value&another",
                    "http",
                    null,
                    null,
                    "www.example.com",
                    "/",
                    "q=one+two&key=value&another",
                    null,
                    "http://www.example.com/?q=one+two&key=value&another"
            ),
            // Query with hex escaping
            new URLTestCase(
                    "http://www.example.com/?q=one%20two",
                    "http",
                    null,
                    null,
                    "www.example.com",
                    "/",
                    "q=one%20two",
                    null,
                    "http://www.example.com/?q=one%20two"
            ),
            // Hex escaping outside query
            new URLTestCase(
                    "http://www.example.com/one%20two?q=three+four",
                    "http",
                    null,
                    null,
                    "www.example.com",
                    "/one two",
                    "q=three+four",
                    null,
                    "http://www.example.com/one%20two?q=three+four"
            ),
            // Path without leading /
            new URLTestCase(
                    "http:www.example.com/one%20two?q=three+four",
                    "http",
                    null,
                    null,
                    null,
                    null,
                    "q=three+four",
                    null,
                    "http:www.example.com/one%20two?q=three+four"
            ),
            // Path without leading /, escaped
            new URLTestCase(
                    "http:%2f%2fwww.example.com/one%20two?q=three+four",
                    "http",
                    null,
                    null,
                    null,
                    null,
                    "q=three+four",
                    null,
                    "http:%2f%2fwww.example.com/one%20two?q=three+four"
            ),
            // Opaque with fragment
            new URLTestCase(
                    "http:%2f%2fwww.example.com/one%20two?q=three+four#flag",
                    "http",
                    null,
                    null,
                    null,
                    null,
                    "q=three+four",
                    "flag",
                    "http:%2f%2fwww.example.com/one%20two?q=three+four#flag"
            ),
            // Non-authority with path
            new URLTestCase(
                    "mailto:/admin@example.com",
                    "mailto",
                    null,
                    null,
                    null,
                    "/admin@example.com",
                    null,
                    null,
                    "mailto:///admin@example.com"
            ),
            // Non-authority
            new URLTestCase(
                    "mailto:admin@example.com",
                    "mailto",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "mailto:admin@example.com"
            ),
            // Unescaped :// should not create scheme
            new URLTestCase(
                    "/foo?q=http://something",
                    null,
                    null,
                    null,
                    null,
                    "/foo",
                    "q=http://something",
                    null,
                    "/foo?q=http://something"
            ),
            // Leading // without scheme should create an authority
            new URLTestCase(
                    "//foo",
                    null,
                    null,
                    null,
                    "foo",
                    null,
                    null,
                    null,
                    "//foo"
            ),
            // Leading // without scheme, with credentials and query
            new URLTestCase(
                    "//user@foo/path?a=b",
                    null,
                    "user",
                    null,
                    "foo",
                    "/path",
                    "a=b",
                    null,
                    "//user@foo/path?a=b"
            ),
            // Three leading slashes
            new URLTestCase(
                    "///hello",
                    null,
                    null,
                    null,
                    null,
                    "///hello",
                    null,
                    null,
                    "///hello"
            ),
            // Don't try to resolve path
            new URLTestCase(
                    "http://example.com/abc/..",
                    "http",
                    null,
                    null,
                    "example.com",
                    "/abc/..",
                    null,
                    null,
                    "http://example.com/abc/.."
            ),
            // Username and password
            new URLTestCase(
                    "https://user:password@example.com",
                    "https",
                    "user",
                    "password",
                    "example.com",
                    null,
                    null,
                    null,
                    "https://user:password@example.com"
            ),
            // Unescaped @ in username
            new URLTestCase(
                    "https://us@r:password@example.com",
                    "https",
                    "us@r",
                    "password",
                    "example.com",
                    null,
                    null,
                    null,
                    "https://us%40r:password@example.com"
            ),
            // Unescaped @ in password
            new URLTestCase(
                    "https://user:p@ssword@example.com",
                    "https",
                    "user",
                    "p@ssword",
                    "example.com",
                    null,
                    null,
                    null,
                    "https://user:p%40ssword@example.com"
            ),
            // Unescaped @ in everywhere
            new URLTestCase(
                    "https://us@r:p@ssword@example.com/p@th?q=@here",
                    "https",
                    "us@r",
                    "p@ssword",
                    "example.com",
                    "/p@th",
                    "q=@here",
                    null,
                    "https://us%40r:p%40ssword@example.com/p@th?q=@here"
            ),
            // Query string and fragment
            new URLTestCase(
                    "https://www.example.de/?q=foo#q=bar",
                    "https",
                    null,
                    null,
                    "www.example.de",
                    "/",
                    "q=foo",
                    "q=bar",
                    "https://www.example.de/?q=foo#q=bar"
            ),
            // Query string and fragment with escaped chars
            new URLTestCase(
                    "https://www.example.de/?q%26foo#a%26b",
                    "https",
                    null,
                    null,
                    "www.example.de",
                    "/",
                    "q%26foo",
                    "a%26b",
                    "https://www.example.de/?q%26foo#a%26b"
            ),
            // File path
            new URLTestCase(
                    "file:///user/docs/recent",
                    "file",
                    null,
                    null,
                    null,
                    "/user/docs/recent",
                    null,
                    null,
                    "file:///user/docs/recent"
            ),
            // Windows file path
            new URLTestCase(
                    "file:///C:/User/Docs/recent.xlsx",
                    "file",
                    null,
                    null,
                    null,
                    "/C:/User/Docs/recent.xlsx",
                    null,
                    null,
                    "file:///C:/User/Docs/recent.xlsx"
            ),
            // Case-insensitive scheme
            new URLTestCase(
                    "HTTP://example.com",
                    "http",
                    null,
                    null,
                    "example.com",
                    null,
                    null,
                    null,
                    "http://example.com"
            ),
            // Relative path
            new URLTestCase(
                    "abc/123/xyz",
                    null,
                    null,
                    null,
                    null,
                    "abc/123/xyz",
                    null,
                    null,
                    "/abc/123/xyz"
            ),
            // Escaped ? in credentials
            new URLTestCase(
                    "https://us%3Fer:p%3fssword@example.com",
                    "https",
                    "us?er",
                    "p?ssword",
                    "example.com",
                    null,
                    null,
                    null,
                    "https://us%3Fer:p%3Fssword@example.com"
            ),
            // IPv4 address
            new URLTestCase(
                    "http://192.168.0.1",
                    "http",
                    null,
                    null,
                    "192.168.0.1",
                    null,
                    null,
                    null,
                    "http://192.168.0.1"
            ),
            // IPv4 address with port
            new URLTestCase(
                    "http://192.168.0.1:8080",
                    "http",
                    null,
                    null,
                    "192.168.0.1:8080",
                    null,
                    null,
                    null,
                    "http://192.168.0.1:8080"
            ),
            // IPv4 address with path
            new URLTestCase(
                    "http://192.168.0.1/",
                    "http",
                    null,
                    null,
                    "192.168.0.1",
                    "/",
                    null,
                    null,
                    "http://192.168.0.1/"
            ),
            // IPv4 address with port and path
            new URLTestCase(
                    "http://192.168.0.1:8080/",
                    "http",
                    null,
                    null,
                    "192.168.0.1:8080",
                    "/",
                    null,
                    null,
                    "http://192.168.0.1:8080/"
            ),
            // IPv6 address with port and path
            new URLTestCase(
                    "http://[fe80::1]:8080/",
                    "http",
                    null,
                    null,
                    "[fe80::1]:8080",
                    "/",
                    null,
                    null,
                    "http://[fe80::1]:8080/"
            ),
            // IPv6 address
            new URLTestCase(
                    "http://[fe80::1]",
                    "http",
                    null,
                    null,
                    "[fe80::1]",
                    null,
                    null,
                    null,
                    "http://[fe80::1]"
            ),
            // IPv6 address with port
            new URLTestCase(
                    "http://[fe80::1]:8080",
                    "http",
                    null,
                    null,
                    "[fe80::1]:8080",
                    null,
                    null,
                    null,
                    "http://[fe80::1]:8080"
            ),
            // IPv6 address with port, path and query
            new URLTestCase(
                    "http://[fe80::1]:8080/?q=foo",
                    "http",
                    null,
                    null,
                    "[fe80::1]:8080",
                    "/",
                    "q=foo",
                    null,
                    "http://[fe80::1]:8080/?q=foo"
            ),
            // IPv6 address with zone identifier, port, path and query
            new URLTestCase(
                    "http://[fe80::1%25en0]:8080/?q=foo",
                    "http",
                    null,
                    null,
                    "[fe80::1%en0]:8080",
                    "/",
                    "q=foo",
                    null,
                    "http://[fe80::1%25en0]:8080/?q=foo"
            ),
            // IPv6 address with zone identifier special chars
            new URLTestCase(
                    "http://[fe80::1%25%65%6e%301-._~]/",
                    "http",
                    null,
                    null,
                    "[fe80::1%en01-._~]",
                    "/",
                    null,
                    null,
                    "http://[fe80::1%25en01-._~]/"
            ),
            // IPv6 address with zone identifier special chars and port
            new URLTestCase(
                    "http://[fe80::1%25%65%6e%301-._~]:8080/",
                    "http",
                    null,
                    null,
                    "[fe80::1%en01-._~]:8080",
                    "/",
                    null,
                    null,
                    "http://[fe80::1%25en01-._~]:8080/"
            ),
            // Alternate escape
            new URLTestCase(
                    "http://rest.rsc.io/foo%2fbar/baz%2Fquux?alt=media",
                    "http",
                    null,
                    null,
                    "rest.rsc.io",
                    "/foo/bar/baz/quux",
                    "alt=media",
                    null,
                    "http://rest.rsc.io/foo/bar/baz/quux?alt=media"
            ),
            // Commas in host
            new URLTestCase(
                    "psql://a,b,c/foo",
                    "psql",
                    null,
                    null,
                    "a,b,c",
                    "/foo",
                    null,
                    null,
                    "psql://a,b,c/foo"
            ),
            // Difficult case host
            new URLTestCase(
                    "http://!$&'()*+,;=hello!:8080/path",
                    "http",
                    null,
                    null,
                    "!$&'()*+,;=hello!:8080",
                    "/path",
                    null,
                    null,
                    "http://!$&'()*+,;=hello!:8080/path"
            ),
            // Difficult case path
            new URLTestCase(
                    "http://host/!$&'()*+,;=:@[hello]",
                    "http",
                    null,
                    null,
                    "host",
                    "/!$&'()*+,;=:@[hello]",
                    null,
                    null,
                    "http://host/!$&'()*+,;=:@[hello]"
            ),
            // Special chars in path
            new URLTestCase(
                    "http://host/abc/[one_two]",
                    "http",
                    null,
                    null,
                    "host",
                    "/abc/[one_two]",
                    null,
                    null,
                    "http://host/abc/[one_two]"
            ),
            // IPv6
            new URLTestCase(
                    "http://[2001:1890:1112:1::20]/foo",
                    "http",
                    null,
                    null,
                    "[2001:1890:1112:1::20]",
                    "/foo",
                    null,
                    null,
                    "http://[2001:1890:1112:1::20]/foo"
            ),
            // IPv6
            new URLTestCase(
                    "http://[fde2:d7de:302::]",
                    "http",
                    null,
                    null,
                    "[fde2:d7de:302::]",
                    null,
                    null,
                    null,
                    "http://[fde2:d7de:302::]"
            ),
            // IPv6 with spaces in scope IDs are the place where they're allowed
            new URLTestCase(
                    "http://[fde2:d7de:302::%25are%20you%20being%20serious]",
                    "http",
                    null,
                    null,
                    "[fde2:d7de:302::%are you being serious]",
                    null,
                    null,
                    null,
                    "http://[fde2:d7de:302::%25are%20you%20being%20serious]"
            ),
            new URLTestCase(
                    "http://test.com//foo",
                    "http",
                    null,
                    null,
                    "test.com",
                    "//foo",
                    null,
                    null,
                    "http://test.com//foo"
            ),
            // Lowercase escape hex digits
            new URLTestCase(
                    "http://TEST.%e4%b8%96%e7%95%8c.com/foo",
                    "http",
                    null,
                    null,
                    "test.世界.com",
                    "/foo",
                    null,
                    null,
                    "http://test.%E4%B8%96%E7%95%8C.com/foo"
            ),
    };

    private URLReferenceTestCase[] resolveReferenceCases = new URLReferenceTestCase[]{
            new URLReferenceTestCase(
                    "http://www.domain.com/path/to/RESOURCE.html",
                    "http://www.domain.com/path/to/ANOTHER_RESOURCE.html?q=abc#section",
                    "http://www.domain.com/path/to/ANOTHER_RESOURCE.html?q=abc#section"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/path/to/RESOURCE.html",
                    "/path/to/ANOTHER_RESOURCE.html?q=abc#section",
                    "http://www.domain.com/path/to/ANOTHER_RESOURCE.html?q=abc#section"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/?q=foo",
                    "/path?q=abc#section",
                    "http://www.domain.com/path?q=abc#section"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/?q=foo",
                    "#section",
                    "http://www.domain.com/#section"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/bar",
                    "/foo",
                    "http://www.domain.com/foo"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/bar?q#y",
                    "/foo",
                    "http://www.domain.com/foo"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/bar?q#y",
                    "/foo?k#z",
                    "http://www.domain.com/foo?k#z"
            ),
            new URLReferenceTestCase(
                    "mailto:user@example.com",
                    "//example.com",
                    "mailto://example.com"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/bar?q#y",
                    "//example.com",
                    "http://example.com"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com",
                    "/path",
                    "http://www.domain.com/path"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com",
                    "home",
                    "http://www.domain.com/home"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/",
                    "path",
                    "http://www.domain.com/path"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/here/there",
                    "/here/there/that",
                    "http://www.domain.com/here/there/that"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/one/two",
                    "three",
                    "http://www.domain.com/one/three"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/one/two",
                    "//example.com/three",
                    "http://example.com/three"
            ),
            new URLReferenceTestCase(
                    "http://192.168.0.1/one",
                    "http://192.168.0.1/three",
                    "http://192.168.0.1/three"
            ),
            new URLReferenceTestCase(
                    "http://192.168.0.1/one",
                    "/three",
                    "http://192.168.0.1/three"
            ),
            new URLReferenceTestCase(
                    "http://192.168.0.1:44/one",
                    "/three",
                    "http://192.168.0.1:44/three"
            ),
            new URLReferenceTestCase(
                    "http://192.168.0.1/one",
                    "three",
                    "http://192.168.0.1/three"
            ),
            new URLReferenceTestCase(
                    "http://192.168.0.1",
                    "three",
                    "http://192.168.0.1/three"
            ),
            new URLReferenceTestCase(
                    "http://[1080::8:800:200C:417A]/foo",
                    "three",
                    "http://[1080::8:800:200c:417a]/three"
            ),
            new URLReferenceTestCase(
                    "http://[1080::8:800:200C:417A]:9090/foo",
                    "three",
                    "http://[1080::8:800:200c:417a]:9090/three"
            ),
            new URLReferenceTestCase(
                    "http://[1080::8:800:200C:417A]:9090/foo/",
                    "three",
                    "http://[1080::8:800:200c:417a]:9090/foo/three"
            ),
            new URLReferenceTestCase(
                    "http://[1080::8:800:200C:417A]:9090/foo/a",
                    "three",
                    "http://[1080::8:800:200c:417a]:9090/foo/three"
            ),
            // Opaque base
            new URLReferenceTestCase(
                    "http:www.domain.com/",
                    "path",
                    "http:///path"
            ),
            new URLReferenceTestCase(
                    "http://www.domain.com/",
                    "mailto:user@domain.com",
                    "mailto:user@domain.com"
            ),
            new URLReferenceTestCase(
                    "mailto:user@domain.com",
                    "mailto:another@test.de",
                    "mailto:another@test.de"
            ),
            new URLReferenceTestCase(
                    "file://",
                    "/documents",
                    "file:///documents"
            ),
            new URLReferenceTestCase(
                    "file:///",
                    "/documents",
                    "file:///documents"
            ),
            new URLReferenceTestCase(
                    "file:///home",
                    "/documents",
                    "file:///documents"
            ),
            new URLReferenceTestCase(
                    "file:///user/documents",
                    "pictures",
                    "file:///user/pictures"
            ),
            new URLReferenceTestCase(
                    "file:///user/documents/one",
                    "pictures",
                    "file:///user/documents/pictures"
            ),
            new URLReferenceTestCase(
                    "file:///",
                    "pictures",
                    "file:///pictures"
            ),
            new URLReferenceTestCase(
                    "file:///home/user/",
                    "../here",
                    "file:///home/here"
            ),
            new URLReferenceTestCase(
                    "file:///home/user/",
                    "..",
                    "file:///home/"
            ),
            new URLReferenceTestCase(
                    "file:///home/user/",
                    ".",
                    "file:///home/user/"
            ),
            new URLReferenceTestCase(
                    "file:///home/user",
                    ".",
                    "file:///home/"
            ),
            new URLReferenceTestCase(
                    "http://home.com/user",
                    "../../../",
                    "http://home.com/"
            ),
            new URLReferenceTestCase(
                    "http://home.com/user/test",
                    "foo/bar/../last",
                    "http://home.com/user/foo/last"
            ),
            new URLReferenceTestCase(
                    "http://home.com/user/test",
                    "foo/bar/../last/..",
                    "http://home.com/user/foo/"
            ),
            new URLReferenceTestCase(
                    "http://home.com/user/test",
                    "./..",
                    "http://home.com/"
            ),
            new URLReferenceTestCase(
                    "http://home.com/user/test",
                    ".",
                    "http://home.com/user/"
            ),
            new URLReferenceTestCase(
                    "http://home.com/user/test",
                    "..",
                    "http://home.com/"
            ),
            new URLReferenceTestCase(
                    "http://home.com",
                    ".",
                    "http://home.com/"
            ),
            new URLReferenceTestCase(
                    "http://home.com/",
                    ".",
                    "http://home.com/"
            ),
            new URLReferenceTestCase(
                    "http://home.com/foo",
                    ".",
                    "http://home.com/"
            ),
            new URLReferenceTestCase(
                    "http://home.com/foo/",
                    ".",
                    "http://home.com/foo/"
            ),
            new URLReferenceTestCase(
                    "http://home.com/foo/",
                    "../../../../bar",
                    "http://home.com/bar"
            ),
            new URLReferenceTestCase(
                    "http://home.com/foo/",
                    "./../../.././../bar",
                    "http://home.com/bar"
            ),
            new URLReferenceTestCase(
                    "http://home.com/foo/",
                    "./../../.././../bar/",
                    "http://home.com/bar"
            ),
            new URLReferenceTestCase(
                    "http://home.com/foo/bar",
                    "a/./b/../c/../d/./last/..",
                    "http://home.com/foo/a/d/"
            ),
            // Triple dots do not affect the path
            new URLReferenceTestCase(
                    "http://home.com/foo/bar/",
                    "...",
                    "http://home.com/foo/bar/..."
            ),
            // Triple dots do not affect the path
            new URLReferenceTestCase(
                    "http://home.com/foo/bar/",
                    "/...",
                    "http://home.com/..."
            ),
            // Triple dots do not affect the path
            new URLReferenceTestCase(
                    "http://home.com/foo/bar/",
                    "./...",
                    "http://home.com/foo/bar/..."
            ),
    };

    private PathResolveTestCase[] pathResolveCases = new PathResolveTestCase[]{
            new PathResolveTestCase(
                    "",
                    "",
                    ""
            ),
            new PathResolveTestCase(
                    "abc",
                    "",
                    "/abc"
            ),
            new PathResolveTestCase(
                    "abc",
                    "456",
                    "/456"
            ),
            new PathResolveTestCase(
                    "/abc",
                    "123",
                    "/123"
            ),
            new PathResolveTestCase(
                    "/abc/abc",
                    "/123",
                    "/123"
            ),
            new PathResolveTestCase(
                    "abc/def",
                    ".",
                    "/abc/"
            ),
            new PathResolveTestCase(
                    "abc/def",
                    "123",
                    "/abc/123"
            ),
            new PathResolveTestCase(
                    "abc/def",
                    "..",
                    "/"
            ),
            new PathResolveTestCase(
                    "abc/",
                    "..",
                    "/"
            ),
            new PathResolveTestCase(
                    "abc/",
                    "../..",
                    "/"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    "..",
                    "/abc/"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    ".",
                    "/abc/def/"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    "../123",
                    "/abc/123"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    ".././123",
                    "/abc/123"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    "../../123",
                    "/123"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    "./../123",
                    "/abc/123"
            ),
            new PathResolveTestCase(
                    "abc/../hij",
                    "",
                    "/hij"
            ),
            new PathResolveTestCase(
                    "abc/hij",
                    "./..",
                    "/"
            ),
            new PathResolveTestCase(
                    "abc/./hij",
                    ".",
                    "/abc/"
            ),
            new PathResolveTestCase(
                    "abc/./hij",
                    "",
                    "/abc/hij"
            ),
            new PathResolveTestCase(
                    "abc/../hij",
                    "",
                    "/hij"
            ),
            new PathResolveTestCase(
                    "abc/../hij",
                    ".",
                    "/"
            ),
            new PathResolveTestCase(
                    "",
                    "../../../././../",
                    "/"
            ),
            new PathResolveTestCase(
                    "../../../././../",
                    "../../../././../",
                    "/"
            ),
            new PathResolveTestCase(
                    "../////../.././/./../",
                    "../../../././../",
                    "/"
            ),
            new PathResolveTestCase(
                    "abc",
                    "////",
                    "/"
            ),
            new PathResolveTestCase(
                    "/////",
                    "abc",
                    "/abc"
            ),
            new PathResolveTestCase(
                    "abc/.././123",
                    "x",
                    "/x"
            ),
    };

    private QueryStringTestCase[] queryStringCases = new QueryStringTestCase[]{
            new QueryStringTestCase(
                    "http://example.com",
                    new HashMap<String, String>()
            ),
            new QueryStringTestCase(
                    "http://example.com?",
                    new HashMap<String, String>()
            ),
            new QueryStringTestCase(
                    "http://example.com?key=value",
                    new HashMap<String, String>() {{
                        put("key", "value");
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?key=",
                    new HashMap<String, String>()
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos",
                    new HashMap<String, String>() {{
                        put("one", "uno");
                        put("two", "dos");
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&",
                    new HashMap<String, String>() {{
                        put("one", "uno");
                        put("two", "dos");
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&three=",
                    new HashMap<String, String>() {{
                        put("one", "uno");
                        put("two", "dos");
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&three",
                    new HashMap<String, String>() {{
                        put("one", "uno");
                        put("two", "dos");
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&=",
                    new HashMap<String, String>() {{
                        put("one", "uno");
                        put("two", "dos");
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&===",
                    new HashMap<String, String>() {{
                        put("one", "uno");
                        put("two", "dos");
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&===&three=tres",
                    new HashMap<String, String>() {{
                        put("one", "uno");
                        put("two", "dos");
                        put("three", "tres");
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&===&&=&&three=tres",
                    new HashMap<String, String>() {{
                        put("one", "uno");
                        put("two", "dos");
                        put("three", "tres");
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&===&&=&&three=tres#fragment=hello",
                    new HashMap<String, String>() {{
                        put("one", "uno");
                        put("two", "dos");
                        put("three", "tres");
                    }}
            ),
    };

    @Test
    public void testUrls() throws Exception {
        for (URLTestCase testCase : urlTestCases) {
            URL url = URL.parse(testCase.input);
            assertEquals(testCase.expectedScheme, url.scheme());
            assertEquals(testCase.expectedUsername, url.username());
            assertEquals(testCase.expectedPassword, url.password());
            assertEquals(testCase.expectedHost, url.host());
            assertEquals(testCase.expectedPath, url.path());
            assertEquals(testCase.expectedQuery, url.query());
            assertEquals(testCase.expectedFragment, url.fragment());
            assertEquals(testCase.expectedStringRepr, url.toString());
        }
    }

    @Test
    public void testQueryStringValues() throws Exception {
        for (QueryStringTestCase testCase : queryStringCases) {
            URL url = URL.parse(testCase.input);
            assertEquals(testCase.expected, url.queryPairs());
        }
    }

    @Test(expected = MalformedURLException.class)
    public void testNullParam() throws MalformedURLException {
        URL url = URL.parse("");
    }

    @Test(expected = MalformedURLException.class)
    public void testEmpty() throws MalformedURLException {
        URL url = URL.parse("");
    }

    @Test(expected = MalformedURLException.class)
    public void testColonWithoutPort() throws MalformedURLException {
        URL url = URL.parse("http://host:/abc/[one_two]");
    }

    @Test(expected = MalformedURLException.class)
    public void testMalformedIPv6() throws MalformedURLException {
        URL url = URL.parse("http://e34::1");
    }

    @Test
    public void testAbsoluteURLs() throws Exception {
        assertTrue(URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section").isAbsolute());
        assertTrue(URL.parse("http://domain.com").isAbsolute());
        assertTrue(URL.parse("http://www.domain.com?key=value").isAbsolute());
        assertTrue(URL.parse("http://192.168.0.1/path/to/RESOURCE.html?q=abc#section").isAbsolute());
        assertTrue(URL.parse("http://192.168.0.1").isAbsolute());
        assertTrue(URL.parse("file:///home/user").isAbsolute());

        assertFalse(URL.parse("///home/user").isAbsolute());
        assertFalse(URL.parse("/home/user").isAbsolute());
        assertFalse(URL.parse("home/user").isAbsolute());
        assertFalse(URL.parse("home").isAbsolute());
    }

    @Test
    public void testResolveReferences() throws Exception {
        for (URLReferenceTestCase testCase : resolveReferenceCases) {
            URL base = URL.parse(testCase.inputBase);
            URL urlRef = URL.parse(testCase.inputReference);

            URL resolvedUrlRef = base.resolveReference(urlRef);
            URL resolvedStringRef = base.resolveReference(testCase.inputReference);

            assertEquals(testCase.expectedResolvedReference, resolvedUrlRef.toString());
            assertEquals(testCase.expectedResolvedReference, resolvedStringRef.toString());
        }
    }

    @Test
    public void testResolvePath() throws Exception {
        for (PathResolveTestCase testCase : pathResolveCases) {
            URL url = new URL();
            String resolved = url.resolvePath(testCase.inputBase, testCase.inputReference);
            assertEquals(testCase.expected, resolved);
        }
    }


    @Test
    public void testEquals() throws Exception {
        URL urlA = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        URL urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        assertTrue(urlA.equals(urlB));

        urlA = URL.parse("http://domain.com/path/to/RESOURCE.html?q=abc#section");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        assertFalse(urlA.equals(urlB));

        urlA = URL.parse("http://www.DOMAIN.com/path/to/RESOURCE.html?q=abc#section");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        assertTrue(urlA.equals(urlB));

        urlA = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        assertFalse(urlA.equals(urlB));

        urlA = URL.parse("http://domain.com/path/to/RESOURCE.html?q=abc");
        urlB = URL.parse("http://domain.com/path/to/RESOURCE.html?q=abc#");
        assertTrue(urlA.equals(urlB));

        urlA = URL.parse("http://www.domain.de/path/to/RESOURCE.html?q=abc");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        assertFalse(urlA.equals(urlB));

        urlA = URL.parse("http://www.domain.com:8080/path/to/RESOURCE.html?q=abc");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        assertFalse(urlA.equals(urlB));

        urlA = URL.parse("http://www.domain.com/path/to/ANOTHER.html?q=abc");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        assertFalse(urlA.equals(urlB));
    }

    private class URLReferenceTestCase {
        public String inputBase;
        public String inputReference;
        public String expectedResolvedReference;

        public URLReferenceTestCase(String inputBase, String inputReference, String expectedResolvedReference) {
            this.inputBase = inputBase;
            this.inputReference = inputReference;
            this.expectedResolvedReference = expectedResolvedReference;
        }
    }

    private class PathResolveTestCase {
        public String inputBase;
        public String inputReference;
        public String expected;

        public PathResolveTestCase(String inputBase, String inputReference, String expected) {
            this.inputBase = inputBase;
            this.inputReference = inputReference;
            this.expected = expected;
        }
    }

    private class URLTestCase {
        public String input;
        public String expectedScheme;
        public String expectedUsername;
        public String expectedPassword;
        public String expectedHost;
        public String expectedPath;
        public String expectedQuery;
        public String expectedFragment;
        public String expectedStringRepr;

        public URLTestCase(String input, String expectedScheme, String expectedUsername, String expectedPassword, String expectedHost, String expectedPath, String expectedQuery, String expectedFragment, String expectedStringRepr) {
            this.input = input;
            this.expectedScheme = expectedScheme;
            this.expectedUsername = expectedUsername;
            this.expectedPassword = expectedPassword;
            this.expectedHost = expectedHost;
            this.expectedPath = expectedPath;
            this.expectedQuery = expectedQuery;
            this.expectedFragment = expectedFragment;
            this.expectedStringRepr = expectedStringRepr;
        }
    }

    private class QueryStringTestCase {
        public String input;
        public Map<String, String> expected;

        public QueryStringTestCase(String input, Map<String, String> expected) {
            this.input = input;
            this.expected = expected;
        }
    }
}