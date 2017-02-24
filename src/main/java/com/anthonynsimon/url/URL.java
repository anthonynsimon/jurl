package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.InvalidURLReferenceException;
import com.anthonynsimon.url.exceptions.MalformedURLException;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * URL is a reference to a web resource. This class implements functionality for parsing and
 * manipulating the various parts that make up a URL.
 * <p>
 * Once parsed it is of the form:
 * <p>
 * scheme:[//[user:password@]host[:port]][/]path[?query][#fragment]
 */
public final class URL implements Serializable {

    /**
     * Unique ID for serialization purposes.
     */
    static final long serialVersionUID = 80443L;

    /**
     * URLParser to be used to parse the URL string into the URL object.
     * Do not serialize.
     */
    private transient final static URLParser URL_PARSER = new DefaultURLParser();

    private final String scheme;
    private final String username;
    private final String password;
    private final String host;
    private final String path;
    private final String query;
    private final String fragment;
    private final String opaque;

    /**
     * Cached parsed query string key-value pairs.
     * Do not serialize.
     */
    private transient Map<String, String> parsedQueryPairs;

    /**
     * Cached string representation of the URL.
     * Do not serialize.
     */
    private transient String stringRepresentation;

    /**
     * Protect instantiation of class. Use public parse method instead to construct URLs.
     * Builder is for protected use only.
     */
    URL(String scheme, String username, String password, String host, String path, String query, String fragment, String opaque) {
        this.scheme = mapToNullIfEmpty(scheme);
        this.username = mapToNullIfEmpty(username);
        this.password = mapToNullIfEmpty(password);
        this.host = mapToNullIfEmpty(host);
        this.path = mapToNullIfEmpty(path);
        this.query = mapToNullIfEmpty(query);
        this.fragment = mapToNullIfEmpty(fragment);
        this.opaque = mapToNullIfEmpty(opaque);
    }

    /**
     * Returns a new URL object after parsing the provided URL string.
     */
    public static URL parse(String url) throws MalformedURLException {
        return URL_PARSER.parse(url);
    }

    private String mapToNullIfEmpty(String str) {
        return str != null && !str.isEmpty() ? str : null;
    }

    /**
     * Returns the scheme ('http' or 'file' or 'ftp' etc...) of the URL if it exists.
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Returns the username part of the userinfo if it exists.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password part of the userinfo if it exists.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the host ('www.example.com' or '192.168.0.1:8080' or '[fde2:d7de:302::]') of the URL if it exists.
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the path ('/path/to/my/file.html') of the URL if it exists.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the query ('?q=foo{@literal &}bar') of the URL if it exists.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Returns the fragment ('#foo{@literal &}bar') of the URL if it exists.
     */
    public String getFragment() {
        return fragment;
    }

    /**
     * Returns the opaque part of the URL if it exists.
     */
    public String getOpaque() {
        return opaque;
    }

    /**
     * Returns a java.net.URL object from the parsed url.
     *
     * @throws java.net.MalformedURLException if something went wrong while created the new object.
     */
    public java.net.URL toJavaURL() throws java.net.MalformedURLException {
        return new java.net.URL(toString());
    }

    /**
     * Returns a java.net.URI object from the parsed url.
     *
     * @throws java.net.URISyntaxException if something went wrong while created the new object.
     */
    public java.net.URI toJavaURI() throws URISyntaxException {
        return new URI(toString());
    }


    /**
     * Returns a map of key-value pairs from the parsed query string.
     */
    public Map<String, String> getQueryPairs() {
        if (parsedQueryPairs != null) {
            return parsedQueryPairs;
        }
        parsedQueryPairs = new HashMap<>();

        if (!nullOrEmpty(query)) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] parts = pair.split("=");
                if (parts.length == 2 && !parts[0].isEmpty()) {
                    parsedQueryPairs.put(parts[0], parts[1]);
                }
            }
        }

        return parsedQueryPairs;
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
        return toString().equals(other.toString());
    }

    /**
     * Returns a string representation of the all parts of the URL that are not null.
     */
    @Override
    public String toString() {
        if (stringRepresentation != null) {
            return stringRepresentation;
        }

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
                    sb.append(PercentEncoder.encode(username, URLPart.CREDENTIALS));
                    if (!nullOrEmpty(password)) {
                        sb.append(":");
                        sb.append(PercentEncoder.encode(password, URLPart.CREDENTIALS));
                    }
                    sb.append("@");
                }
                if (!nullOrEmpty(host)) {
                    sb.append(PercentEncoder.encode(host, URLPart.HOST));
                }
            }
            if (!nullOrEmpty(path)) {
                if (!path.startsWith("/") && !path.equals("*")) {
                    sb.append("/");
                }
                sb.append(PercentEncoder.encode(path, URLPart.PATH));
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

        stringRepresentation = sb.toString();

        return stringRepresentation;
    }

    /**
     * Returns the hash code of the URL.
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
        URL url = URL_PARSER.parse(ref);
        return resolveReference(url);
    }

    /**
     * Returns the resolved reference URL using the instance URL as a base.
     * <p>
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

        URLBuilder builder = new URLBuilder()
                .setScheme(ref.getScheme())
                .setUsername(ref.getUsername())
                .setPassword(ref.getPassword())
                .setHost(ref.getHost())
                .setPath(ref.getPath())
                .setQuery(ref.getQuery())
                .setFragment(ref.getFragment())
                .setOpaque(ref.getOpaque());

        if (!ref.isAbsolute()) {
            builder.setScheme(scheme);
        }

        if (!nullOrEmpty(ref.scheme) || !nullOrEmpty(ref.host)) {
            builder.setPath(PathResolver.resolve(ref.path, ""));
            return builder.build();
        }

        if (ref.isOpaque() || isOpaque()) {
            return builder.build();
        }

        return builder
                .setHost(host)
                .setUsername(username)
                .setPassword(password)
                .setPath(PathResolver.resolve(path, ref.path))
                .build();
    }

}
