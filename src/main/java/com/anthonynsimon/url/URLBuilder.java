package com.anthonynsimon.url;

/**
 * URLBuilder is a helper class for the construction of a URL object.
 */
final class URLBuilder {
    private String scheme;
    private String username;
    private String password;
    private String host;
    private String path;
    private String query;
    private String fragment;
    private String opaque;

    public URL build() {
        return new URL(scheme, username, password, host, path, query, fragment, opaque);
    }

    public URLBuilder setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public URLBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public URLBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public URLBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public URLBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public URLBuilder setQuery(String query) {
        this.query = query;
        return this;
    }

    public URLBuilder setFragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    public URLBuilder setOpaque(String opaque) {
        this.opaque = opaque;
        return this;
    }
}
