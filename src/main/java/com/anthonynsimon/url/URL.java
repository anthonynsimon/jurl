package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.InvalidURLReferenceException;
import com.anthonynsimon.url.exceptions.MalformedURLException;

import java.io.Serializable;

/**
 * URL is a reference to a web resource. This class implements functionality for parsing and
 * manipulating the various parts that make up a URL.
 *
 * Once parsed it is of the form:
 *
 * scheme:[//[user:password@]host[:port]][/]path[?query][#fragment]
 */
public class URL implements Serializable {
    // TODO: add support to build an URL manually
    // TODO: add support to parse query string

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
        StringBuffer sb = new StringBuffer();
        if (!nullOrEmpty(scheme)) {
            sb.append(scheme);
            sb.append(":");
        }
        if (!nullOrEmpty(opaque)) {
            sb.append(opaque);
        } else {
            if (!nullOrEmpty(scheme) || !nullOrEmpty(host)) {
                sb.append("//");
                if (!nullOrEmpty(username)) {
                    sb.append(PercentEscaper.escape(username, URLPart.CREDENTIALS));
                    if (!nullOrEmpty(password)) {
                        sb.append(":");
                        sb.append(PercentEscaper.escape(password, URLPart.CREDENTIALS));
                    }
                    sb.append("@");
                }
                if (!nullOrEmpty(host)) {
                    sb.append(PercentEscaper.escape(host, URLPart.HOST));
                }
            }
            if (!nullOrEmpty(path)) {
                if (!path.startsWith("/")) {
                    sb.append("/");
                }
                sb.append(PercentEscaper.escape(path, URLPart.PATH));
            }
        }
        if (!nullOrEmpty(query)) {
            sb.append("?");
            sb.append(query);
        }
        if (!nullOrEmpty(fragment)) {
            sb.append("#");
            sb.append(fragment);
        }
        return sb.toString();
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
    private boolean nullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Returns true if URL is opaque.
     */
    public boolean isOpaque() {
        return !nullOrEmpty(opaque);
    }

    /**
     * Returns true if URL is absolute.
     */
    public boolean isAbsolute() {
        return !nullOrEmpty(scheme);
    }

    public URL resolveReference(String ref) throws MalformedURLException, InvalidURLReferenceException {
        URL url = new URL();
        Parser.parse(ref, url);
        return resolveReference(url);
    }

    /**
     * Returns the resolved reference URL using the instance URL as a base.
     *
     * If the reference URL is absolute, then it simply creates a new URL that is identical to it
     * and returns it. If the reference and the base URLs are identical, a new instance of the reference is returned.
     *
     * @throws InvalidURLReferenceException if the provided ref URL is invalid or if the base URL is not absolute.
     */
    public URL resolveReference(URL ref) throws InvalidURLReferenceException {
        if (!isAbsolute()) {
            throw new InvalidURLReferenceException("base url is not absolute");
        }
        if (ref == null) {
            throw new InvalidURLReferenceException("reference url is null");
        }

        URL target;
        try {
            target = URL.parse(ref.toString());
        } catch (MalformedURLException e) {
            throw new InvalidURLReferenceException("reference url is invalid");
        }

        if (isOpaque()) {
            return target;
        }
        if (!ref.isAbsolute()) {
            target.scheme = scheme;
        }
        if (nullOrEmpty(ref.host())) {
            target.host = host;
        }

        // Case for base=http://host.com/one/two and ref=three => http://host.com/one/three
        if (!nullOrEmpty(ref.path) && !nullOrEmpty(path) && !path.equals("/") && !ref.path.startsWith("/")) {
            String[] parts = path.split("/");
            parts[parts.length - 1] = ref.path;
            StringBuilder sb = new StringBuilder();
            for (String part : parts) {
                if (part.isEmpty()) {
                    continue;
                }
                sb.append("/");
                sb.append(part);
            }
            target.path = sb.toString();
        }

        return target;

        // TODO: handle path relative references. i.e. '.' '..' './..' '../../../here'
        // TODO: handle opaque references and base
    }

}
