package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.InvalidURLReferenceException;
import com.anthonynsimon.url.exceptions.MalformedURLException;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

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
                    "http://www.example.com/path%20one%20two%26three"
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
                    "?",
                    null,
                    "http://www.example.com/?"
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
                    "abc/123/xyz"
            ),
            // '*' path
            new URLTestCase(
                    "*",
                    null,
                    null,
                    null,
                    null,
                    "*",
                    null,
                    null,
                    "*"
            ),
            // '*' path with query and fragment
            new URLTestCase(
                    "*?key=value#frag",
                    null,
                    null,
                    null,
                    null,
                    "*",
                    "key=value",
                    "frag",
                    "*?key=value#frag"
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
                    "http://rest.rsc.io/foo%2fbar/baz%2Fquux?alt=media"
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
            // More UTF-8
            new URLTestCase(
                    "https://user:secret@example♬.com/path/to/my/dir?search=one+two#about",
                    "https",
                    "user",
                    "secret",
                    "example♬.com",
                    "/path/to/my/dir",
                    "search=one+two",
                    "about",
                    "https://user:secret@example%E2%99%AC.com/path/to/my/dir?search=one+two#about"
            ),

            // Percent encoding in url path
            new URLTestCase(
                    "http://abc.net/1160x%3E/quality/",
                    "http",
                    null,
                    null,
                    "abc.net",
                    "/1160x>/quality/",
                    null,
                    null,
                    "http://abc.net/1160x%3E/quality/"
            ),

            // Percent encoding in url path
            new URLTestCase(
                    "http://db-engines.com/en/system/PostgreSQL%3BRocksDB",
                    "http",
                    null,
                    null,
                    "db-engines.com",
                    "/en/system/PostgreSQL;RocksDB",
                    null,
                    null,
                    "http://db-engines.com/en/system/PostgreSQL%3BRocksDB"
            ),

            // Percent encoding in url path
            new URLTestCase(
                    "http://xzy.org/test/hei%DFfl",
                    "http",
                    null,
                    null,
                    "xzy.org",
                    "/test/hei�fl",
                    null,
                    null,
                    "http://xzy.org/test/hei%DFfl"),

            // Percent encoding in url path
            new URLTestCase(
                    "http://www.net/decom/category/AA/A_%26_BBB/AAA_%26_BBB/",
                    "http",
                    null,
                    null,
                    "www.net",
                    "/decom/category/AA/A_&_BBB/AAA_&_BBB/",
                    null,
                    null,
                    "http://www.net/decom/category/AA/A_%26_BBB/AAA_%26_BBB/"
            ),

            // Percent encoding in url path
            new URLTestCase(
                    "https://en.wikipedia.org/wiki/Eat_one%27s_own_dog_food",
                    "https",
                    null,
                    null,
                    "en.wikipedia.org",
                    "/wiki/Eat_one's_own_dog_food",
                    null,
                    null,
                    "https://en.wikipedia.org/wiki/Eat_one%27s_own_dog_food"
            ),
    };

    private String[] toJavaClassCases = {
            "http://www.example.com",
            "http://www.example.com/path/to/my/file.html",
            "http://www.example.com/path/to/my/file.html?q=key/value",
            "http://www.example.com/path/to/my/file.html?q=key/value#fragment",
            "http://example/path/to/my/file?q=key/value#fragment",
            "http://example/path/to/my/file?q=http://testing/value#fragment",
            "https://username:password@host.com:8080/path/goes/here?search=for+this,and+this&another=true#fragment",
            "https://192.168.1.1:443",
            "http://[::1]:8080"
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

    private QueryStringTestCase[] queryStringCases = new QueryStringTestCase[]{
            new QueryStringTestCase(
                    "http://example.com",
                    Collections.emptyMap()
            ),
            new QueryStringTestCase(
                    "http://example.com?",
                    Collections.emptyMap()
            ),
            new QueryStringTestCase(
                    "http://example.com?key=value",
                    new HashMap<String, Collection<String>>() {{
                        put("key", Arrays.asList("value"));
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?key=",
                    new HashMap<String, Collection<String>>() {{
                        put("key", Collections.emptyList());
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos",
                    new HashMap<String, Collection<String>>() {{
                        put("one", Arrays.asList("uno"));
                        put("two", Arrays.asList("dos"));
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&",
                    new HashMap<String, Collection<String>>() {{
                        put("one", Arrays.asList("uno"));
                        put("two", Arrays.asList("dos"));
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&three=",
                    new HashMap<String, Collection<String>>() {{
                        put("one", Arrays.asList("uno"));
                        put("two", Arrays.asList("dos"));
                        put("three", Collections.emptyList());
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&three",
                    new HashMap<String, Collection<String>>() {{
                        put("one", Arrays.asList("uno"));
                        put("two", Arrays.asList("dos"));
                        put("three", Collections.emptyList());
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&three=tres&three=drei",
                    new HashMap<String, Collection<String>>() {{
                        put("one", Arrays.asList("uno"));
                        put("two", Arrays.asList("dos"));
                        put("three", Arrays.asList("tres", "drei"));
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&=",
                    new HashMap<String, Collection<String>>() {{
                        put("one", Arrays.asList("uno"));
                        put("two", Arrays.asList("dos"));
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&===",
                    new HashMap<String, Collection<String>>() {{
                        put("one", Arrays.asList("uno"));
                        put("two", Arrays.asList("dos"));
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&===&three=tres",
                    new HashMap<String, Collection<String>>() {{
                        put("one", Arrays.asList("uno"));
                        put("two", Arrays.asList("dos"));
                        put("three", Arrays.asList("tres"));
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&===&&=&&three=tres",
                    new HashMap<String, Collection<String>>() {{
                        put("one", Arrays.asList("uno"));
                        put("two", Arrays.asList("dos"));
                        put("three", Arrays.asList("tres"));
                    }}
            ),
            new QueryStringTestCase(
                    "http://example.com?one=uno&two=dos&===&&=&&three=tres#fragment=hello",
                    new HashMap<String, Collection<String>>() {{
                        put("one", Arrays.asList("uno"));
                        put("two", Arrays.asList("dos"));
                        put("three", Arrays.asList("tres"));
                    }}
            ),
    };

    @Test
    public void testUrls() throws Exception {
        for (URLTestCase testCase : urlTestCases) {
            URL url = URL.parse(testCase.input);
            Assert.assertEquals(testCase.expectedScheme, url.getScheme());
            Assert.assertEquals(testCase.expectedUsername, url.getUsername());
            Assert.assertEquals(testCase.expectedPassword, url.getPassword());
            Assert.assertEquals(testCase.expectedHost, url.getHost());
            Assert.assertEquals(testCase.expectedPath, url.getPath());
            Assert.assertEquals(testCase.expectedQuery, url.getQuery());
            Assert.assertEquals(testCase.expectedFragment, url.getFragment());
            Assert.assertEquals(testCase.expectedStringRepr, url.toString());
        }
    }

    @Test
    public void testToURL() throws Exception {
        for (String testCase : toJavaClassCases) {
            URL url = URL.parse(testCase);
            java.net.URL javaURL = url.toJavaURL();
            Assert.assertEquals(testCase, javaURL.toString());
        }
    }


    @Test
    public void testToURI() throws Exception {
        for (String testCase : toJavaClassCases) {
            URL url = URL.parse(testCase);
            java.net.URI javaURI = url.toJavaURI();
            Assert.assertEquals(testCase, javaURI.toString());
        }
    }

    @Test
    public void testQueryStringValues() throws Exception {
        for (QueryStringTestCase testCase : queryStringCases) {
            URL url = URL.parse(testCase.input);
            Assert.assertEquals(testCase.expected, url.getQueryPairs());
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
    public void testIPv6WithoutClosingtag() throws MalformedURLException {
        URL url = URL.parse("http://[::1");
    }

    @Test(expected = MalformedURLException.class)
    public void testIPv6InvalidPort() throws MalformedURLException {
        URL url = URL.parse("http://[::1]:123abc");
    }

    @Test(expected = MalformedURLException.class)
    public void testIPv6InvalidPort2() throws MalformedURLException {
        URL url = URL.parse("http://[::1]:");
    }

    @Test
    public void testCachedQueryPairs() throws MalformedURLException {
        URL url = URL.parse("http://example.com?one=uno&two=dos&three");
        Map<String, Collection<String>> expected = new HashMap<String, Collection<String>>() {{
            put("one", Arrays.asList("uno"));
            put("two", Arrays.asList("dos"));
            put("three", Collections.emptyList());
        }};

        Map<String, Collection<String>> first = url.getQueryPairs();
        // Second time, it should come from a cached value
        Map<String, Collection<String>> second = url.getQueryPairs();

        // Test expected values
        Assert.assertEquals(expected, first);

        // Test referential equality
        Assert.assertTrue(first == second);
    }

    @Test
    public void testIsPortValid() throws MalformedURLException {
        Assert.assertFalse(new DefaultURLParser().isPortValid("12345"));
        Assert.assertFalse(new DefaultURLParser().isPortValid("abcde"));
        Assert.assertFalse(new DefaultURLParser().isPortValid(":abcde"));
        Assert.assertFalse(new DefaultURLParser().isPortValid(":"));
        Assert.assertFalse(new DefaultURLParser().isPortValid(":::"));
        Assert.assertFalse(new DefaultURLParser().isPortValid("123:456"));

        Assert.assertTrue(new DefaultURLParser().isPortValid(":1234"));
        Assert.assertTrue(new DefaultURLParser().isPortValid(":888888"));
        Assert.assertTrue(new DefaultURLParser().isPortValid(":443"));
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
        Assert.assertTrue(URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section").isAbsolute());
        Assert.assertTrue(URL.parse("http://domain.com").isAbsolute());
        Assert.assertTrue(URL.parse("http://www.domain.com?key=value").isAbsolute());
        Assert.assertTrue(URL.parse("http://192.168.0.1/path/to/RESOURCE.html?q=abc#section").isAbsolute());
        Assert.assertTrue(URL.parse("http://192.168.0.1").isAbsolute());
        Assert.assertTrue(URL.parse("file:///home/user").isAbsolute());
        Assert.assertFalse(URL.parse("///home/user").isAbsolute());
        Assert.assertFalse(URL.parse("/home/user").isAbsolute());
        Assert.assertFalse(URL.parse("home/user").isAbsolute());
        Assert.assertFalse(URL.parse("home").isAbsolute());
    }

    @Test
    public void testResolveReferences() throws Exception {
        for (URLReferenceTestCase testCase : resolveReferenceCases) {
            URL base = URL.parse(testCase.inputBase);
            URL urlRef = URL.parse(testCase.inputReference);

            URL resolvedUrlRef = base.resolveReference(urlRef);
            URL resolvedStringRef = base.resolveReference(testCase.inputReference);

            Assert.assertEquals(testCase.expectedResolvedReference, resolvedUrlRef.toString());
            Assert.assertEquals(testCase.expectedResolvedReference, resolvedStringRef.toString());
        }
    }


    @Test
    public void testHashCode() throws Exception {
        URL urlA = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        URL urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        Assert.assertTrue(urlA.hashCode() == urlB.hashCode());

        urlA = URL.parse("http://www.domain.com");
        urlB = URL.parse("http://www.domain.com/");
        Assert.assertFalse(urlA.hashCode() == urlB.hashCode());

        urlA = URL.parse("http://www.domain.com");
        urlB = URL.parse("http://www.domain.de");
        Assert.assertFalse(urlA.hashCode() == urlB.hashCode());
    }

    @Test(expected = InvalidURLReferenceException.class)
    public void testInvalidResolveNotAbsoluteBase() throws Exception {
        URL base = URL.parse("/path/to/RESOURCE.html");
        URL ref = URL.parse("http://www.domain.com/path/to/ANOTHER_RESOURCE.html?q=abc#section");
        base.resolveReference(ref);
    }

    @Test(expected = InvalidURLReferenceException.class)
    public void testInvalidResolveNullRef() throws Exception {
        URL base = URL.parse("http://www.domain.com/path");
        URL ref = null;
        base.resolveReference(ref);
    }

    @Test(expected = MalformedURLException.class)
    public void testMissingScheme() throws Exception {
        URL url = URL.parse(":http://www.domain.com/path");
    }


    @Test
    public void testRoundtrip() throws Exception {
        URL url = URL.parse("http://www.domain.com/?");
        Assert.assertEquals("http://www.domain.com/?", url.toString());
    }

    @Test
    public void testEquals() throws Exception {
        URL urlA = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        URL urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        Assert.assertTrue(urlA.equals(urlB));

        urlA = URL.parse("http://domain.com/path/to/RESOURCE.html?q=abc#section");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        Assert.assertFalse(urlA.equals(urlB));

        urlA = URL.parse("http://www.DOMAIN.com/path/to/RESOURCE.html?q=abc#section");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        Assert.assertTrue(urlA.equals(urlB));

        urlA = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc#section");
        Assert.assertFalse(urlA.equals(urlB));

        urlA = URL.parse("http://domain.com/path/to/RESOURCE.html?q=abc");
        urlB = URL.parse("http://domain.com/path/to/RESOURCE.html?q=abc#");
        Assert.assertTrue(urlA.equals(urlB));

        urlA = URL.parse("http://www.domain.de/path/to/RESOURCE.html?q=abc");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        Assert.assertFalse(urlA.equals(urlB));

        urlA = URL.parse("http://www.domain.com:8080/path/to/RESOURCE.html?q=abc");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        Assert.assertFalse(urlA.equals(urlB));

        urlA = URL.parse("http://www.domain.com/path/to/ANOTHER.html?q=abc");
        urlB = URL.parse("http://www.domain.com/path/to/RESOURCE.html?q=abc");
        Assert.assertFalse(urlA.equals(urlB));

        Assert.assertFalse(urlA == null);
        Assert.assertFalse(urlA.equals(null));
        Assert.assertFalse("http://www.domain.com/path/to/ANOTHER.html?q=abc".equals(urlA));
        Assert.assertFalse(new Integer(123).equals(urlA));

        urlA = URL.parse("8080");
        Assert.assertFalse(urlA.equals(8080));
        Assert.assertFalse("8080".equals(urlA));
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
        public Map<String, Collection<String>> expected;

        public QueryStringTestCase(String input, Map<String, Collection<String>> expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    private class ToJavaClassTestCase {
        public String input;
        public String expected;

        public ToJavaClassTestCase(String input, String expected) {
            this.input = input;
            this.expected = expected;
        }
    }
}