package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.MalformedURLException;

/**
 * URL is a reference to a web resource. This class implements functionality for parsing and
 * manipulating the various parts that make up a URL.
 *
 * Once parsed it is of the form:
 *
 * scheme:[//[user:password@]host[:port]][/]path[?query][#fragment]
 */
public class URL {
    // TODO: handle absolute and relative references
    // TODO: handle path resolving

    protected String scheme;
    protected String username;
    protected String password;
    protected String host;
    protected String path;
    protected String query;
    protected String fragment;
    protected String opaque;

    /**
     * Returns a new URL object after parsing the provided URL string.
     */
    public static URL parse(String url) throws MalformedURLException {
        URL u = new URL();
        return Parser.parse(url, u);
    }

    /**
     * Returns the scheme ('http' or 'file' or 'ftp' etc...) of the URL if it exists.
     */
    public String scheme() {
        return scheme;
    }

    /**
     * Returns the username part of the userinfo if it exists.
     */
    public String username() {
        return username;
    }

    /**
     * Returns the password part of the userinfo if it exists.
     */
    public String password() {
        return password;
    }

    /**
     * Returns the host ('www.example.com' or '192.168.0.1:8080' or '[fde2:d7de:302::]') of the URL if it exists.
     */
    public String host() {
        return host;
    }

    /**
     * Returns the path ('/path/to/my/file.html') of the URL if it exists.
     */
    public String path() {
        return path;
    }

    /**
     * Returns the query ('?q=foo&bar') of the URL if it exists.
     */
    public String query() {
        return query;
    }

    /**
     * Returns the fragment ('#foo&bar') of the URL if it exists.
     */
    public String fragment() {
        return fragment;
    }

    /**
     * Returns true if the two Objects are instances of URL and their string representations match.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof URL)) {
            return false;
        }
        URL otherURL = (URL) other;
        return toString().equals(other.toString());
    }

    /**
     * Returns a string representation of the all parts of the URL that are not null.
     */
    @Override
    public String toString() {
        String result = "";
        if (notNullNotEmpty(scheme)) {
            result += scheme + ":";
        }
        if (notNullNotEmpty(opaque)) {
            result += opaque;
        } else {
            if (notNullNotEmpty(scheme) || notNullNotEmpty(host)) {
                result += "//";
                if (notNullNotEmpty(username)) {
                    result += PercentEscaper.escape(username, URLPart.CREDENTIALS);
                    if (notNullNotEmpty(password)) {
                        result += ":" + PercentEscaper.escape(password, URLPart.CREDENTIALS);
                    }
                    result += "@";
                }
                if (notNullNotEmpty(host)) {
                    result += PercentEscaper.escape(host, URLPart.HOST);
                }
            }
            if (notNullNotEmpty(path)) {
                result += PercentEscaper.escape(path, URLPart.PATH);
            }
        }
        if (notNullNotEmpty(query)) {
            result += "?" + query;
        }
        if (notNullNotEmpty(fragment)) {
            result += "#" + fragment;
        }
        return result;
    }

    /**
     * Returns the hashCode of the string representation of the URL.
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Returns true if the parameter string is neither null nor empty ("").
     */
    private boolean notNullNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public boolean isAbsolute() {
        return notNullNotEmpty(scheme);
    }

}
