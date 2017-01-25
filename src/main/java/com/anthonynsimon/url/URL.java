package com.anthonynsimon.url;

public class URL {
    private String protocol;
    private String username;
    private String password;
    private String host;
    private Integer port;
    private String path;
    private String query;
    private String fragment;
    private String stringRepr;

    // TODO: switch to parse method and builder pattern for constructor or setters
    public URL(String rawUrl) throws MalformedURLException {
        // TODO: escape rawUrl first!
        parse(rawUrl);
    }

    public String normalized() {
        // TODO: implement this
        return null;
    }

    private void parse(String rawUrl) throws MalformedURLException {
        int cursor = 0;
        String remaining = rawUrl;

        // Find the protocol
        cursor = remaining.indexOf("://");
        if (cursor > 0) {
            protocol = remaining.substring(0, cursor);
            remaining = remaining.substring(cursor + 3, remaining.length());
        }

        // Find the username and password
        cursor = remaining.indexOf("@");
        if (cursor > 0) {
            String credentials = remaining.substring(0, cursor);
            String[] parts = credentials.split(":");
            if (parts.length > 2) {
                throw new MalformedURLException("invalid credentials string");
            } else if (parts.length == 2) {
                password = parts[1];
            }
            username = parts[0];
            remaining = remaining.substring(cursor + 1, remaining.length());
        }

        // Find the fragment
        // TODO: handle query after fragment (bad order case)
        cursor = remaining.indexOf("#");
        if (cursor > 0) {
            String fragmentPart = remaining.substring(cursor + 1, remaining.length());
            if (!fragmentPart.isEmpty()) {
                fragment = fragmentPart;
            }
            remaining = remaining.substring(0, cursor);
        }

        // Find the query string
        cursor = remaining.indexOf("?");
        if (cursor > 0) {
            String queryPart = remaining.substring(cursor + 1, remaining.length());
            if (!queryPart.isEmpty()) {
                query = queryPart;
            }
            remaining = remaining.substring(0, cursor);
        }

        // Find the path
        cursor = remaining.indexOf("/");
        if (cursor > 0) {
            path = remaining.substring(cursor, remaining.length());
            remaining = remaining.substring(0, cursor);
        } else {
            path = "/";
        }

        // Find the host and port
        String[] parts = remaining.split(":");
        if (parts.length > 2) {
            throw new MalformedURLException("malformed url: " + rawUrl);
        }
        String hostPart = parts[0];
        if (hostPart == null || hostPart == "" || hostPart.split(".", -1).length < 2) {
            throw new MalformedURLException("invalid host: " + hostPart);

        }
        host = hostPart.toLowerCase();
        if (parts.length == 2) {
            try {
                Integer portPart = Integer.valueOf(parts[1]);
                port = portPart;
            } catch (NumberFormatException e) {
                throw new MalformedURLException("invalid port: " + parts[1]);
            }
        }


    }

    public String protocol() {
        return protocol;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String host() {
        return host;
    }

    public Integer port() {
        return port;
    }

    public String path() {
        return path;
    }

    public String query() {
        return query;
    }

    public String fragment() {
        return fragment;
    }

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

    @Override
    public String toString() {
        if (stringRepr != null) {
            return stringRepr;
        }
        String result = protocol + "://";
        if (username != null) {
            result += username;
            if (password != null) {
                result += ":" + password;
            }
        }
        result += host;
        if (port != null) {
            result += ":" + port.toString();
        }
        result += path;
        if (query != null) {
            result += "?" + query;
        }
        if (fragment != null) {
            result += "#" + fragment;
        }
        stringRepr = result;
        return result;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
