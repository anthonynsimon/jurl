package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.MalformedURLException;

/**
 * URL represents a parsed URL string and is of the form:
 * <p>
 * scheme://username:password@host:port/path?query#fragment
 */
public class URL {
    // TODO: handle absolute and relative references
    // TODO: handle path resolving

    private String scheme;
    private String username;
    private String password;
    private String host;
    private String path;
    private String query;
    private String fragment;
    private String opaque;

    /**
     * Returns a new URL object after parsing the provided URL string.
     *
     * @param url is a string with the url to be parsed.
     * @throws MalformedURLException if one or more errors occur during parsing.
     */
    public static URL parse(String url) throws MalformedURLException {
        URL u = new URL();
        u.parseAll(url);
        return u;
    }

    /**
     * Parses all the URL parts from the provided string.
     * <p>
     * scheme://username:password@host:port/path?query#fragment
     *
     * @throws MalformedURLException if one or more errors occur during parsing.
     */
    private void parseAll(String rawUrl) throws MalformedURLException {
        if (rawUrl == null) {
            throw new MalformedURLException("url is empty");
        }

        String remaining = rawUrl;

        int index = remaining.lastIndexOf("#");
        if (index > 0) {
            String frag = remaining.substring(index + 1, remaining.length());
            fragment = frag.isEmpty() ? null : frag;
            remaining = remaining.substring(0, index);
        }

        if (remaining.isEmpty()) {
            throw new MalformedURLException("invalid url");
        }

        if (remaining.equals("*")) {
            path = "*";
            return;
        }

        index = remaining.indexOf("?");
        if (index > 0) {
            String qr = remaining.substring(index + 1, remaining.length());
            if (!qr.isEmpty()) {
                query = qr;
            }
            remaining = remaining.substring(0, index);
        }

        remaining = parseScheme(remaining);

        if (scheme != null && !scheme.isEmpty()) {
            if (!remaining.startsWith("/")) {
                opaque = remaining;
                return;
            }
        }
        if (((scheme != null && !scheme.isEmpty()) || !remaining.startsWith("///")) && remaining.startsWith("//")) {
            remaining = remaining.substring(2, remaining.length());
            int i = remaining.indexOf("/");
            if (i >= 0) {
                parseAuthority(remaining.substring(0, i));
                remaining = remaining.substring(i, remaining.length());
            } else {
                parseAuthority(remaining);
                remaining = "";
            }
        }

        if (!remaining.isEmpty()) {
            path = EscapeUtils.unescape(remaining);
        }
    }

    /**
     * Parses the scheme from the provided string.
     * <p>
     * [SCHEME]://username:password@host:port/path?query#fragment
     *
     * @throws MalformedURLException if one or more errors occur during parsing.
     */
    private String parseScheme(String remaining) throws MalformedURLException {
        for (int i = 0; i < remaining.length(); i++) {
            char c = remaining.charAt(i);
            if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z') {
                continue;
            } else if (c == ':') {
                if (i == 0) {
                    throw new MalformedURLException("missing scheme");
                }
                scheme = remaining.substring(0, i).toLowerCase();
                remaining = remaining.substring(i + 1, remaining.length());
                return remaining;
            } else if ('0' <= c && c <= '9' || c == '+' || c == '-' || c == '.') {
                if (i == 0) {
                    throw new MalformedURLException("bad scheme format");
                }
            }
        }
        return remaining;
    }

    /**
     * Parses the authority from the provided string.
     * <p>
     * scheme://[AUTHORITY]/path?query#fragment
     *
     * @throws MalformedURLException if one or more errors occur during parsing.
     */
    private void parseAuthority(String authority) throws MalformedURLException {
        int i = authority.lastIndexOf('@');
        if (i >= 0) {
            String credentials = authority.substring(0, i);
            if (credentials.contains(":")) {
                String[] parts = credentials.split(":", 2);
                username = EscapeUtils.unescape(parts[0]);
                password = EscapeUtils.unescape(parts[1]);
            } else {
                username = EscapeUtils.unescape(credentials);
            }
            authority = authority.substring(i + 1, authority.length());
        }
        parseHost(authority);
    }

    /**
     * Parses the host from the provided string.
     * <p>
     * scheme://username:password@[HOST]/path?query#fragment
     *
     * @throws MalformedURLException if one or more errors occur during parsing.
     */
    private void parseHost(String str) throws MalformedURLException {
        if (str.startsWith("[")) {
            int i = str.lastIndexOf("]");
            if (i < 0) {
                throw new MalformedURLException("IPv6 detected, but missing closing ']' token");
            }
            String portPart = str.substring(i + 1, str.length());
            if (!isPortValid(portPart)) {
                throw new MalformedURLException("invalid port");
            }
        } else {
            String[] parts = str.split(":", -1);
            if (parts.length > 2) {
                throw new MalformedURLException("invalid host: " + parts.toString());
            }
            if (parts.length == 2) {
                try {
                    Integer.valueOf(parts[1]);
                } catch (NumberFormatException e) {
                    throw new MalformedURLException("invalid port");
                }
            }
        }
        String ht = EscapeUtils.unescape(str.toLowerCase());
        if (!ht.isEmpty()) {
            host = ht;
        }
    }

    /**
     * Returns true if the provided port string contains a valid port number.
     * Note that an empty string is a valid port number since it's optional.
     * <p>
     * For example:
     * <p>
     * ''      => TRUE
     * null    => TRUE
     * ':8080' => TRUE
     * ':ab80' => FALSE
     * ':abc'  => FALSE
     */
    private boolean isPortValid(String portStr) {
        if (portStr == null || portStr.isEmpty()) {
            return true;
        }
        int i = portStr.indexOf(":");
        if (i < 0) {
            return false;
        }
        portStr = portStr.substring(i + 1, portStr.length());
        try {
            Integer.valueOf(portStr);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
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
     *
     * @param other is the Object to compare with.
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
        if (scheme != null && !scheme.isEmpty()) {
            result += scheme + ":";
        }
        if (opaque != null) {
            result += opaque;
        } else {
            if (scheme != null || host != null) {
                result += "//";
                if (username != null) {
                    result += EscapeUtils.escape(username, URLPart.CREDENTIALS);
                    if (password != null) {
                        result += ":" + EscapeUtils.escape(password, URLPart.CREDENTIALS);
                    }
                    result += "@";
                }
                if (host != null) {
                    result += EscapeUtils.escape(host, URLPart.HOST);
                }
            }
            if (path != null) {
                result += EscapeUtils.escape(path, URLPart.PATH);
            }
        }
        if (query != null) {
            result += "?" + query;
        }
        if (fragment != null) {
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

    protected enum URLPart {
        CREDENTIALS,
        HOST,
        PATH,
        QUERY,
        FRAGMENT,
        ENCODE_ZONE,
    }
}
