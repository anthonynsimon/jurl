package com.anthonynsimon.url;

class URLBuilder {
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

    public String getScheme() {
        return scheme;
    }

    public URLBuilder setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public URLBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public URLBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getHost() {
        return host;
    }

    public URLBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public String getPath() {
        return path;
    }

    public URLBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public URLBuilder setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getFragment() {
        return fragment;
    }

    public URLBuilder setFragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    public String getOpaque() {
        return opaque;
    }

    public URLBuilder setOpaque(String opaque) {
        this.opaque = opaque;
        return this;
    }
}
